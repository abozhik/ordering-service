package abozhik.repository;

import abozhik.DbConnection;
import abozhik.mapper.OrderingMapper;
import abozhik.model.Ordering;
import abozhik.model.OrderingItem;
import abozhik.util.JdbcUtils;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderingRepository {

    private final OrderingItemsRepository orderingItemsRepository = new OrderingItemsRepository();
    private final OrderingMapper orderingMapper = new OrderingMapper();

    @SneakyThrows
    public Long saveOrdering(Ordering ordering) {
        Connection connection = DbConnection.getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement("INSERT INTO ordering (user_name) VALUES (?) RETURNING id");
            ps.setString(1, ordering.getUserName());
            Long orderingId = JdbcUtils.getInsertedId(ps);

            for (OrderingItem orderingItem : ordering.getOrderingItemList()) {
                orderingItem.setOrderingId(orderingId);
                orderingItemsRepository.saveOrderingItem(orderingItem);
            }

            connection.commit();
            return orderingId;
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            return null;
        }
    }

    public void setAllOrderingDone() {
        try (Connection connection = DbConnection.getConnection()) {
            PreparedStatement s = connection.prepareStatement("update ordering set done=true where done=false");
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Ordering getOrderingWithItems(Long orderingId) {
        try (Connection connection = DbConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("select * from ordering " +
                    "join ordering_items oi on ordering.id = oi.ordering_id where ordering.id = ?");
            ps.setLong(1, orderingId);
            ResultSet resultSet = ps.executeQuery();
            return orderingMapper.convert(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
