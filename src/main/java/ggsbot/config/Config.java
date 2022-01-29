package ggsbot.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@EnableWebMvc
public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private final ObjectMapper mapper;

    private String token;

    private String prodHost;

    private String prodPort;

    private String serverLocalPort;

    @Value("${server.port}")
    public void setServerLocalPort(String serverLocalPort) {
        this.serverLocalPort = serverLocalPort;
    }

    public Config() {
        this.mapper = new ObjectMapper();
    }

    @Value("${nginx.prod.host}")
    public void setProdHost(String prodHost) {
        this.prodHost = prodHost;
    }

    @Value("${nginx.prod.port}")
    public void setProdPort(String prodPort) {
        this.prodPort = prodPort;
    }

    @Value("${bot.token}")
    public void setToken(String token) {
        this.token = token;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    //для разработки на локальной машине
    @Bean
    @Profile("local")
    public void createWebhook() throws JsonProcessingException {
        ResponseEntity<Object> response = new RestTemplate().getForEntity(
                generateWebhookUrl(startNgrok()),
                Object.class);
        logger.info("Set ngrok-webhook, response - {}", mapper.writeValueAsString(response));
    }

    @Bean
    @Profile("prod")
    public void createProdWebhook() throws JsonProcessingException {
        ResponseEntity response = getWebhookInfo();
        logger.info("Get Webhook Info - {}", mapper.writeValueAsString(response));
        if (!isAlreadyRegisterProdHost(response.getBody())) {
            ResponseEntity<Object> resp = new RestTemplate().getForEntity(
                    generateWebhookUrl(prodHost, prodPort),
                    Object.class);
            
            logger.info("Set Prod-webhook, response - {}", mapper.writeValueAsString(resp));
        }
    }

    private String generateWebhookUrl(String ngrokUrl) {
        return String.format("https://api.telegram.org/bot%s/setWebhook?url=%s/updates/v1/%s",
                token, ngrokUrl.replace("http", "https"), token);
    }

    private String generateWebhookUrl(String prodHost, String prodPort) {
        return String.format("https://api.telegram.org/bot%s/setWebhook?url=%s/updates/v1/%s",
                token, String.format("https://%s:%s", prodHost, prodPort), token);
    }

    private String startNgrok() {
        final NgrokClient ngrokClient = new NgrokClient.Builder().build();
        final CreateTunnel createTunnel = new CreateTunnel.Builder()
                .withAddr(serverLocalPort)
                .build();
        final Tunnel httpTunnel = ngrokClient.connect(createTunnel);
        return httpTunnel.getPublicUrl();
    }

    private ResponseEntity getWebhookInfo() {
        ResponseEntity response = new RestTemplate().getForEntity(
                String.format("https://api.telegram.org/bot%s/getWebhookInfo", token),
                Object.class
        );
        return response;
    }

    private boolean isAlreadyRegisterProdHost(Object body) {
        ObjectNode node = mapper.valueToTree(body);
        BooleanNode ok = (BooleanNode) node.at("/ok");
        TextNode url = (TextNode) node.at("/result/url");
        return ok.asBoolean() && url.asText().contains(prodHost);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
