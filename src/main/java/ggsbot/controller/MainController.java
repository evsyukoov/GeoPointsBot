package ggsbot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Value("${bot.token}")
    private String token;

    @PostMapping(path = "/updates/v1/{token}")
    public Object processRequest(Object object) {
        System.out.println("Test");
        return null;
    }

}
