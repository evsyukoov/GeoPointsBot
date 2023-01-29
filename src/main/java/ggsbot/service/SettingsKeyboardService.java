package ggsbot.service;

import ggsbot.constants.Messages;
import ggsbot.model.data.Client;
import ggsbot.model.data.FileFormat;
import ggsbot.model.data.Settings;
import ggsbot.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SettingsKeyboardService {

    private List<Integer> availableRadius;

    private List<String> availablePointClasses;

    private final ClientService clientService;

    @Autowired
    public SettingsKeyboardService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Value("#{'${search.radius}'.split(',\\s*')}")
    public void setAvailableRadius(List<Integer> availableRadius) {
        this.availableRadius = availableRadius;
    }

    @Value("#{'${point.classes}'.split(',\\s*')}")
    public void setPointClasses(List<String> pointClasses) {
        this.availablePointClasses = pointClasses;
    }

    public SendMessage initInlineKeyboard(Client client) {
        SendMessage sm = new SendMessage();
        InlineKeyboardMarkup markup = initKeyboardMarkup(client);
        sm.setText(Messages.SETTINGS_MESSAGE);
        sm.setReplyMarkup(markup);
        sm.setChatId(String.valueOf(client.getId()));
        return sm;
    }

    private InlineKeyboardMarkup initKeyboardMarkup(Client client) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        initRadiusKeyBoard(rows, client);
        initAvailablePointClasses(rows, client);
        initAvailableFileFormats(rows, client);
        initActionButtons(rows);
        markup.setKeyboard(rows);
        return markup;
    }

    private void initRadiusKeyBoard(List<List<InlineKeyboardButton>> rows, Client client) {
        for (int i = 0; i < availableRadius.size(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            rows.add(row);
            int savedClientRadius = clientService.getSavedPointRadius(client);
            String msg;
            if (availableRadius.get(i) != savedClientRadius) {
                msg = Messages.EMPTY_SYMBOL.concat(String.valueOf(availableRadius.get(i)));
            } else {
                msg = Messages.CONFIRM_SYMBOL.concat(String.valueOf(availableRadius.get(i)));
            }
            row.add(newButton(msg + " км", msg));
        }
    }

    private void initAvailablePointClasses(List<List<InlineKeyboardButton>> rows, Client client) {
        for (int i = 0; i < availablePointClasses.size(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            rows.add(row);
            List<String> savedClientClasses = clientService.getSavedClientPointClasses(client);
            String msg;
            if (savedClientClasses.contains(availablePointClasses.get(i))) {
                msg = Messages.CONFIRM_SYMBOL.concat(String.valueOf(availablePointClasses.get(i)));
            } else {
                msg = Messages.EMPTY_SYMBOL.concat(String.valueOf(availablePointClasses.get(i)));
            }
            row.add(newButton(msg + " класс", msg));
        }
    }

    private void initAvailableFileFormats(List<List<InlineKeyboardButton>> rows, Client client) {
        FileFormat[] fileFormats = FileFormat.values();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < fileFormats.length; i++) {
            List<String> savedFileFormats = clientService.getSavedClientFileFormats(client);
            String msg;
            if (savedFileFormats.contains(fileFormats[i].name())) {
                msg = Messages.CONFIRM_SYMBOL.concat(String.valueOf(fileFormats[i]));
            } else {
                msg = Messages.EMPTY_SYMBOL.concat(String.valueOf(fileFormats[i]));
            }
            row.add(newButton(msg, msg));
        }
        rows.add(row);
    }

    private void initActionButtons(List<List<InlineKeyboardButton>> rows) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(newButton(Messages.DEFAULT, Messages.DEFAULT));
        row.add(newButton(Messages.APPROVE, Messages.APPROVE));
        rows.add(row);
    }

    private InlineKeyboardButton newButton(String msg, String callback) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(msg);
        button.setCallbackData(callback);
        return button;
    }

    public Optional<Settings> setSettings(Update update, Client client) {
        Settings settings = client.getSettings();
        List<String> pointClasses = new ArrayList<>(5);
        List<String> fileFormats = new ArrayList<>(2);
        InlineKeyboardMarkup markup = update.getCallbackQuery().getMessage().getReplyMarkup();
        final AtomicBoolean fillRadius = new AtomicBoolean();
        final AtomicBoolean fillClasses = new AtomicBoolean();
        final AtomicBoolean fillFileFormats = new AtomicBoolean();
        markup.getKeyboard().stream()
                .flatMap(Collection::stream)
                .filter(butt -> butt.getCallbackData().startsWith(Messages.CONFIRM_SYMBOL))
                .forEach(butt -> {
                    if (butt.getText().endsWith("км")) {
                        settings.setRadius(Integer.parseInt(getButtonPayload(butt.getCallbackData())));
                        fillRadius.set(true);
                    } else if (butt.getText().endsWith("класс")) {
                        pointClasses.add(getButtonPayload(butt.getCallbackData()));
                        fillClasses.set(true);
                    } else if (FileFormat.toList().contains(getButtonPayload(butt.getCallbackData()))) {
                        fileFormats.add(getButtonPayload(butt.getCallbackData()));
                        fillFileFormats.set(true);
                    }
                });
        if (!fillRadius.get() || !fillClasses.get() || !fillFileFormats.get()) {
            return Optional.empty();
        }
        settings.setFileFormats(Utils.collectionToString(fileFormats));
        settings.setPointClasses(Utils.collectionToString(pointClasses));
        return Optional.of(settings);
    }

    private String getButtonPayload(String callbackData) {
        return callbackData.split(" ")[1];
    }

    public EditMessageReplyMarkup editKeyboard(Update update, Client client) {
        InlineKeyboardMarkup markup = update.getCallbackQuery().getMessage().getReplyMarkup();
        String message = update.getCallbackQuery().getData();
        InlineKeyboardButton button = findPressedButton(markup, message);
        if (button.getText().endsWith("км") && !message.startsWith(Messages.CONFIRM_SYMBOL)) {
            Optional<InlineKeyboardButton> confirmedButton = findRadiusConfirmedButton(markup);
            confirmedButton.ifPresent(butt ->
                    butt.setText(butt.getText().replace(Messages.CONFIRM_SYMBOL, Messages.EMPTY_SYMBOL)));

            confirmedButton.ifPresent(butt ->
                    butt.setCallbackData(butt.getCallbackData().replace(Messages.CONFIRM_SYMBOL, Messages.EMPTY_SYMBOL)));

        }
        if (button.getCallbackData().startsWith(Messages.CONFIRM_SYMBOL)) {
            button.setText(button.getText().replace(Messages.CONFIRM_SYMBOL, Messages.EMPTY_SYMBOL));
            button.setCallbackData(button.getCallbackData().replace(Messages.CONFIRM_SYMBOL, Messages.EMPTY_SYMBOL));
        } else {
            button.setText(button.getText().replace(Messages.EMPTY_SYMBOL, Messages.CONFIRM_SYMBOL));
            button.setCallbackData(button.getCallbackData().replace(Messages.EMPTY_SYMBOL, Messages.CONFIRM_SYMBOL));
        }
        return EditMessageReplyMarkup.builder()
                .chatId(String.valueOf(client.getId()))
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(markup)
                .build();
    }

    private InlineKeyboardButton findPressedButton(InlineKeyboardMarkup markup, String text) {
        return markup.getKeyboard().stream()
                .flatMap(Collection::stream)
                .filter(button -> button.getCallbackData().equals(text))
                .findAny()
                .orElse(null);
    }

    private Optional<InlineKeyboardButton> findRadiusConfirmedButton(InlineKeyboardMarkup markup) {
        return markup.getKeyboard().stream()
                .flatMap(Collection::stream)
                .filter(button -> button.getCallbackData().startsWith(Messages.CONFIRM_SYMBOL)
                        && button.getText().endsWith("км"))
                .findAny();
    }

    public EditMessageReplyMarkup refreshDefaultKeyboard(Client client, Update update) {
        EditMessageReplyMarkup editMessage = new EditMessageReplyMarkup();
        editMessage.setReplyMarkup(initKeyboardMarkup(client));
        editMessage.setChatId(String.valueOf(client.getId()));
        editMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        return editMessage;
    }
}
