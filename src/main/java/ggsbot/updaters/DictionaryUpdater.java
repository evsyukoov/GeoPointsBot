package ggsbot.updaters;

import com.fasterxml.jackson.databind.ObjectMapper;
import ggsbot.dto.Data;
import ggsbot.dto.Feature;
import ggsbot.mappers.DtoToDataMapper;
import ggsbot.model.access.PointDao;
import ggsbot.model.data.Point;
import ggsbot.states.LocationBotState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ConditionalOnProperty(prefix = "update-dictionaries", name = "enable", havingValue = "true")
@Component
public class DictionaryUpdater {

    private final PointDao pointDao;

    private final DtoToDataMapper dtoToDataMapper;

    private final ObjectMapper objectMapper;

    private String dictFilePath;

    @Value("${update-dictionaries.directory}")
    public void setDictFilePath(String dictFilePath) {
        this.dictFilePath = dictFilePath;
    }

    private static final Logger logger = LoggerFactory.getLogger(LocationBotState.class);

    @Autowired
    public DictionaryUpdater(PointDao pointDao,
                             DtoToDataMapper dtoToDataMapper,
                             ObjectMapper objectMapper) {
        this.pointDao = pointDao;
        this.dtoToDataMapper = dtoToDataMapper;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        updateDictionaries();
    }

    public void updateDictionaries() {
        File dir = new File(dictFilePath);
        List<Data> allPoints = new ArrayList<>();
        if (dir.listFiles() == null) {
            logger.error("No files at point dictionary directory");
            return;
        }
        try {
            for (File file : dir.listFiles()) {
                InputStream is = new FileInputStream(file);
                allPoints.add(objectMapper.readValue(is, Data.class));
            }
        } catch (Exception e) {
            logger.error("Error while parsing geoData: ", e);
        }
        logger.info("Parsing geoData was success");
        long startUpdateTime = System.currentTimeMillis();

        List<Point> points = allPoints.stream()
                .map(Data::getFeatures)
                .flatMap(Collection::stream)
                .map(Feature::getAttributes)
                .map(dtoToDataMapper::dtoToPoint)
                .collect(Collectors.toList());
        logger.info("Successfully transform points to ORM model");
        pointDao.savePoints(points);
        logger.info("Successfully update POINTS dictionary. Time ms: {}", System.currentTimeMillis() - startUpdateTime);
    }
}
