package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private RestaurantCategoryDao restaurantCategoryDao;

    /**
     * @return List of all restaurants order by rating
     */
    public List<RestaurantEntity> restaurantsByRating() {
        return restaurantDao.getAllRestaurants();
    }

    /**
     * @return List of all restaurants by entered input order by restaurant name
     */
    public List<RestaurantEntity> restaurantsByName(final String restaurantName)
    throws RestaurantNotFoundException {
        //If the restaurant name field entered by the customer is empty, throw exception
        if(restaurantName == null || restaurantName == "") {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }

        //Fetch all restaurants by provided name
        List<RestaurantEntity> restaurantEntities = restaurantDao.getRestaurantByName(restaurantName);

        //If there are no restaurants by the name entered by the customer, return an empty list
        if(restaurantEntities == null) {
            return new ArrayList<>();
        }
        else {
            return restaurantEntities;
        }
    }

    /**
     * @return List of all restaurants by category uuid order by restaurant name
     */
    public List<RestaurantEntity> restaurantsByCategory(final String categoryUuid)
            throws CategoryNotFoundException {
        //Throw exception if category is null
        if(categoryUuid == null) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryUuid);
        if(categoryEntity == null) {
            //Throw exception if there are no categories available by the id provided
            throw new CategoryNotFoundException( "CNF-002", "No category by this id");
        }
        //Fetch all restaurants by provided name
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantCategoryDao.getRestaurantsOfCategory(categoryEntity);
        List<RestaurantEntity> restaurants = new ArrayList<>();
        //Retrieve restaurants from list of RestaurantCategoryEntity
        for(RestaurantCategoryEntity restaurantCategoryEntity: restaurantCategoryEntities) {
            restaurants.add(restaurantCategoryEntity.getRestaurant());
        }
        return restaurants;
    }


}
