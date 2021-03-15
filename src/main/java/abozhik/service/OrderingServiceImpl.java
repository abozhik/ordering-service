package abozhik.service;

import abozhik.DataSource;
import abozhik.model.Ordering;
import abozhik.model.OrderingItem;
import abozhik.repository.OrderingItemsRepository;
import abozhik.repository.OrderingRepository;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.Optional;

@RequiredArgsConstructor
public class OrderingServiceImpl implements OrderingService {

    private final OrderingRepository orderingRepository;
    private final OrderingItemsRepository orderingItemsRepository;
    private final DataSource dataSource;

    public long createOrdering(Ordering ordering) {
       return orderingRepository.saveOrdering(ordering);
    }

    public long addItemToOrdering(Long orderingId, OrderingItem orderingItem) throws SQLException {
        try(var connection = dataSource.getConnection()) {
            orderingItem.setOrderingId(orderingId);
            return orderingItemsRepository.saveOrderingItem(connection, orderingItem);
        }
    }

    public void changeItemCount(Long orderingItemId, Long itemCount) {
        orderingItemsRepository.updateItemCount(orderingItemId, itemCount);
    }

    public Optional<Ordering> getOrdering(Long orderingId) {
        return orderingRepository.getOrderingWithItems(orderingId);
    }

    public void setAllOrderingDone() {
        orderingRepository.setAllOrderingDone();
    }

}
