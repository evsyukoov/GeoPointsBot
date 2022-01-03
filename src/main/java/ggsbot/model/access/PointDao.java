package ggsbot.model.access;

import ggsbot.model.data.Point;
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

    public void savePoints(List<Point> points) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            points.forEach(p -> session.save(p));
            session.getTransaction().commit();
        }
    }

    public void updatePoints(List<Point> points) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            points.forEach(session::update);
            session.getTransaction().commit();
        }
    }

    public List<Point> getAllPoints() {
        List<Point> points = new ArrayList<>();
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Query<Point> query = session.createQuery("FROM Point", Point.class);
            points = query.list();
            session.getTransaction().commit();
        }
        return points;
    }

    public List<Point> getAllPointsByZone(int zone) {
        List<Point> points = new ArrayList<>();
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Query<Point> query = session.createQuery("FROM Point WHERE zone =: zone", Point.class);
            query.setParameter("zone", zone);
            points = query.list();
            session.getTransaction().commit();
        }
        return points;
    }

}
