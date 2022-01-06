package ggsbot.utils;

import ggsbot.constants.Messages;
import ggsbot.model.data.Client;
import ggsbot.states.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

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

    public static String getCurrentDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        return dtf.format(LocalDateTime.now());
    }

    public static <T extends CharSequence> String collectionToString(List<T> list) {
        return String.join(",", list);
    }

    public static List<?> stringToCollection(String str) {
        return Arrays.asList(str.split(","));
    }

    public static SendMessage initSendMessage(Client client, String msg) {
        return SendMessage.builder()
                .chatId(String.valueOf(client.getId()))
                .text(msg)
                .build();
    }

    public static SendMessage initSendMessage(Client client, String msg, ReplyKeyboardMarkup markup) {
        return SendMessage.builder()
                .chatId(String.valueOf(client.getId()))
                .text(msg)
                .replyMarkup(markup)
                .build();
    }

}
