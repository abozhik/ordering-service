package abozhik.service;

import abozhik.model.Ordering;
import abozhik.model.OrderingItem;

import java.sql.SQLException;
import java.util.Optional;

public interface OrderingService {

    long createOrdering(Ordering ordering);

    long addItemToOrdering(Long orderingId, OrderingItem orderingItem) throws SQLException;

    void changeItemCount(Long orderingItemId, Long itemCount);

    Optional<Ordering> getOrdering(Long orderingId);

    void setAllOrderingDone();

}
