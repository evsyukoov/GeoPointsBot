package ggsbot.service;

import ggsbot.model.access.ClientDao;
import ggsbot.model.data.Client;
import ggsbot.model.data.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    List<String> pointClasses;

    private final ClientDao clientDao;

    @Value("${point.classes}")
    public void setPointClasses(List<String> pointClasses) {
        this.pointClasses = pointClasses;
    }

    @Autowired
    public ClientService(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    public Client getClient(long id) {
        Client client;
        if ((client = clientDao.getClient(id)) == null) {
            return initDefaultClient(id);
        }
        return client;
    }

    private Client initDefaultClient(long id) {
        Client client = new Client();
        client.setId(id);
        client.setState(0);
        client.setCount(0);
        Settings settings = new Settings();
        settings.setRadius(20);
        settings.setPointClasses(String.join(",", pointClasses));
        settings.setId(id);
        client.setSettings(settings);
        return client;
    }
}
