package abozhik.repository;

import abozhik.DataSource;
import abozhik.model.OrderingItem;
import abozhik.util.JdbcUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
public class OrderingItemsRepositoryImpl implements OrderingItemsRepository {

    private final Logger logger = LoggerFactory.getLogger(OrderingItemsRepositoryImpl.class);

    private final DataSource dataSource;

    @SneakyThrows
    public long saveOrderingItem(Connection connection, OrderingItem orderingItem) {
        try (var ps = connection.prepareStatement(
                "INSERT INTO ordering_items (ordering_id, item_name, item_count, item_price) VALUES (?, ?, ?, ?) RETURNING id");) {
            ps.setLong(1, orderingItem.getOrderingId());
            ps.setString(2, orderingItem.getItemName());
            ps.setLong(3, orderingItem.getItemCount());
            ps.setBigDecimal(4, orderingItem.getItemPrice());

            long orderingItemId = JdbcUtils.getLongValue(ps.executeQuery());
            connection.commit();
            return orderingItemId;
        } catch (SQLException e) {
            logger.error("Error during saving ordering item", e);
            connection.rollback();
            throw new RuntimeException(e);
        }
    }

    public void updateItemCount(Long orderingItemId, Long itemCount) {
        try (var connection = dataSource.getConnection()) {
            var ps = connection.prepareStatement(
                    "update ordering_items set item_count=? where id=?");
            ps.setLong(1, itemCount);
            ps.setLong(2, orderingItemId);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            logger.error("Error during updating 'item count' field in 'ordering items' table", e);
        }
    }

}
