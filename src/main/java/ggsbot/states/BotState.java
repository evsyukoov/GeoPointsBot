package ggsbot.states;


import ggsbot.model.data.Client;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface BotState {

    State getState();

    List<PartialBotApiMethod<?>> handleMessage(Client client, Update update);

}
