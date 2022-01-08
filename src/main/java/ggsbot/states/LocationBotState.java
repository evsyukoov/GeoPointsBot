package ggsbot.states;

import ggsbot.constants.Messages;
import ggsbot.model.data.Client;
import ggsbot.model.data.Point;
import ggsbot.service.ClientService;
import ggsbot.service.FileService;
import ggsbot.service.PointsService;
import ggsbot.service.SettingsKeyboardService;
import ggsbot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationBotState implements BotState{

    private static final Logger logger = LoggerFactory.getLogger(LocationBotState.class);

    private final PointsService pointsService;

    private final FileService fileService;

    private final ClientService clientService;

    private final SettingsKeyboardService settingsKeyboardService;

    @Autowired
    public LocationBotState(PointsService pointsService, FileService fileService,
                            ClientService clientService, SettingsKeyboardService settingsKeyboardService) {
        this.pointsService = pointsService;
        this.fileService = fileService;
        this.clientService = clientService;
        this.settingsKeyboardService = settingsKeyboardService;
    }

    @Override
    public State getState() {
        return State.LOCATION;
    }

    @Override
    public List<PartialBotApiMethod<?>> handleMessage(Client client, Update update) {
        if (isLocationReceived(update)) {
            Location location = update.getMessage().getLocation();
            List<Point> points = pointsService.getPoints(location.getLatitude(), location.getLongitude(), client);
            try {
                clientService.incrementCount(client);
                if (!points.isEmpty()) {
                    List<PartialBotApiMethod<?>> result = fileService.formFiles(points, client)
                            .stream()
                            .map(f -> initSendDocument(f, client))
                            .collect(Collectors.toList());
                    result.add(Utils.initStartMessage(client, Messages.FILES_READY, Messages.SEND_LOCATION));
                    return result;
                }
                return List.of(Utils.initStartMessage(client, Messages.POINTS_NOT_FOUND, Messages.SEND_LOCATION));
            } catch (IOException e) {
                logger.error("Проблемы при формировании файлов", e);
            }
        } else if (isSettingsReceived(update)) {
            clientService.incrementClientState(client);
            return List.of(settingsKeyboardService.initInlineKeyboard(client));
        }
        return Collections.emptyList();
    }

    private boolean isLocationReceived(Update update) {
        return update.getMessage() != null && update.getMessage().getLocation() != null;
    }

    private boolean isSettingsReceived(Update update) {
        return update.getMessage() != null && update.getMessage().getText() != null &&
                update.getMessage().getText().equals(Messages.SETTINGS);
    }

    private SendDocument initSendDocument(File file, Client client) {
        return SendDocument.builder()
                .chatId(String.valueOf(client.getId()))
                .document(new InputFile(file, Utils.getResultFileName(file)))
                .build();

    }

}
