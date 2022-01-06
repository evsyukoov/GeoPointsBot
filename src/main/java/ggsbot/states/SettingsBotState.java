package ggsbot.states;

import ggsbot.model.data.Client;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class SettingsBotState implements BotState {

    @Override
    public State getState() {
        return State.SETTINGS;
    }

    @Override
    public List<PartialBotApiMethod<?>> handleMessage(Client client, Update update) {
        return null;
    }
}
