package ggsbot.handlers;

import ggsbot.constants.Messages;
import ggsbot.model.access.ClientDao;
import ggsbot.model.data.Client;
import ggsbot.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MainMessageHandler {


    private final ClientService clientService;

    @Autowired
    public MainMessageHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    public SendMessage handleMessage(Update update) {
          long id = update.getMessage().getChatId();
          Client client = clientService.getClient(id);
          SendMessage sendMessage = new SendMessage();
          sendMessage.setChatId(String.valueOf(client.getId()));
          sendMessage.setText(Messages.SEND_LOCATION);
          return sendMessage;
    }
}
