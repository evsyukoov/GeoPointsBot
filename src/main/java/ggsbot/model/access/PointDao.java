package ggsbot.model.access;

import ggsbot.model.data.Point;
import org.checkerframework.framework.qual.Unused;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PointDao {

    final private static SessionFactory factory;

    static {
        factory = new Configuration()
                .configure("hibernate_settings.cfg.xml")
                .buildSessionFactory();
    }

    public void savePoint(Point point) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            session.save(point);
            session.getTransaction().commit();
        }
    }

    public List<Point> getAllPointsByZone(int zone) {
        List<Point> points;
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Query<Point> query = session.createQuery("FROM Point WHERE zone =: zone", Point.class);
            query.setParameter("zone", zone);
            points = query.list();
            session.getTransaction().commit();
        }
        return points;
    }

    public List<Point> getAllPointsByZone(List<Integer> zones) {
        List<Point> points;
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            StringBuilder queryStr = new StringBuilder("FROM Point WHERE ");
            for (int i = 0; i < zones.size(); i++) {
                queryStr.append("zone =: zone").append(i).append(" OR ");
            }
            Query<Point> query = session.createQuery(queryStr.substring(0, queryStr.length() - 4), Point.class);
            for (int i = 0; i < zones.size(); i++) {
                query.setParameter("zone" + i, zones.get(i));
            }
            points = query.list();
            session.getTransaction().commit();
        }
        return points;
    }

}
