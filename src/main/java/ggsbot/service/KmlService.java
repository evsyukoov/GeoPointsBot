package ggsbot.service;

import ggsbot.model.data.Point;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class KmlService {

    private static final String KML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.0\">\"" +
            "<Document><Style id=\"z1\"><IconStyle><scale>1.2</scale><color>ffFFFFFF</color><Icon>" +
            "<href>http://maps.google.com/mapfiles/kml/shapes/triangle.png</href></Icon></IconStyle></Style>\n";

    public void formKml(List<Point> points, File output) throws IOException {
        try (Writer kml = new OutputStreamWriter(new FileOutputStream(output))) {
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

