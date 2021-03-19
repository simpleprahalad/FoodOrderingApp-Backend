package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    /**
     * @return List of all restaurants
     */
    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantDao.getAllRestaurants();
    }

    /**
     * @return List of all restaurants
     */
    public List<RestaurantEntity> getRestaurantsByName(final String restaurantName)
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


}
