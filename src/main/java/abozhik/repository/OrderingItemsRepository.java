package abozhik.repository;

import abozhik.DbConnection;
import abozhik.model.OrderingItem;
import abozhik.util.JdbcUtils;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderingItemsRepository {

    @SneakyThrows
    public Long saveOrderingItem(Connection connection, OrderingItem orderingItem) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO ordering_items (ordering_id, item_name, item_count, item_price) VALUES (?, ?, ?, ?) RETURNING id");) {
            ps.setLong(1, orderingItem.getOrderingId());
            ps.setString(2, orderingItem.getItemName());
            ps.setLong(3, orderingItem.getItemCount());
            ps.setBigDecimal(4, orderingItem.getItemPrice());

            return JdbcUtils.getLongValue(ps.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        }
        return null;
    }

    public void updateItemCount(Long orderingItemId, Long itemCount) {
        try (Connection connection = DbConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    "update ordering_items set item_count=? where id=?");
            ps.setLong(1, itemCount);
            ps.setLong(2, orderingItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
