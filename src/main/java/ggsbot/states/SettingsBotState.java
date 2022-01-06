package ggsbot.states;

import ggsbot.constants.Messages;
import ggsbot.model.data.Client;
import ggsbot.model.data.FileFormat;
import ggsbot.model.data.Settings;
import ggsbot.service.ClientService;
import ggsbot.service.SettingsService;
import ggsbot.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SettingsBotState implements BotState {

    private final SettingsService settingsService;

    private final ClientService clientService;

    @Autowired
    public SettingsBotState(SettingsService settingsService, ClientService clientService) {
        this.settingsService = settingsService;
        this.clientService = clientService;
    }

    @Override
    public State getState() {
        return State.SETTINGS;
    }

    @Override
    public List<PartialBotApiMethod<?>> handleMessage(Client client, Update update) {
        if (isValidRequest(update)) {
            if (isModifySettingsRequest(update)) {
                return List.of(editKeyboard(update, client));
            } else if (isFinishSettings(update)) {
                if (setSettings(update, client).isPresent()) {
                    return List.of(Utils.initSendMessage(client, Messages.SETTINGS_APPROVE));
                }
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    private boolean isValidRequest(Update update) {
        return update.getCallbackQuery() != null && update.getCallbackQuery().getMessage() != null;
    }

    private boolean isModifySettingsRequest(Update update) {
        String message = update.getCallbackQuery().getData();
        return message.startsWith(Messages.EMPTY_SYMBOL) || message.startsWith(Messages.CONFIRM_SYMBOL);
    }

    private boolean isFinishSettings(Update update) {
        String message = update.getCallbackQuery().getData();
        return message.equals(Messages.APPROVE) || message.equals(Messages.DEFAULT);
    }

    private Optional<Settings> setSettings(Update update, Client client) {
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

    private EditMessageReplyMarkup editKeyboard(Update update, Client client) {
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
}
