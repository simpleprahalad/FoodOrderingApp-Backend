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
     * @param itemUuid
     * @return
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
     * @param restaurantEntity
     * @return
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

        //Get top 5 items id in map
        itemCountHashMap = getTopCountMap(itemCountHashMap, 5);

        //Populate items from the saved uuid of items
        List<ItemEntity> popularItems = new ArrayList<>();
        for (String id : itemCountHashMap.keySet()) {
            popularItems.add(itemDao.getItemByUuid(id));
        }
        return popularItems;
    }

    /**
     * @param map
     * @param limit
     * @return
     */
    private Map<String, Integer> getTopCountMap(final Map<String, Integer> map, final int limit) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        // Sorting in decreasing order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue().compareTo(o1.getValue()));
            }
        });

        Map<String, Integer> sortedByValueMap = new HashMap<String, Integer>();
        int index = 1;
        for (Map.Entry<String, Integer> item : list) {
            if (index <= limit) {
                sortedByValueMap.put(item.getKey(), item.getValue());
                index++;
            } else {
                return sortedByValueMap;
            }
        }
        return sortedByValueMap;
    }
}
