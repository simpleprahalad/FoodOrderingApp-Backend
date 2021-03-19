package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
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
    CategoryService categoryService;

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    ItemService itemService;

    @RequestMapping(method = RequestMethod.GET,
            value = "/restaurant",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<RestaurantList>> getAllRestaurants() {

        //Get all restaurants order by rating as a list of RestaurantEntity
        List<RestaurantEntity> restaurants = restaurantService.restaurantsByRating();

        //Declare list of RestaurantListResponse
        return getRestaurantListResponseEntity(restaurants);
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/restaurant/name/{restaurant_name}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<RestaurantList>> getRestaurantsByName(@PathVariable("restaurant_name") final String restaurantName)
    throws RestaurantNotFoundException {
        //Get all restaurants by name order by name as a list of RestaurantEntity
        List<RestaurantEntity> restaurants = restaurantService.restaurantsByName(restaurantName);

        //Declare list of RestaurantListResponse
        return getRestaurantListResponseEntity(restaurants);
    }


    @RequestMapping(method = RequestMethod.GET,
            value = "/restaurant/category/{category_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<RestaurantList>> getRestaurantsByCategory(@PathVariable("category_id") final String categoryId)
            throws CategoryNotFoundException {
        //Get all restaurants by category order by name as a list of RestaurantEntity
        List<RestaurantEntity> restaurants = restaurantService.restaurantsByCategory(categoryId);

        //Declare list of RestaurantListResponse
        return getRestaurantListResponseEntity(restaurants);
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/api/restaurant/{restaurant_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<RestaurantDetailsResponse>> getRestaurantByUuid(@PathVariable("restaurant_id") final String restaurantId)
            throws RestaurantNotFoundException {
        //Get all restaurants by category order by name as a list of RestaurantEntity
        List<RestaurantEntity> restaurants = restaurantService.restaurantByUuid(restaurantId);
        //Declare list of RestaurantListResponse
        return getRestaurantDetailstResponseEntity(restaurants);
    }

    private ResponseEntity<List<RestaurantList>> getRestaurantListResponseEntity(List<RestaurantEntity> restaurants) {
        List<RestaurantList> restaurantListResponse = new ArrayList<>();
        for (RestaurantEntity restaurantEntity: restaurants) {
            RestaurantList restaurant = populateRestaurantListObject(restaurantEntity);
            //Get Category names of that restaurant
            List<CategoryEntity> categoriesList = categoryService.getCategoriesOfRestaurant(restaurantEntity);
            restaurant.setCategories(getCommaSeparatedCategoryName(categoriesList));
            restaurantListResponse.add(restaurant);
        }

        return new ResponseEntity<List<RestaurantList>>(restaurantListResponse, HttpStatus.OK);
    }

    private ResponseEntity<List<RestaurantDetailsResponse>> getRestaurantDetailstResponseEntity(List<RestaurantEntity> restaurants) {
        List<RestaurantDetailsResponse> restaurantDetailsResponse = new ArrayList<>();
        for (RestaurantEntity restaurantEntity: restaurants) {

            RestaurantDetailsResponse restaurantDetails = populateRestaurantDetailsObject(restaurantEntity);
            //Get Category names of that restaurant
            List<CategoryEntity> categoriesEntityList = categoryService.getCategoriesOfRestaurant(restaurantEntity);
            List<CategoryList> categoriesList = new ArrayList<>();
            for (CategoryEntity categoryEntity: categoriesEntityList) {
                CategoryList categoryList = new CategoryList();
                UUID uuid = UUID.fromString(categoryEntity.getUuid());
                categoryList.setId(uuid);
                categoryList.setCategoryName(categoryEntity.getCategoryName());
                final List<ItemList> itemLists = new ArrayList<>(categoryEntity.getItems().size());
                for (ItemEntity item : categoryEntity.getItems()) {
                    populateItemListObject(itemLists, item);
                }
                categoryList.setItemList(itemLists);
                categoriesList.add(categoryList);
            }
            restaurantDetails.setCategories(categoriesList);
            restaurantDetailsResponse.add(restaurantDetails);
        }

        return new ResponseEntity<List<RestaurantDetailsResponse>>(restaurantDetailsResponse, HttpStatus.OK);
    }

    static void populateItemListObject(List<ItemList> itemLists, ItemEntity item) {
        ItemList itemList = new ItemList();
        UUID itemUuid = UUID.fromString(item.getUuid());
        itemList.setId(itemUuid);
        itemList.setItemName(item.getItemName());
        itemList.setPrice(item.getPrice());
        itemList.setItemType(ItemList.ItemTypeEnum.fromValue(item.getType().getValue()));
        itemLists.add(itemList);
    }

    static RestaurantList populateRestaurantListObject(RestaurantEntity restaurantEntity) {
        RestaurantList restaurant = new RestaurantList();
        restaurant.setId(UUID.fromString(restaurantEntity.getUuid()));
        restaurant.setRestaurantName(restaurantEntity.getRestaurantName());
        restaurant.setPhotoURL(restaurantEntity.getPhotoUrl());
        restaurant.setCustomerRating(restaurantEntity.getCustomerRating());
        restaurant.setAveragePrice(restaurantEntity.getAveragePriceForTwo());
        restaurant.setNumberCustomersRated(restaurantEntity.getNumberOfCustomersRated());

        /** TBD **/
        restaurant.setAddress(null);
        return restaurant;
    }

    static RestaurantDetailsResponse populateRestaurantDetailsObject(RestaurantEntity restaurantEntity) {
        RestaurantDetailsResponse restaurantDetails = new RestaurantDetailsResponse();
        restaurantDetails.setId(UUID.fromString(restaurantEntity.getUuid()));
        restaurantDetails.setRestaurantName(restaurantEntity.getRestaurantName());
        restaurantDetails.setPhotoURL(restaurantEntity.getPhotoUrl());
        restaurantDetails.setCustomerRating(restaurantEntity.getCustomerRating());
        restaurantDetails.setAveragePrice(restaurantEntity.getAveragePriceForTwo());
        restaurantDetails.setNumberCustomersRated(restaurantEntity.getNumberOfCustomersRated());

        /** TBD **/
        restaurantDetails.setAddress(null);
        return restaurantDetails;
    }

    private String getCommaSeparatedCategoryName(List<CategoryEntity> categoriesList) {
        List<String> categoryNames = new ArrayList<>();
        categoriesList.forEach(categoryEntity -> {
            categoryNames.add(categoryEntity.getCategoryName());
        });
        return String.join(", ", categoryNames);
    }




}
