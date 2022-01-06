package ggsbot.handlers;

import ggsbot.constants.Messages;
import ggsbot.model.data.Client;
import ggsbot.service.ClientService;
import ggsbot.states.BotStateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Collections;
import java.util.List;

@Service
public class MainMessageHandler {

    private final ClientService clientService;

    @Autowired
    public MainMessageHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    public List<PartialBotApiMethod<?>> handleMessage(Update update) {
        long id = update.getMessage().getChatId();
        Client client = clientService.getClient(id);
        if (isStartMessage(update)) {
            clientService.moveClientToStart(client);
            return Collections.singletonList(initStartMessage(client));
        }
        return BotStateFactory
                .initState(client)
                .handleMessage(client, update);
    }

    private boolean isStartMessage(Update update) {
        return update.getMessage() != null
                && update.getMessage().getText() != null
                && update.getMessage().getText().equals(Messages.START);
    }

    private SendMessage initStartMessage(Client client) {
        return SendMessage.builder()
                .chatId(String.valueOf(client.getId()))
                .text(Messages.SEND_LOCATION)
                .replyMarkup(initReplyKeyboardMarkup())
                .build();
    }

    private ReplyKeyboardMarkup initReplyKeyboardMarkup() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton button = new KeyboardButton();
        button.setText(Messages.SETTINGS);
        List<KeyboardRow> keyboardRows = List.of(keyboardRow);
        keyboardRow.add(button);
        markup.setKeyboard(keyboardRows);
        markup.setOneTimeKeyboard(true);
        markup.setResizeKeyboard(true);
        return markup;
    }
}
