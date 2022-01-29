package ggsbot.model.access;

import ggsbot.model.data.Client;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClientDao {
    final private static SessionFactory factory;

    static {
        factory = new Configuration()
                .configure("hibernate_settings.cfg.xml")
                .buildSessionFactory();
    }

    public void saveClient(Client client) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            session.save(client);
            session.getTransaction().commit();
        }
    }

    public void updateClient(Client client) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public Client getClient(long id) {
        Client client = null;
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client = session.get(Client.class, id);
            session.getTransaction().commit();
        }
        return client;
    }

    public List<Client> getAllClients() {
        List<Client> list;
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            list = session.createQuery("From Client", Client.class).list();
            session.getTransaction().commit();
        }
        return list;
    }
}
