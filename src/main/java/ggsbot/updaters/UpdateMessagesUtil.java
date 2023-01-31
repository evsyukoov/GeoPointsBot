package ggsbot.updaters;

import org.springframework.core.io.ClassPathResource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.IOException;

import static ggsbot.constants.UpdateMessages.UPDATE1;
import static ggsbot.constants.UpdateMessages.UPDATE2;
import static ggsbot.constants.UpdateMessages.UPDATE3;

/**
 * Класс для хранения методов, формирующих сообщение об обновлении пользователю
 * Методы хранятся для истории
 */
public class UpdateMessagesUtil {

    public static File file;

    static {
        try {
            file = new ClassPathResource("updates/update-2022-01-29.mp4").getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SendVideo initUpdateVideo(String clientId) {
        return SendVideo
                .builder()
                .chatId(clientId)
                .video(new InputFile(file))
                .build();
    }

    public static SendMessage initMessage(String clientId) {
        SendMessage sm = new SendMessage();
        sm.setChatId(clientId);
        return sm;
    }

    /**
     * Обновление 29.01.2022 - возможность поделиться координатами с ботом из яндекс карт
     * @param clientId
     * @return
     */
    public static SendMessage initUpdateMessage1(String clientId) {
        SendMessage sm = initMessage(clientId);
        sm.setText(UPDATE1);
        return sm;
    }

    /**
     * Обновление 05.02.2022 - Добавлена номенклатура точек из росреестра.
     * Добавлен ответ пользователю в случае невалидного ввода
     * @param clientId
     * @return
     */
    public static SendMessage initUpdateMessage2(String clientId) {
        SendMessage sm = initMessage(clientId);
        sm.setText(UPDATE2);
        return sm;
    }

    /**
     * Обновление 31.01.2023 - Добавлены нивелирные пункты по РФ и бывшему СНГ.
     * Добавлены польщовательские настройки
     * @param clientId
     * @return
     */
    public static SendMessage initUpdateMessage3(String clientId) {
        SendMessage sm = initMessage(clientId);
        sm.setText(UPDATE3);
        return sm;
    }
}
