package abozhik.generator;

import abozhik.model.Ordering;
import abozhik.model.OrderingItem;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestDataGenerator {

    public Ordering generateOrdering() {
        Ordering ordering = new Ordering();
        ordering.setUserName(getRandomString());
        return ordering;
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
        byte[] array = new byte[10];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    public Integer getRandomInt() {
        Random random = new Random();
        return random.nextInt(1000 - 100) + 100;
    }

}
