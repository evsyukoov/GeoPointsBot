package ggsbot.notification.updaters;

import ggsbot.bot.GeoPointBot;
import ggsbot.constants.Messages;
import ggsbot.model.access.ClientDao;
import ggsbot.model.data.Client;
import ggsbot.service.SettingsKeyboardService;
import ggsbot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


/**
 * Класс для отправки видео-информации, о новой фиче бота
 * всем пользователям
 * Обновление 29.01.2022 - возможность поделиться координатами с ботом из яндекс карт
 */
@Service
public class Updater1 {

    private final ClientDao clientDao;

    private final GeoPointBot geoPointBot;

    private final SettingsKeyboardService keyboardService;

    private static final Logger logger = LoggerFactory.getLogger(Updater1.class);

    @Autowired
    public Updater1(ClientDao clientDao, GeoPointBot geoPointBot, SettingsKeyboardService keyboardService) {
        this.clientDao = clientDao;
        this.geoPointBot = geoPointBot;
        this.keyboardService = keyboardService;
    }

    //@PostConstruct
    public void run() {
        new Thread(this::sendInfo).start();
    }

    private void sendInfo(){
        File infoAboutUpdate;
        try {
            infoAboutUpdate = new ClassPathResource("updates/update-2022-01-29.mp4").getFile();
        } catch (IOException e) {
            return;
        }
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
                    geoPointBot.sendAnswer(List.of(
                            initUpdateMessage(String.valueOf(client.getId())),
                            initUpdateVideo(String.valueOf(client.getId()), infoAboutUpdate),
                            initStartMessage(client)));
                } catch (TelegramApiException e) {
                    logger.error("{} Failed sending update to {}", i, client.getName());
                    continue;
                }
                logger.info("{} Sucessfully sending update to {}", i, client.getName());
                i++;
        }
    }

    private SendVideo initUpdateVideo(String clientId, File file) {
        return SendVideo
                .builder()
                .chatId(clientId)
                .video(new InputFile(file))
                .build();
    }

    private SendMessage initUpdateMessage(String clientId) {
        return SendMessage
                .builder()
                .chatId(clientId)
                .text(Messages.UPDATE)
                .build();
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
