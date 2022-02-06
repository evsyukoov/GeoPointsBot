package ggsbot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.List;

@Component
public class GeoPointBot extends TelegramWebhookBot {

    private static final Logger logger = LoggerFactory.getLogger(GeoPointBot.class);

    private String token;

    private String userName;

    @Value("${bot.token}")
    public void setToken(String token) {
        this.token = token;
    }

    @Value("${bot.name}")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return null;
    }

    public void sendAnswer(List<PartialBotApiMethod<?>> answer) throws TelegramApiException {
        for (PartialBotApiMethod<?> send : answer) {
            try {
                if (send instanceof SendMessage) {
                    this.execute((SendMessage) send);
                } else if (send instanceof SendDocument) {
                    this.execute((SendDocument) send);
                } else if (send instanceof EditMessageReplyMarkup) {
                    this.execute((EditMessageReplyMarkup) send);
                } else if (send instanceof SendVideo) {
                    this.execute((SendVideo) send);
                } else if (send instanceof SendInvoice) {
                    this.execute((SendInvoice) send);
                } else if (send instanceof AnswerPreCheckoutQuery) {
                    this.execute((AnswerPreCheckoutQuery) send);
                }
            } catch (TelegramApiException e) {
                // TODO ошибка может возникнуть, если клиент ничего не изменил в настройках,
                //  тогда отправка EditMessageReplyMarkup падает,
                //  но ничего не ломается, в идеале добавить в SettingsKeyboardService проверку,
                //  что клиент действительно что-то менял, чтобы не формировать лишний объект на отправку
                if (e instanceof TelegramApiRequestException) {
                    continue;
                }
                throw e;
            }
        }
    }
}
