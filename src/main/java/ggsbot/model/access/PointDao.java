package ggsbot.model.access;

import ggsbot.model.data.Point;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointDao {

    final private static SessionFactory factory;

    private static final Logger logger = LoggerFactory.getLogger(PointDao.class);

    static {
        factory = new Configuration()
                .configure("hibernate_settings.cfg.xml")
                .buildSessionFactory();
    }

    public void savePoints(List<Point> points) {
        try(StatelessSession session = factory.openStatelessSession()) {
            session.beginTransaction();
            int i = 0;
            long time = System.currentTimeMillis();
            for (Point p : points) {
                session.insert(p);
                if (i % 1000 == 0) {
                    logger.info("{} Iteration - {} ms", i, System.currentTimeMillis() - time);
                    time = System.currentTimeMillis();
                }
                i++;
            }
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
