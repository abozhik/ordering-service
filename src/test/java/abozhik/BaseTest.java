package abozhik;

import abozhik.reader.PropertyReader;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {

    private final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    public DataSource dataSource;

    public BaseTest() {
        var propertyReader = new PropertyReader();
        dataSource = new DataSource(propertyReader.getDatabaseCredentials("test.db.properties"));
        flywayMigrations(dataSource);
    }

    private void flywayMigrations(DataSource dataSource) {
        logger.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.clean();
        logger.info("db cleaned");
        flyway.migrate();
        logger.info("db migration finished.");
    }
}
