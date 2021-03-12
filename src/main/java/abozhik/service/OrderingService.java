package abozhik.service;

import abozhik.DbConnection;
import abozhik.model.Ordering;
import abozhik.model.OrderingItem;
import abozhik.repository.OrderingItemsRepository;
import abozhik.repository.OrderingRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class OrderingService {

    private final OrderingRepository orderingRepository = new OrderingRepository();
    private final OrderingItemsRepository orderingItemsRepository = new OrderingItemsRepository();

    public Long createOrdering(Ordering ordering) {
       return orderingRepository.saveOrdering(ordering);
    }

    public Long addItemToOrdering(Long orderingId, OrderingItem orderingItem) throws SQLException {
        try(Connection connection = DbConnection.getConnection()) {
            orderingItem.setOrderingId(orderingId);
            return orderingItemsRepository.saveOrderingItem(connection, orderingItem);
        }
    }

    public void changeItemCount(Long orderingItemId, Long itemCount) {
        orderingItemsRepository.updateItemCount(orderingItemId, itemCount);
    }

    public Ordering getOrdering(Long orderingId) {
        return orderingRepository.getOrderingWithItems(orderingId);
    }

    public void setAllOrderingDone() {
        orderingRepository.setAllOrderingDone();
    }

}
