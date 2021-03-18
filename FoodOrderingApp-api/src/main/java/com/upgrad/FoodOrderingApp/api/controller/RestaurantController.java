package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.ItemBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    CategoryBusinessService categoryBusinessService;

    @Autowired
    RestaurantBusinessService restaurantBusinessService;

    @RequestMapping(method = RequestMethod.GET,
            value = "/restaurant",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<RestaurantList>> getAllRestaurants() {

        //Get all restaurants as a list of RestaurantEntity
        List<RestaurantEntity> restaurants = restaurantBusinessService.getAllRestaurants();

        //Declare list of RestaurantListResponse
        List<RestaurantList> restaurantListResponse = new ArrayList<>();

        for (RestaurantEntity restaurantEntity: restaurants) {
            RestaurantList restaurant = new RestaurantList();
            UUID uuid = UUID.fromString(restaurantEntity.getUuid());
            restaurant.setId(uuid);
            restaurant.setRestaurantName(restaurantEntity.getRestaurantName());
            restaurant.setPhotoURL(restaurantEntity.getPhotoUrl());
            restaurant.setCustomerRating(restaurantEntity.getCustomerRating());
            restaurant.setAveragePrice(restaurantEntity.getAveragePriceForTwo());
            restaurant.setNumberCustomersRated(restaurantEntity.getNumberOfCustomersRated());

            /** TBD **/
            restaurant.setAddress(null);

            //Get Category names of that restaurant
            List<String> categoryNames = categoryBusinessService.getCategoryNamesOfRestaurant(restaurantEntity.getId());
            restaurant.setCategories(String.join(", ", categoryNames));
            restaurantListResponse.add(restaurant);
        }

        return new ResponseEntity<List<RestaurantList>>(restaurantListResponse, HttpStatus.OK);
    }


}
