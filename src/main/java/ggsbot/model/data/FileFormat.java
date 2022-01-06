package ggsbot.model.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum FileFormat {
    KML, GPX;

    public static String toStr() {
        return Arrays.stream(FileFormat.values())
                .map(FileFormat::name)
                .collect(Collectors.joining(","));
    }

    public static List<String> toList() {
        return Arrays.stream(FileFormat.values())
                .map(FileFormat::name)
                .collect(Collectors.toList());
    }
}
