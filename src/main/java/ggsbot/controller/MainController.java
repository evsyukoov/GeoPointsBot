package ggsbot.controller;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ggsbot.bot.GeoPointBot;
import ggsbot.config.Config;
import ggsbot.handlers.MainMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final ObjectMapper objectMapper;

    private final MainMessageHandler handler;

    private final GeoPointBot geoPointBot;

    @Autowired
    public MainController(ObjectMapper objectMapper, MainMessageHandler handler, GeoPointBot geoPointBot) {
        this.objectMapper = objectMapper;
        this.handler = handler;
        this.geoPointBot = geoPointBot;
    }

    @Value("${bot.token}")
    private String token;

    @PostMapping(path = "/updates/v1/{token}")
    public void processRequest(@RequestBody Update update) throws JsonProcessingException {
        try {
            logger.info("Request from telegram {}", objectMapper.writeValueAsString(update));
            geoPointBot.sendAnswer(handler.handleMessage(update));
        } catch (Exception e) {
            System.out.println("exc");
        }
    }

}
