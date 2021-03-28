package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ItemService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private OrderItemDao orderItemDao;

    /**
     * Get items by category and restaurant
     * @param restaurantUuid
     * @param categoryUuid
     * @return List of ItemEntity
     */
    public List<ItemEntity> getItemsByCategoryAndRestaurant(final String restaurantUuid, final String categoryUuid) {

        //Get RestaurantEntity from restaurant id
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);

        //Get CategoryEntity from category id
        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryUuid);

        List<ItemEntity> itemEntities = new ArrayList<>();
        for (ItemEntity restaurantItemEntity : restaurantEntity.getItems()) {
            for (ItemEntity categoryItemEntity : categoryEntity.getItems()) {
                if (restaurantItemEntity.equals(categoryItemEntity)) {
                    itemEntities.add(restaurantItemEntity);
                }
            }
        }
        return itemEntities;
    }

    /**
     * Get items by UUID
     * @param itemUuid
     * @return ItemEntity
     * @throws ItemNotFoundException
     */
    public ItemEntity getItemByUuid(final String itemUuid) throws ItemNotFoundException {
        ItemEntity item = itemDao.getItemByUuid(itemUuid);
        if (item == null) {
            throw new ItemNotFoundException("INF-003", "No item by this id exist");
        }
        return item;
    }

    /**
     * Get items by popularity
     * @param restaurantEntity
     * @return List<ItemEntity>
     */
    public List<ItemEntity> getItemsByPopularity(final RestaurantEntity restaurantEntity) {
        //Get all orders of the given restaurant
        List<OrderEntity> ordersEntities = orderDao.getAllOrdersRestaurantUuid(restaurantEntity.getUuid());

        Map<String, Integer> itemCountHashMap = new HashMap<>();
        for (OrderEntity orderedEntity : ordersEntities) {
            //Get order item entity from order's uuid
            List<OrderItemEntity> orderItemEntities = orderItemDao.getOrderItemsByOrderUuid(orderedEntity.getUuid());
            for (OrderItemEntity orderItemEntity : orderItemEntities) {
                String itemUUID = orderItemEntity.getItem().getUuid();
                Integer count = itemCountHashMap.get(itemUUID);
                itemCountHashMap.put(itemUUID, (count == null) ? 1 : count + 1);
            }
        }

        //Get top 5 item ids in sorted order
        List<String> listIdKeys = getTopCountMap(itemCountHashMap, 5);

        //Populate items from the saved uuid of items
        List<ItemEntity> popularItems = new ArrayList<>();
        for (String id : listIdKeys) {
            popularItems.add(itemDao.getItemByUuid(id));
        }
        return popularItems;
    }

    /**
     * Sort map based on the value and get limited items provided to param
     * @param map
     * @param limit
     * @return Map<String, Integer>
     */
    private List<String> getTopCountMap(final Map<String, Integer> map, final int limit) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());

        // Sorting in decreasing order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue().compareTo(o1.getValue()));
            }
        });

        List<String> sortedItemKeys = new ArrayList<>();
        int index = 1;
        //Get top 5 items
        for (Map.Entry<String, Integer> item : list) {
            //Iterate only upto a limit
            if (index <= limit) {
                sortedItemKeys.add(item.getKey());
                index++;
            } else {
                return sortedItemKeys;
            }
        }

        return sortedItemKeys;
    }
}
