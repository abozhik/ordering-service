package abozhik.reader;

import abozhik.repository.OrderingItemsRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class PropertyReader {

    private final Logger logger = LoggerFactory.getLogger(OrderingItemsRepositoryImpl.class);

    private final Properties properties;

    public PropertyReader() {
        properties = new Properties();
    }

    public DatabaseCredentials getDatabaseCredentials(String recourseName) {
        var systemResource = ClassLoader.getSystemResource(recourseName);
        try {
            var file = new File(systemResource.toURI());
            properties.load(new FileReader(file));
            return new DatabaseCredentials(
                    (String) properties.get("url"),
                    (String) properties.get("user"),
                    (String) properties.get("password")
            );
        } catch (IOException | URISyntaxException e) {
            logger.error("Error during getting properties", e);
            throw new RuntimeException(e);
        }
    }
}
