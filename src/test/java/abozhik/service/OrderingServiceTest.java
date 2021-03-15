package abozhik.service;

import abozhik.BaseTest;
import abozhik.generator.TestDataGenerator;
import abozhik.mapper.OrderingMapper;
import abozhik.repository.OrderingItemsRepositoryImpl;
import abozhik.repository.OrderingRepositoryImpl;
import abozhik.util.JdbcUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderingServiceTest extends BaseTest {

    private final OrderingService orderingService;
    private final TestDataGenerator testDataGenerator;

    public OrderingServiceTest() {
        var orderingMapper = new OrderingMapper();
        var orderingItemsRepository = new OrderingItemsRepositoryImpl(dataSource);
        var orderingRepository = new OrderingRepositoryImpl(orderingItemsRepository, orderingMapper, dataSource);
        orderingService = new OrderingServiceImpl(orderingRepository, orderingItemsRepository, dataSource);
        testDataGenerator = new TestDataGenerator();
    }

    @After
    public void deleteInsertedRows() throws SQLException {
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.executeUpdate("delete from ordering_items");
            connection.commit();
            statement.executeUpdate("delete from ordering");
            connection.commit();
        }
    }

    @Test
    public void testCreateOrdering() throws SQLException {
        var oldCountOrdering = getCountOrdering();
        var oldCountOrderingItems = getCountOrderingItems();

        var ordering = testDataGenerator.generateOrdering();
        var orderingItemList = testDataGenerator.generateOrderingItemList(3);
        ordering.setOrderingItemList(orderingItemList);
        var orderingId = orderingService.createOrdering(ordering);

        var newCountOrdering = getCountOrdering();
        var newCountOrderingItems = getCountOrderingItems();

        Assert.assertEquals(getCountOrderingItemsByOrderingId(orderingId), orderingItemList.size());
        Assert.assertEquals(oldCountOrdering + 1, newCountOrdering);
        Assert.assertEquals(oldCountOrderingItems + orderingItemList.size(), newCountOrderingItems);
    }

    @Test
    public void testAddItemToOrdering() throws SQLException {
        try (var connection = dataSource.getConnection();
             var ps = connection.prepareStatement("INSERT INTO ordering (user_name) VALUES (?) RETURNING id")) {
            ps.setString(1, testDataGenerator.getRandomString());
            var orderingId = JdbcUtils.getLongValue(ps.executeQuery());
            connection.commit();
            var oldCountOrderingItems = getCountOrderingItemsByOrderingId(orderingId);
            var orderingItem = testDataGenerator.generateOrderingItem();
            orderingService.addItemToOrdering(orderingId, orderingItem);
            var newCountOrderingItems = getCountOrderingItemsByOrderingId(orderingId);
            Assert.assertEquals(oldCountOrderingItems + 1, newCountOrderingItems);
        }
    }

    @Test
    public void testChangeItemCount() throws SQLException {
        try (var connection = dataSource.getConnection();
             var ps = connection.prepareStatement(
                     "INSERT INTO ordering_items (ordering_id, item_name, item_count, item_price) " +
                             "VALUES (?, ?, ?, ?) RETURNING id")) {

            var oldCount = 123L;
            var newCount = 321L;
            var orderingId = insertOrdering();

            var orderingItem = testDataGenerator.generateOrderingItem();
            ps.setLong(1, orderingId);
            ps.setString(2, orderingItem.getItemName());
            ps.setLong(3, oldCount);
            ps.setBigDecimal(4, orderingItem.getItemPrice());
            var orderingItemId = JdbcUtils.getLongValue(ps.executeQuery());
            connection.commit();

            orderingService.changeItemCount(orderingItemId, newCount);
            var prepareStatement = connection.prepareStatement("select item_count from ordering_items where id = ?");
            prepareStatement.setLong(1, orderingItemId);
            var itemCountFromDb = JdbcUtils.getLongValue(prepareStatement.executeQuery());
            prepareStatement.close();

            Assert.assertEquals(newCount, itemCountFromDb);
            Assert.assertNotEquals(oldCount, itemCountFromDb);
        }
    }

    @Test
    public void testGetOrdering() {
        var generatedOrdering = testDataGenerator.generateOrdering();
        var orderingItemList = testDataGenerator.generateOrderingItemList(3);
        generatedOrdering.setOrderingItemList(orderingItemList);
        var orderingId = orderingService.createOrdering(generatedOrdering);
        var ordering = orderingService.getOrdering(orderingId);

        Assert.assertTrue(ordering.isPresent());
        Assert.assertEquals(generatedOrdering.getUserName(), ordering.get().getUserName());
        for (int i = 0; i < ordering.get().getOrderingItemList().size(); i++) {
            var orderingItem = orderingItemList.get(i);
            var generatedOrderingItem = generatedOrdering.getOrderingItemList().get(i);
            Assert.assertEquals(generatedOrderingItem.getOrderingId(), orderingItem.getOrderingId());
            Assert.assertEquals(generatedOrderingItem.getItemName(), orderingItem.getItemName());
            Assert.assertEquals(generatedOrderingItem.getItemCount(), orderingItem.getItemCount());
            Assert.assertEquals(generatedOrderingItem.getItemPrice(), orderingItem.getItemPrice());
        }
    }

    @Test
    public void testSetAllOrderingDone() throws SQLException {
        try (var connection = dataSource.getConnection()) {
//            var orderingList = testDataGenerator.generateOrderingList(10000);
            for (int i = 0; i < 1000; i++) {
                insertOrdering();
            }
//            insertOrdering();
            orderingService.setAllOrderingDone();
            try (var statement = connection.createStatement();
                 var resultSet = statement.executeQuery("select count(id) from ordering where done=false")) {
                resultSet.next();
                Assert.assertEquals(0, resultSet.getLong(1));
            }
        }
    }

    private long insertOrdering() throws SQLException {
        try (var connection = dataSource.getConnection();
             var ps = connection
                     .prepareStatement("INSERT INTO ordering (user_name) VALUES (?) RETURNING id")) {
            ps.setString(1, testDataGenerator.getRandomString());
            long orderingId = JdbcUtils.getLongValue(ps.executeQuery());
            connection.commit();
            return orderingId;
        }
    }

    private long getCountOrdering() throws SQLException {
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select count(id) from ordering")) {
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    private long getCountOrderingItems() throws SQLException {
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select count(id) from ordering_items")) {
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    private long getCountOrderingItemsByOrderingId(long orderingId) throws SQLException {
        try (var connection = dataSource.getConnection();
             var ps = connection.prepareStatement("select count(id) from ordering_items where ordering_id = ?")) {
            ps.setLong(1, orderingId);
            return JdbcUtils.getLongValue(ps.executeQuery());
        }
    }

}
