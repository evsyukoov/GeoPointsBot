package ggsbot.states;

import ggsbot.constants.Messages;
import ggsbot.model.data.Client;
import ggsbot.service.ClientService;
import ggsbot.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

@Service
public class DonateBotState implements BotState {

    private ClientService clientService;

    @Autowired
    public DonateBotState(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public State getState() {
        return State.DONATE;
    }

    //TODO продумать ответ если оплата не совершенна, что должно прийти? и должно ли, продумать таймаут
    @Override
    public List<PartialBotApiMethod<?>> handleMessage(Client client, Update update) {
        if (isPaymentPreCheck(update)) {
            return List.of(AnswerPreCheckoutQuery
                    .builder()
                    .ok(true)
                    .preCheckoutQueryId(
                            update.getPreCheckoutQuery().getId())
                    .build());
        } else if (isSuccessfullPaymentInfoReceived(update)) {
            clientService.moveClientToStart(client);
            return List.of(Utils.initMessage(client, Messages.PAYMENT_RESULT_OK, Messages.SEND_LOCATION));
        }
        return Collections.emptyList();
    }

    private boolean isPaymentPreCheck(Update update) {
        return update.getPreCheckoutQuery() != null;
    }

    private boolean isSuccessfullPaymentInfoReceived(Update update) {
        return update.getMessage() != null
                && update.getMessage().getSuccessfulPayment() != null;
    }
}
