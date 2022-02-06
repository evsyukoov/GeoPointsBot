package ggsbot.states;

import com.mchange.v2.cfg.PropertiesConfigSource;
import ggsbot.constants.Messages;
import ggsbot.errors.ParseCoordsException;
import ggsbot.errors.YandexMapException;
import ggsbot.handlers.Coordinate;
import ggsbot.model.data.Client;
import ggsbot.model.data.Point;
import ggsbot.service.*;
import ggsbot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendInvoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationBotState implements BotState {

    private static final Logger logger = LoggerFactory.getLogger(LocationBotState.class);

    private final PointsService pointsService;

    private final FileService fileService;

    private final ClientService clientService;

    private final SettingsKeyboardService settingsKeyboardService;

    private final YandexMapService yandexMapService;

    private String yandexMapLink;

    private String providerPaymentToken;

    @Value("${yandex.map.link}")
    public void setYandexMapLink(String yandexMapLink) {
        this.yandexMapLink = yandexMapLink;
    }

    @Value("${payment.provider.token}")
    public void setProviderPaymentToken(String providerPaymentToken) {
        this.providerPaymentToken = providerPaymentToken;
    }

    @Autowired
    public LocationBotState(PointsService pointsService, FileService fileService,
                            ClientService clientService, SettingsKeyboardService settingsKeyboardService,
                            YandexMapService yandexMapService) {
        this.pointsService = pointsService;
        this.fileService = fileService;
        this.clientService = clientService;
        this.settingsKeyboardService = settingsKeyboardService;
        this.yandexMapService = yandexMapService;
    }

    @Override
    public State getState() {
        return State.LOCATION;
    }

    @Override
    public List<PartialBotApiMethod<?>> handleMessage(Client client, Update update) {
        if (isLocationReceived(update) || isCoordinatesReceived(update)) {
            Coordinate coordinate;
            try {
                coordinate = getCoord(update);
            } catch (Exception e) {
                return processClientInputError(client, e);
            }
            List<Point> points = pointsService.getPoints(coordinate.getLat(), coordinate.getLon(), client);
            try {
                clientService.incrementCount(client);
                if (!points.isEmpty()) {
                    List<PartialBotApiMethod<?>> result = fileService.formFiles(points, client)
                            .stream()
                            .map(f -> initSendDocument(f, client))
                            .collect(Collectors.toList());
                    result.add(Utils.initMessage(client, Messages.FILES_READY, Messages.SEND_LOCATION));
                    return result;
                }
                return List.of(Utils.initMessage(client, Messages.POINTS_NOT_FOUND, Messages.SEND_LOCATION));
            } catch (IOException e) {
                logger.error("Проблемы при формировании файлов", e);
            }
        } else if (isSettingsReceived(update)) {
            clientService.incrementClientState(client);
            return List.of(settingsKeyboardService.initInlineKeyboard(client));
        } else if (isDonateReceived(update)) {
            clientService.updateClientState(client, State.DONATE);
            return List.of(initDonate(client));
        }
        else {
            return List.of(Utils.initMessage(client, Messages.INCORRECT_INPUT, Messages.SEND_LOCATION));
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<?>> processClientInputError(Client client, Exception e) {
        if (e instanceof ParseCoordsException) {
            return List.of(Utils.initMessage(client, Messages.WRONG_TEXT_COORDS));
        } else if (e instanceof YandexMapException) {
            return List.of(Utils.initMessage(client, Messages.WRONG_YANDEX_MAP_LINK));
        } else {
            return Collections.emptyList();
        }
    }

    private boolean isTextInfoReceived(Update update) {
        return update.getMessage() != null && update.getMessage().getText() != null;
    }

    private boolean isLocationReceived(Update update) {
        return update.getMessage() != null && update.getMessage().getLocation() != null;
    }

    private boolean isCoordinatesReceived(Update update) {
        return isTextInfoReceived(update) &&
                !update.getMessage().getText().equals(Messages.SETTINGS) &&
                !update.getMessage().getText().equals(Messages.DONATE);
    }

    private boolean isYandexMapLinkReceived(Update update) {
        return update.getMessage().getText().contains(yandexMapLink);
    }

    private Coordinate getCoord(Update update) {
        Coordinate coordinate = new Coordinate();
        if (isCoordinatesReceived(update)) {
            if (isYandexMapLinkReceived(update)) {
                return yandexMapService.getCoordsFromLink(update.getMessage().getText());
            } else {
                return parseCoordsFromText(update.getMessage().getText());
            }
        } else if (isLocationReceived(update)) {
            coordinate.setLat(update.getMessage().getLocation().getLatitude());
            coordinate.setLon(update.getMessage().getLocation().getLongitude());
        }
        return coordinate;
    }

    private Coordinate parseCoordsFromText(String text) {
        Coordinate coordinate = new Coordinate();
        String[] arr = text.split(";");
        if (arr.length != 2) {
            throw new ParseCoordsException();
        }
        try {
            coordinate.setLat(Double.parseDouble(arr[0].trim()));
            coordinate.setLon(Double.parseDouble(arr[1].trim()));
        } catch (Exception e) {
            throw new ParseCoordsException();
        }
        return coordinate;
    }

    private boolean isSettingsReceived(Update update) {
        return isTextInfoReceived(update) &&
                update.getMessage().getText().equals(Messages.SETTINGS);
    }

    private boolean isDonateReceived(Update update) {
        return isTextInfoReceived(update) &&
                update.getMessage().getText().equals(Messages.DONATE);
    }

    private SendDocument initSendDocument(File file, Client client) {
        return SendDocument.builder()
                .chatId(String.valueOf(client.getId()))
                .document(new InputFile(file, Utils.getResultFileName(file)))
                .build();

    }

    private SendInvoice initDonate(Client client) {
        SendInvoice invoice = new SendInvoice();
        invoice.setChatId(String.valueOf(client.getId()));
        invoice.setCurrency("RUB");
        invoice.setDescription("Поддержать разработчика");
        invoice.setPrices(List.of(LabeledPrice.builder()
                .amount(10000).label("Руб").build()));
        invoice.setPayload("Перевести 100 рублей");
        invoice.setProviderToken(providerPaymentToken);
        invoice.setTitle("Разработчику на кофе");
        invoice.setAllowSendingWithoutReply(true);
        invoice.setIsFlexible(false);
        return invoice;
    }

}
