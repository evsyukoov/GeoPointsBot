package ggsbot.service;

import ggsbot.model.access.ClientDao;
import ggsbot.model.data.Client;
import ggsbot.model.data.FileFormat;
import ggsbot.model.data.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private List<String> pointClasses;

    private Integer defaultRadius;

    private final ClientDao clientDao;

    @Value("${search.default-radius}")
    public void setDefaultRadius(Integer defaultRadius) {
        this.defaultRadius = defaultRadius;
    }

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

    public void incrementClientState(Client client) {
        client.setState(client.getState() + 1);
        clientDao.updateClient(client);
    }

    public void incrementCount(Client client) {
        client.setState(client.getCount() + 1);
        clientDao.updateClient(client);
    }

    public void moveClientToStart(Client client) {
        if (client.getState() != 0) {
            client.setState(0);
            clientDao.updateClient(client);
        }
    }

    private Client initDefaultClient(long id) {
        Client client = new Client();
        client.setId(id);
        client.setState(0);
        client.setCount(0);
        Settings settings = new Settings();
        settings.setRadius(defaultRadius);
        settings.setPointClasses(String.join(",", pointClasses));
        settings.setId(id);
        settings.setFileFormats(FileFormat.toStr());
        client.setSettings(settings);
        clientDao.saveClient(client);
        return client;
    }

    public int getSavedPointRadius(Client client) {
        return client.getSettings().getRadius();
    }

    public List<String> getSavedClientPointClasses(Client client) {
        return Arrays.asList(
                client.getSettings().getPointClasses().split(","));
    }

    public List<String> getSavedClientFileFormats(Client client) {
        return Arrays.asList(
                client.getSettings().getFileFormats().split(","));
    }
}
