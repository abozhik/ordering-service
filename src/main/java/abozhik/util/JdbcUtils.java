package abozhik.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcUtils {

    public static Long getInsertedId(PreparedStatement ps) {
        try {
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
