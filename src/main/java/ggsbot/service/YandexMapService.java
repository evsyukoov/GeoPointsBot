package ggsbot.service;

import com.jayway.jsonpath.JsonPath;
import ggsbot.errors.YandexMapException;
import ggsbot.handlers.Coordinate;
import ggsbot.states.LocationBotState;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class YandexMapService {

    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(LocationBotState.class);


    private String htmlQuery;


    private String jsonPath;

    @Value("${yandex.html.query}")
    public void setHtmlQuery(String htmlQuery) {
        this.htmlQuery = htmlQuery;
    }

    @Value("${yandex.json.path}")
    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    @Autowired
    public YandexMapService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Coordinate getCoordsFromLink(String link) {
        try {
            logger.info("Start GET request to {}", link);
            Document document = Jsoup.connect(link).get();
            logger.info("Get answer from {}", link);
            Elements els = document.select(htmlQuery);
            String coords = JsonPath.read(els.get(0).data(), jsonPath);
            return new Coordinate(Double.parseDouble(coords.split(",")[1]),
                    Double.parseDouble(coords.split(",")[0]));
        } catch (Exception e) {
            logger.error("Problem with connect or parsing html-content: {}, ex message: {}", link, e.getMessage());
            throw new YandexMapException();
        }
    }
}
