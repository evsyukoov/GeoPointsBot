package ggsbot.states;

import ggsbot.constants.Messages;
import ggsbot.model.access.PointDao;
import ggsbot.model.data.Client;
import ggsbot.model.data.Point;
import ggsbot.service.ClientService;
import ggsbot.service.KmlService;
import ggsbot.service.PointsService;
import ggsbot.service.SettingsService;
import ggsbot.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LocationBotState implements BotState{

    private final PointsService pointsService;

    private final KmlService kmlService;

    private final ClientService clientService;

    private final SettingsService settingsService;

    @Autowired
    public LocationBotState(PointsService pointsService, KmlService kmlService,
                            ClientService clientService, SettingsService settingsService) {
        this.pointsService = pointsService;
        this.kmlService = kmlService;
        this.clientService = clientService;
        this.settingsService = settingsService;
    }

    @Override
    public State getState() {
        return State.LOCATION;
    }

    @Override
    public List<PartialBotApiMethod<?>> handleMessage(Client client, Update update) {
        if (isLocationReceived(update)) {
            Location location = update.getMessage().getLocation();
            List<Point> points = pointsService.getPoints(location.getLatitude(), location.getLongitude());
            File file = new File(String.format("files/GGS_%s_%s.kml", client.getId(), Utils.getCurrentDateTime()));
            try {
                kmlService.formKml(points, file);
                return List.of(initSendDocument(file, client));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (isSettingsReceived(update)) {
            clientService.incrementClientState(client);
            return List.of(settingsService.initInlineKeyboard(client));
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
                .document(new InputFile(file))
                .build();

    }

}
