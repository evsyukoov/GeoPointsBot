package ggsbot.utils;

import ggsbot.states.State;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

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
}
