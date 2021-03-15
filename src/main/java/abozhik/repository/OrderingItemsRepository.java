package abozhik.repository;

import abozhik.model.OrderingItem;

import java.sql.Connection;

public interface OrderingItemsRepository {

    long saveOrderingItem(Connection connection, OrderingItem orderingItem);

    void updateItemCount(Long orderingItemId, Long itemCount);

}
