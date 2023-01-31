package ggsbot.service;

import ggsbot.constants.Const;
import ggsbot.model.data.Client;
import ggsbot.model.data.FileFormat;
import ggsbot.model.data.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.math3.util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    ClientService clientService;

    @Autowired
    public FileService(ClientService clientService) {
        this.clientService = clientService;
    }

    private static final String KML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.0\">\"" +
            "<Document><Style id=\"z1\"><IconStyle><scale>1.2</scale><color>ffFFFFFF</color><Icon>" +
            "<href>http://maps.google.com/mapfiles/kml/shapes/triangle.png</href></Icon></IconStyle></Style>\n";

    private static final String GPX_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gpx\nxmlns=\"http://www.topografix.com/GPX/1/1\"" +
            "\nversion=\"1.1\"\ncreator=\"InjGeo_bot\"\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "xsi:schemaLocation=\"http://www.garmin.com/xmlschemas/WaypointExtension/v1 \">\n";

    public List<File> formFiles(List<Point> points, Client client) throws IOException {
        List<File> outputFiles = new ArrayList<>();
        List<String> formats = clientService.getSavedClientFileFormats(client);
        for (String s : formats) {
            FileFormat fileFormat = FileFormat.valueOf(s);
            if (fileFormat == FileFormat.KML) {
                File file = new File(String.format("files/%s.kml", client.getId()));
                outputFiles.add(file);
                formKml(points, file);
            }
            if (fileFormat == FileFormat.GPX) {
                File file = new File(String.format("files/%s.gpx", client.getId()));
                outputFiles.add(file);
                formGpx(points, file);
            }
        }
        return outputFiles;
    }

    private void formKml(List<Point> points, File file) throws IOException {
        try (Writer kml = new OutputStreamWriter(new FileOutputStream(file))) {
            kml.write(KML_HEADER);
            for (Point p : points) {
                kml.write(String.format("<Placemark><name>%s%s</name><description>%s</description><stileUrl>#z1</stileUrl>" +
                                "<Point><coordinates>%s,%s,%d</coordinates></Point></Placemark>\r\n",
                        getPointClassPrefix(p.getPointClass()),
                        p.getName(),
                        getDescription(p),
                        Precision.round(p.getLon(), 6), Precision.round(p.getLat(), 6), 0));
            }
            kml.write("</Document>\n</kml>");
        }
    }

    private void formGpx(List<Point> points, File file) throws IOException {
        try (Writer gpx = new OutputStreamWriter(new FileOutputStream(file))) {
            gpx.write(GPX_HEADER);
            for (Point p : points) {
                gpx.write(String.format("<wpt lat=\"%s\" lon=\"%s\"><name>%s%s</name><desc>%s</desc></wpt>\n",
                        Precision.round(p.getLat(), 6), Precision.round(p.getLon(), 6),
                        getPointClassPrefix(p.getPointClass()),
                        p.getName(),
                        getDescription(p)));
            }
            gpx.write("</gpx>");
        }
    }

    private String getPointClassPrefix(String s) {
        if (s.contains(Const.HEIGHT)) {
            return s.charAt(0) + "Н_";
        }
        return s.equals("Неизвестный") ? "" : (s + "_");
    }

    private String getDescription(Point p) {
        return p.getPointClass().contains(Const.HEIGHT) ?
            p.getDescription() : String.format("Catalog number: %s, Class: %s, Mark: %s, Center-type: %s",
                p.getIndex() == null ? "-" : p.getIndex(), // Крымский кейс
                p.getPointClass(),
                p.getMark() == null ? "-" : p.getMark(),
                p.getCenterType() == null ? "-" : p.getCenterType());
    }

}

