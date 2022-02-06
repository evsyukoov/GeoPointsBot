package ggsbot.service;

import ggsbot.model.access.ClientDao;
import ggsbot.model.data.Client;
import ggsbot.model.data.FileFormat;
import ggsbot.model.data.Settings;
import ggsbot.states.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

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

    public Client getClient(Update update) {
        Client client;
        long id = getId(update);
        if ((client = clientDao.getClient(id)) == null) {
            return initDefaultClient(id, getName(update), getNickName(update));
        }
        return client;
    }

    private long getId(Update update) {
        if (update.getPreCheckoutQuery() != null) {
            return update.getPreCheckoutQuery().getFrom().getId();
        } else if (update.getMessage() != null) {
            return update.getMessage().getChatId();
        } else {
            return update.getCallbackQuery().getMessage().getChatId();
        }
    }

    private String getName(Update update) {
        Chat chat = update.getMessage().getChat();
        return chat.getFirstName()
                + (chat.getLastName() == null ? "" : (" " + chat.getLastName()));
    }

    private String getNickName(Update update) {
        return update.getMessage().getChat().getUserName();
    }

    public void updateSettings(Client client, Settings settings) {
        client.setSettings(settings);
        clientDao.updateClient(client);
    }

    public void defaultSettings(Client client) {
        client.setSettings(initDefaultSettings(client.getId()));
        clientDao.updateClient(client);
    }

    public void incrementClientState(Client client) {
        client.setState(client.getState() + 1);
        clientDao.updateClient(client);
    }

    public void updateClientState(Client client, State state) {
        client.setState(state.ordinal());
        clientDao.updateClient(client);
    }

    public void incrementCount(Client client) {
        client.setCount(client.getCount() + 1);
        clientDao.updateClient(client);
    }

    public void moveClientToStart(Client client) {
        if (client.getState() != 0) {
            client.setState(0);
            clientDao.updateClient(client);
        }
    }

    private Client initDefaultClient(long id, String name, String nickName) {
        Client client = new Client();
        client.setId(id);
        client.setState(0);
        client.setCount(0);
        client.setName(name);
        client.setNickName(nickName);
        client.setSettings(initDefaultSettings(id));
        clientDao.saveClient(client);
        return client;
    }

    private Settings initDefaultSettings(long id) {
        Settings settings = new Settings();
        settings.setRadius(defaultRadius);
        settings.setPointClasses(String.join(",", pointClasses));
        settings.setId(id);
        settings.setFileFormats(FileFormat.toStr());
        return settings;
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
