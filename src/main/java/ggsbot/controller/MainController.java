package ggsbot.controller;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ggsbot.config.Config;
import ggsbot.handlers.MainMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@RestController
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final ObjectMapper objectMapper;

    private final MainMessageHandler handler;

    @Autowired
    public MainController(ObjectMapper objectMapper, MainMessageHandler handler) {
        this.objectMapper = objectMapper;
        this.handler = handler;
    }

    @Value("${bot.token}")
    private String token;

    @PostMapping(path = "/updates/v1/{token}")
    public BotApiMethod processRequest(@RequestBody Update update) throws JsonProcessingException {
        System.out.println("Test");
        logger.info("Request from telegram {}", objectMapper.writeValueAsString(update));
        return handler.handleMessage(update);
    }

}
