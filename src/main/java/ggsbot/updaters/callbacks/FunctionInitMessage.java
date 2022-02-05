package ggsbot.updaters.callbacks;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

public interface FunctionInitMessage {

    PartialBotApiMethod<?> initUpdate(String clientId);
}
