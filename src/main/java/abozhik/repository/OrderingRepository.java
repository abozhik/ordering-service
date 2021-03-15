package abozhik.repository;

import abozhik.model.Ordering;

import java.util.Optional;

public interface OrderingRepository {

    long saveOrdering(Ordering ordering);

    void setAllOrderingDone();

    Optional<Ordering> getOrderingWithItems(Long orderingId);

}
