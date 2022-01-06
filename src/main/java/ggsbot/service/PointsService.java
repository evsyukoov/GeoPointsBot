package ggsbot.service;

import ggsbot.model.access.PointDao;
import ggsbot.model.data.Point;
import ggsbot.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PointsService {

    private final PointDao pointDao;

    public static final double radius = 20;

    @Autowired
    public PointsService(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public List<Point> getPoints(double lat, double lon) {
        int zone = Utils.findZone(lon);
        List<Point> points = pointDao.getAllPointsByZone(zone);
        return filterPoints(points, lat, lon);
    }

    public List<Point> filterPoints(List<Point> list, double lat, double lon) {
        List<Point> res = list.stream()
                .filter(p -> getDist(lat, lon, p.getLat(), p.getLon()) <= radius)
                .collect(Collectors.toList());
        return res;
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
