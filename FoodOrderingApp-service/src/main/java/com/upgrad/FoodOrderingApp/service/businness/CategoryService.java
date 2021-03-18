package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    RestaurantCategoryDao restaurantCategoryDao;

    @Autowired
    RestaurantDao restaurantDao;

    /**
     * @return List of all categories
     */
    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        return categoryDao.getAllCategoriesOrderedByName();
    }

    /**
     *
     * @param categoryUuid
     * @return CategoryEntity
     */
    public CategoryEntity getCategoryById(final String categoryUuid) {
        return categoryDao.getCategoryById(categoryUuid);
    }

    /**
     *
     * @param restaurant
     * @return list of category entity
     */
    public List<String> getCategoriesOfRestaurant(RestaurantEntity restaurant){

        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantCategoryDao.getCategoriesOfRestaurant(restaurant);

        List<String> categoriesNameList = new ArrayList<>();
        restaurantCategoryEntities.forEach(restaurantCategoryEntity -> {
            categoriesNameList.add(restaurantCategoryEntity.getCategory().getCategoryName());
        });
        return categoriesNameList;
    }

}
