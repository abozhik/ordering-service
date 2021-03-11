package abozhik.mapper;

import abozhik.model.Ordering;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderingMapper {

    public Ordering convert(ResultSet resultSet) {
        Ordering ordering = new Ordering();
        try {
            while (resultSet.next()) {

            }
            return ordering;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
