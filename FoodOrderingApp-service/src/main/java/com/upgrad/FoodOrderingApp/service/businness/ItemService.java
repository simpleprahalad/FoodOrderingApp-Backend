package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    RestaurantDao restaurantDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    ItemDao itemDao;

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
}
