package ggsbot.utils;

import ggsbot.constants.Messages;
import ggsbot.model.data.Client;
import ggsbot.states.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static final double startMeridian = 19.5;

    public static int findZone(double lon) {
        return (int) ((lon - startMeridian) / 3);
    }

    public static State getState(int stateFromBd) {
        return State.values()[stateFromBd];
    }

    public static String getResultFileName(File file) {
        return "GGS_" + getCurrentDateTime() + getExtension(file);
    }

    private static String getCurrentDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        return dtf.format(LocalDateTime.now());
    }

    private static String  getExtension(File file) {
        String fileName = file.getName();
        return fileName.substring(fileName.indexOf("."));
    }

    public static <T extends CharSequence> String collectionToString(List<T> list) {
        return String.join(",", list);
    }

    public static SendMessage initSendMessage(Client client, String msg, ReplyKeyboardMarkup markup) {
        return SendMessage.builder()
                .chatId(String.valueOf(client.getId()))
                .text(msg)
                .replyMarkup(markup)
                .build();
    }

    public static SendMessage initStartMessage(Client client) {
        return Utils.initSendMessage(client, Messages.SEND_LOCATION, initReplyKeyboardMarkup());
    }

    public static SendMessage initMessage(Client client, String ... messages) {
        return Utils.initSendMessage(client,
                String.join("\n", Arrays.asList(messages)),
                initReplyKeyboardMarkup());
    }

    private static ReplyKeyboardMarkup initReplyKeyboardMarkup() {
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
