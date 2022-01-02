package ggsbot.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ggsbot.model.Point;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PointDao {

    public PointDao() {
        LoggerContext logContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        Logger logHiber = logContext.getLogger("org.hibernate");
        logHiber.setLevel(Level.INFO);
    }

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
