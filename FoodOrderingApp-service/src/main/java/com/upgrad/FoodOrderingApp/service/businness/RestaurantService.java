package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

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
        if (restaurantName == null || restaurantName == "") {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }

        //Fetch all restaurants by provided name
        List<RestaurantEntity> restaurantEntities = restaurantDao.getRestaurantByName(restaurantName);

        //If there are no restaurants by the name entered by the customer, return an empty list
        if (restaurantEntities == null) {
            return new ArrayList<>();
        } else {
            return restaurantEntities;
        }
    }

    /**
     * @return List of all restaurants by category uuid order by restaurant name
     */
    public List<RestaurantEntity> restaurantByCategory(final String categoryUuid)
            throws CategoryNotFoundException {
        //Throw exception if category is null
        if (categoryUuid == null) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryUuid);
        if (categoryEntity == null) {
            //Throw exception if there are no categories available by the id provided
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }

        return categoryEntity.getRestaurants();
    }

    /**
     * @return Restaurant by id
     */
    public RestaurantEntity restaurantByUUID(final String restaurantId)
            throws RestaurantNotFoundException {
        //If the restaurant id field entered by the customer is empty, throw exception
        if (restaurantId == null) {
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }

        //Fetch restaurant by provided id
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantId);

        //If there are no restaurants by the id entered by the customer, return an empty list
        if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        } else {
            return restaurantEntity;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity, Double customerRating)
            throws InvalidRatingException {

        //Checking the validity of customer rating
        if (customerRating == null || !(customerRating >= 1 && customerRating <= 5)) {
            throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
        }

        Double oldRestaurantRating = restaurantEntity.getCustomerRating();
        Integer oldCustomersRatingCount = restaurantEntity.getNumberCustomersRated();
        //Update number of customer rated
        restaurantEntity.setNumberCustomersRated(oldCustomersRatingCount + 1);

        /**
         * Calculate avg customer rating
         * New Average rating = (Old average Rating * Old count + NewRating)/NewRatingCount
         * **/
        Double newCustomerRating = ((oldRestaurantRating * oldCustomersRatingCount) + customerRating) / (oldCustomersRatingCount + 1);
        restaurantEntity.setCustomerRating(newCustomerRating);

        //Updating restaurant rating
        RestaurantEntity updatedRestaurantEntity = restaurantDao.updateRestaurantRating(restaurantEntity);
        return updatedRestaurantEntity;
    }
}
