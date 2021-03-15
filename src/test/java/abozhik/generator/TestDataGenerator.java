package abozhik.generator;

import abozhik.model.Ordering;
import abozhik.model.OrderingItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestDataGenerator {

    public Ordering generateOrdering() {
        Ordering ordering = new Ordering();
        ordering.setUserName(getRandomString());
        return ordering;
    }

    public List<Ordering> generateOrderingList(int size) {
        List<Ordering> orderingList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            orderingList.add(generateOrdering());
        }
        return orderingList;
    }

    public OrderingItem generateOrderingItem() {
        return new OrderingItem(getRandomString(), getRandomInt().longValue(), new BigDecimal(getRandomInt()));
    }

    public List<OrderingItem> generateOrderingItemList(int size) {
        List<OrderingItem> orderingItemList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            orderingItemList.add(generateOrderingItem());
        }
        return orderingItemList;
    }

    public String getRandomString() {
        return java.util.UUID.randomUUID().toString();
    }

    public Integer getRandomInt() {
        Random random = new Random();
        return random.nextInt(1000 - 100) + 100;
    }

}
