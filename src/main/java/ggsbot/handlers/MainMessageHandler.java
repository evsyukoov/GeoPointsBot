package ggsbot.handlers;

import ggsbot.constants.Messages;
import ggsbot.model.data.Client;
import ggsbot.service.ClientService;
import ggsbot.states.BotStateFactory;
import ggsbot.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

@Service
public class MainMessageHandler {

    private final ClientService clientService;

    @Autowired
    public MainMessageHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    public List<PartialBotApiMethod<?>> handleMessage(Update update) {
        Client client = clientService.getClient(update);
        if (isStartMessage(update)) {
            clientService.moveClientToStart(client);
            return Collections.singletonList(Utils.initStartMessage(client));
        }
        return BotStateFactory
                .initState(client)
                .handleMessage(client, update);
    }

    private boolean isStartMessage(Update update) {
        return update.getMessage() != null
                && update.getMessage().getText() != null
                && update.getMessage().getText().equals(Messages.START);
    }

}
