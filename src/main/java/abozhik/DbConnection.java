package abozhik;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {

    private static final Properties properties = new Properties();
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        File file = new File(System.getProperty("user.dir") + "/src/main/resources/db.properties");
        try {
            properties.load(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        config.setJdbcUrl((String) properties.get("url"));
        config.setUsername((String) properties.get("user"));
        config.setPassword((String) properties.get("password"));
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource( config );
    }


    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
