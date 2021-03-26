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
    RestaurantDao restaurantDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    OrderDao orderDao;

    @Autowired
    ItemDao itemDao;

    @Autowired
    OrderItemDao orderItemDao;

    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantUuid, String categoryUuid) {

        //Get RestaurantEntity from restaurant id
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);

        //Get CategoryEntity from category id
        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryUuid);

        List<ItemEntity> itemEntities = new ArrayList<>();
        for (ItemEntity restaurantItemEntity: restaurantEntity.getItems()) {
            for (ItemEntity categoryItemEntity: categoryEntity.getItems()) {
                if(restaurantItemEntity.equals(categoryItemEntity)) {
                    itemEntities.add(restaurantItemEntity);
                }
            }
        }
        return itemEntities;
    }

    public ItemEntity getItemByUuid(String itemUuid) throws ItemNotFoundException {
        ItemEntity item = itemDao.getItemByUuid(itemUuid);
        if(item == null) {
            throw new ItemNotFoundException("INF-003", "No item by this id exist");
        }
        return item;
    }

    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {

        //Get all orders of the given restaurant
        List<OrdersEntity> ordersEntities = orderDao.getAllOrdersRestaurantUuid(restaurantEntity.getUuid());

        Map<String,Integer> itemCountHashMap = new HashMap<>();
        for (OrdersEntity orderedEntity : ordersEntities) {
            //Get order item entity from order's uuid
            List<OrderItemEntity> orderItemEntities = orderItemDao.getOrderItemsByOrderUuid(orderedEntity.getUuid());
            System.out.print("\norder entity"+orderedEntity.getId());
            for (OrderItemEntity orderItemEntity : orderItemEntities) {
                System.out.print("\norder item entity"+orderItemEntity.getItem().getItemName());
                    String itemUUID = orderItemEntity.getItem().getUuid();
                    Integer count = itemCountHashMap.get(itemUUID);
                itemCountHashMap.put(itemUUID, (count == null) ? 1 : count + 1);
            }
        }

        //Get top 5 items id in map
        itemCountHashMap =  getTopCountMap(itemCountHashMap, 5);

        //Populate items from the saved uuid of items
        List<ItemEntity> popularItems = new ArrayList<>();
        for (String id : itemCountHashMap.keySet()) {
            popularItems.add(itemDao.getItemByUuid(id));
        }
        return popularItems;
    }

    private Map<String,Integer> getTopCountMap(Map<String,Integer> map, int limit){

        List<Map.Entry<String,Integer>> list = new ArrayList<>(map.entrySet());

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
            if(index <= limit) {
                sortedByValueMap.put(item.getKey(), item.getValue());
                index++;
            }
            else {
                return sortedByValueMap;
            }
        }
        return sortedByValueMap;
    }
}
