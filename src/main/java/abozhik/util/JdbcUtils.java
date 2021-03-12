package abozhik.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcUtils {

    public static long getLongValue(ResultSet resultSet) {
        try (resultSet) {
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
