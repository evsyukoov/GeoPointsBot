package ggsbot.service;

import ggsbot.model.data.Client;
import ggsbot.model.data.FileFormat;
import ggsbot.model.data.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

            }
        }
        return outputFiles;
    }

    private void formKml(List<Point> points, File file) throws IOException {
        try (Writer kml = new OutputStreamWriter(new FileOutputStream(file))) {
            kml.write(KML_HEADER);
            for (Point p : points) {
                kml.write(String.format("<Placemark><name>%s</name><description>Class: %s, Mark: %s, Center-type: %s</description><stileUrl>#z1</stileUrl>" +
                                "<Point><coordinates>%s,%s,%d</coordinates></Point></Placemark>\r\n", p.getName(), p.getPointClass(),
                        p.getMark() == null ? "-" : p.getMark(), p.getCenterType() == null ? "-" : p.getCenterType(),
                        p.getLon(), p.getLat(), 0));
            }
            kml.write("</Document>\n</kml>");
        }

    }

}

