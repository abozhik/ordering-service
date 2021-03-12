package abozhik.repository;

import abozhik.DbConnection;
import abozhik.generator.TestDataGenerator;
import abozhik.model.Ordering;
import abozhik.model.OrderingItem;
import abozhik.service.OrderingService;
import abozhik.util.JdbcUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.List;

public class OrderingRepositoryTest {

    private final OrderingService orderingService = new OrderingService();
    private final TestDataGenerator testDataGenerator = new TestDataGenerator();

    @Before
    public void connectToDb() {
        DbConnection.connectToTest();
    }

    @After
    public void deleteInsertedRows() throws SQLException {
        try (Connection connection = DbConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("delete from ordering_items");
            statement.executeUpdate("delete from ordering");
        }
    }

    @Test
    public void testCreateOrdering() throws SQLException {
        long oldCountOrdering = getCountOrdering();
        long oldCountOrderingItems = getCountOrderingItems();

        Ordering ordering = testDataGenerator.generateOrdering();
        List<OrderingItem> orderingItemList = testDataGenerator.generateOrderingItemList(3);
        ordering.setOrderingItemList(orderingItemList);
        Long orderingId = orderingService.createOrdering(ordering);

        long newCountOrdering = getCountOrdering();
        long newCountOrderingItems = getCountOrderingItems();

        Assert.assertEquals(getCountOrderingItemsByOrderingId(orderingId).intValue(), orderingItemList.size());
        Assert.assertEquals(oldCountOrdering + 1, newCountOrdering);
        Assert.assertEquals(oldCountOrderingItems + orderingItemList.size(), newCountOrderingItems);
    }

    @Test
    public void testAddItemToOrdering() throws SQLException {
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO ordering (user_name) VALUES (?) RETURNING id");) {
            ps.setString(1, testDataGenerator.getRandomString());
            Long orderingId = JdbcUtils.getLongValue(ps.executeQuery());
            long oldCountOrderingItems = getCountOrderingItemsByOrderingId(orderingId);
            OrderingItem orderingItem = testDataGenerator.generateOrderingItem();
            orderingService.addItemToOrdering(orderingId, orderingItem);
            long newCountOrderingItems = getCountOrderingItemsByOrderingId(orderingId);
            Assert.assertEquals(oldCountOrderingItems + 1, newCountOrderingItems);

        }
    }

    @Test
    public void testChangeItemCount() throws SQLException {
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO ordering_items (ordering_id, item_name, item_count, item_price) " +
                             "VALUES (?, ?, ?, ?) RETURNING id");) {

            long oldCount = 123L;
            long newCount = 321L;
            Long orderingId = insertOrdering();

            OrderingItem orderingItem = testDataGenerator.generateOrderingItem();
            ps.setLong(1, orderingId);
            ps.setString(2, orderingItem.getItemName());
            ps.setLong(3, oldCount);
            ps.setBigDecimal(4, orderingItem.getItemPrice());
            long orderingItemId = JdbcUtils.getLongValue(ps.executeQuery());

            orderingService.changeItemCount(orderingItemId, newCount);
            PreparedStatement prepareStatement = connection.prepareStatement("select item_count from ordering_items where id = ?");
            prepareStatement.setLong(1, orderingItemId);
            long itemCountFromDb = JdbcUtils.getLongValue(prepareStatement.executeQuery());
            prepareStatement.close();

            Assert.assertEquals(newCount, itemCountFromDb);
            Assert.assertNotEquals(oldCount, itemCountFromDb);
        }
    }

    @Test
    public void testGetOrdering() {
        Ordering generatedOrdering = testDataGenerator.generateOrdering();
        List<OrderingItem> orderingItemList = testDataGenerator.generateOrderingItemList(3);
        generatedOrdering.setOrderingItemList(orderingItemList);
        Long orderingId = orderingService.createOrdering(generatedOrdering);
        Ordering ordering = orderingService.getOrdering(orderingId);

        Assert.assertEquals(generatedOrdering.getUserName(), ordering.getUserName());

        for (int i = 0; i < ordering.getOrderingItemList().size(); i++) {
            OrderingItem orderingItem = orderingItemList.get(i);
            OrderingItem generatedOrderingItem = generatedOrdering.getOrderingItemList().get(i);
            Assert.assertEquals(generatedOrderingItem.getOrderingId(), orderingItem.getOrderingId());
            Assert.assertEquals(generatedOrderingItem.getItemName(), orderingItem.getItemName());
            Assert.assertEquals(generatedOrderingItem.getItemCount(), orderingItem.getItemCount());
            Assert.assertEquals(generatedOrderingItem.getItemPrice(), orderingItem.getItemPrice());

        }
    }

    @Test
    public void testSetAllOrderingDone() throws SQLException {
        try (Connection connection = DbConnection.getConnection()) {
            insertOrdering();
            orderingService.setAllOrderingDone();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select count(id) from ordering where done=false");
            resultSet.next();
            Assert.assertEquals(0, resultSet.getLong(1));
        }
    }

    private Long insertOrdering() throws SQLException {
        try (Connection connection = DbConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO ordering (user_name) VALUES (?) RETURNING id");
            ps.setString(1, testDataGenerator.getRandomString());
            return JdbcUtils.getLongValue(ps.executeQuery());
        }
    }

    private Long getCountOrdering() throws SQLException {
        try (Connection connection = DbConnection.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select count(id) from ordering");
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    private Long getCountOrderingItems() throws SQLException {
        try (Connection connection = DbConnection.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select count(id) from ordering_items");
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    private Long getCountOrderingItemsByOrderingId(Long orderingId) throws SQLException {
        try (Connection connection = DbConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("select count(id) from ordering_items where ordering_id = ?");
            ps.setLong(1, orderingId);
            return JdbcUtils.getLongValue(ps.executeQuery());
        }
    }

}
