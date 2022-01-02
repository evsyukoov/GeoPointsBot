package ggsbot.config;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class Config {

    private String token;

    @Value("${bot.token}")
    public void setToken(String token) {
        this.token = token;
    }

    //для разработки на локальной машине
    @Bean
    @Profile("local")
    public void createWebhook() {
        new RestTemplate().getForEntity(
                generateWebhookUrl(startNgrok()),
                Object.class);
    }

    private String generateWebhookUrl(String ngrokUrl) {
        return String.format("https://api.telegram.org/bot%s/setWebhook?url=%s/updates/v1/%s",
                token, ngrokUrl.replace("http", "https"), token);
    }

    private String startNgrok() {
        final NgrokClient ngrokClient = new NgrokClient.Builder().build();
        final CreateTunnel createTunnel = new CreateTunnel.Builder()
                .withAddr(8080)
                .build();
        final Tunnel httpTunnel = ngrokClient.connect(createTunnel);
        return httpTunnel.getPublicUrl();
    }
}
