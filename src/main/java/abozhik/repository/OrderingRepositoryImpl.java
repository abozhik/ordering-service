package abozhik.repository;

import abozhik.DataSource;
import abozhik.mapper.OrderingMapper;
import abozhik.model.Ordering;
import abozhik.util.JdbcUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Optional;

@RequiredArgsConstructor
public class OrderingRepositoryImpl implements OrderingRepository {

    private static final Logger logger = LoggerFactory.getLogger(OrderingRepositoryImpl.class);

    private final OrderingItemsRepository orderingItemsRepository;
    private final OrderingMapper orderingMapper;
    private final DataSource dataSource;

    @SneakyThrows
    public long saveOrdering(Ordering ordering) {
        var connection = dataSource.getConnection();
        try {
            var ps = connection.prepareStatement("INSERT INTO ordering (user_name) VALUES (?) RETURNING id");
            ps.setString(1, ordering.getUserName());
            var orderingId = JdbcUtils.getLongValue(ps.executeQuery());

            if (ordering.getOrderingItemList() != null) {
                for (var orderingItem : ordering.getOrderingItemList()) {
                    orderingItem.setOrderingId(orderingId);
                    orderingItemsRepository.saveOrderingItem(connection, orderingItem);
                }
            }

            connection.commit();
            return orderingId;
        } catch (SQLException e) {
            logger.error("Error during saving ordering", e);
            connection.rollback();
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }
    }

    public void setAllOrderingDone() {
        try (var connection = dataSource.getConnection()) {
            try (var s = connection.prepareStatement("update ordering set done= true where id in" +
                    " (select id from ordering where done = false for update skip locked limit 100);")) {
                int res = s.executeUpdate();
                while (res > 0) {
                    res = s.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                logger.error("Error during updating 'done' field in 'ordering' table", e);
                connection.rollback();
            }
        } catch (SQLException e) {
            logger.error("Error during getting database connection", e);
        }
    }

    public Optional<Ordering> getOrderingWithItems(Long orderingId) {
        try (var connection = dataSource.getConnection()) {
            var ps = connection.prepareStatement("select * from ordering " +
                    "join ordering_items oi on ordering.id = oi.ordering_id where ordering.id = ?");
            ps.setLong(1, orderingId);
            var resultSet = ps.executeQuery();
            return orderingMapper.map(resultSet);
        } catch (SQLException e) {
            logger.error("Error during getting ordering", e);
        }
        return Optional.empty();
    }

}
