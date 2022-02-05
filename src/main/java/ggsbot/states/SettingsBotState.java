package ggsbot.states;

import ggsbot.constants.Messages;
import ggsbot.model.data.Client;
import ggsbot.model.data.Settings;
import ggsbot.service.ClientService;
import ggsbot.service.SettingsKeyboardService;
import ggsbot.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

@Service
public class SettingsBotState implements BotState {

    private final SettingsKeyboardService settingsKeyboardService;

    private final ClientService clientService;

    @Autowired
    public SettingsBotState(SettingsKeyboardService settingsKeyboardService, ClientService clientService) {
        this.settingsKeyboardService = settingsKeyboardService;
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
                return List.of(settingsKeyboardService.editKeyboard(update, client));
            } else if (isApproveNewSettings(update)) {
                Optional<Settings> optionalSettings;
                if ((optionalSettings = settingsKeyboardService.setSettings(update, client)).isPresent()) {
                    clientService.updateSettings(client, optionalSettings.get());
                    clientService.moveClientToStart(client);
                    return List.of(Utils.initMessage(client,
                            Messages.SETTINGS_APPROVE, Messages.SEND_LOCATION));
                }
                return Collections.emptyList();
            } else if (isApproveDefaultSettings(update)) {
                clientService.defaultSettings(client);
                clientService.moveClientToStart(client);
                return List.of(settingsKeyboardService.refreshDefaultKeyboard(client, update),
                                Utils.initMessage(client,
                                        Messages.SETTINGS_DEFAULT, Messages.SEND_LOCATION));
            }
        } else {
            return List.of(Utils.initMessage(client, Messages.INCORRECT_INPUT_SETTINGS),
                    settingsKeyboardService.initInlineKeyboard(client));
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

    private boolean isApproveNewSettings(Update update) {
        String message = update.getCallbackQuery().getData();
        return message.equals(Messages.APPROVE);
    }

    private boolean isApproveDefaultSettings(Update update) {
        String message = update.getCallbackQuery().getData();
        return message.equals(Messages.DEFAULT);
    }
}
