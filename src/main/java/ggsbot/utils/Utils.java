package ggsbot.utils;

public class Utils {

    public static final double startMeridian = 19.5;

    public static int findZone(double lon) {
        return (int) ((lon - startMeridian) / 3);
    }
}
