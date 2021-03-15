package abozhik.mapper;

import abozhik.model.Ordering;
import abozhik.model.OrderingItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderingMapper {

    public Optional<Ordering> map(ResultSet resultSet) {
        Ordering ordering = null;
        List<OrderingItem> orderingItemList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                if (ordering == null) {
                    ordering = new Ordering();
                    ordering.setId(resultSet.getLong(1));
                    ordering.setUserName(resultSet.getString(2));
                    ordering.setDone(resultSet.getBoolean(3));
                    ordering.setOrderingItemList(orderingItemList);
                }
                OrderingItem item = new OrderingItem();
                item.setId(resultSet.getLong(4));
                item.setOrderingId(resultSet.getLong(5));
                item.setItemName(resultSet.getString(6));
                item.setItemCount(resultSet.getLong(7));
                item.setItemPrice(resultSet.getBigDecimal(8));
                orderingItemList.add(item);
            }
            return Optional.ofNullable(ordering);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
}
