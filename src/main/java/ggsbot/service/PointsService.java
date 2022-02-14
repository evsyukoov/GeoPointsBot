package ggsbot.service;

import ggsbot.model.access.PointDao;
import ggsbot.model.data.Client;
import ggsbot.model.data.Point;
import ggsbot.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class PointsService {

    private final PointDao pointDao;

    private final ClientService clientService;

    @Autowired
    public PointsService(PointDao pointDao, ClientService clientService) {
        this.pointDao = pointDao;
        this.clientService = clientService;
    }

    public List<Point> getPoints(double lat, double lon, Client client) {
        int zone = Utils.findZone(lon);
        List<Point> points = pointDao.getAllPointsByZone(zone);
        return filterPoints(points, lat, lon, client);
    }

    public List<Point> getPoints(Polygon polygon, Client client) {
        return pointDao.getAllPointsByZone(findAllZonesFromPolygon(polygon));
    }

    private List<Integer> findAllZonesFromPolygon(Polygon polygon) {
        List<Integer> zones = new ArrayList<>(2);
        IntStream.range(Utils.findZone(Arrays.stream(polygon.xpoints).min().getAsInt()),
                Utils.findZone(Arrays.stream(polygon.xpoints).max().getAsInt()) + 1)
                .forEach(zones::add);
        return zones;
    }

    private List<Point> filterPoints(List<Point> list, double lat, double lon, Client client) {
        int radius = clientService.getSavedPointRadius(client);
        List<String> pointClasses = clientService.getSavedClientPointClasses(client);
        return list.stream()
                .filter(p -> getDist(lat, lon, p.getLat(), p.getLon()) <= radius)
                .filter(p -> pointClasses.contains(p.getPointClass()))
                .collect(Collectors.toList());
    }

    private double getDist(double lat1, double lon1, double lat2, double lon2)
    {
        int R = 6373; // radius of the earth in kilometres
        double lat1rad = Math.toRadians(lat1);
        double lat2rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2-lat1);
        double deltaLon = Math.toRadians(lon2-lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1rad) * Math.cos(lat2rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double d = R * c;
        return d;
    }
}
