package ggsbot.updaters;

import ggsbot.bot.GeoPointBot;
import ggsbot.model.access.ClientDao;
import ggsbot.model.data.Client;
import ggsbot.updaters.callbacks.FunctionInitMessage;
import ggsbot.service.SettingsKeyboardService;
import ggsbot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UpdateEngine {

    private final ClientDao clientDao;

    private final GeoPointBot geoPointBot;

    private final SettingsKeyboardService keyboardService;

    private static final Logger logger = LoggerFactory.getLogger(UpdateEngine.class);

    private static final Long WAIT = 10 * 1000L;

    @Autowired
    public UpdateEngine(ClientDao clientDao, GeoPointBot geoPointBot, SettingsKeyboardService keyboardService) {
        this.clientDao = clientDao;
        this.geoPointBot = geoPointBot;
        this.keyboardService = keyboardService;
    }

    @PostConstruct
    public void run() {
//        new Thread(() -> sendInfo(List.of(UpdateMessagesUtil::initUpdateMessage1,
//                UpdateMessagesUtil::initUpdateVideo))).start();
//        new Thread(() -> sendInfo(
//                List.of(UpdateMessagesUtil::initUpdateMessage2))).start();
        new Thread(() -> sendInfo(
                List.of(UpdateMessagesUtil::initUpdateMessage3))).start();
    }

    private void sendInfo(List<FunctionInitMessage> functions){
        int i = 1;
        for (Client client : clientDao.getAllClients()) {
                // обход лимита телеграма на отправку сообщений
                if (i % 10 == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                logger.info("{} Start sending update to {}", i, client.getName());
                try {
                    List<PartialBotApiMethod<?>> updateMsg = functions.stream()
                            .map(func -> func.initUpdate(
                                    String.valueOf(client.getId())))
                                    .collect(Collectors.toList());
                    updateMsg.add(initStartMessage(client));
                    geoPointBot.sendAnswer(updateMsg);
                } catch (TelegramApiException e) {
                    logger.error("{} Failed sending update to {}", i, client.getName());
                    continue;
                }
                logger.info("{} Sucessfully sending update to {}", i, client.getName());
                i++;
        }
    }

    // после апдейта отправляем то сообщение, которое висело у клиента
    private SendMessage initStartMessage(Client client) {
        if (client.getState() == 0) {
            return Utils.initStartMessage(client);
        } else if (client.getState() == 1) {
            return keyboardService.initInlineKeyboard(client);
        }
        return new SendMessage();
    }
}
