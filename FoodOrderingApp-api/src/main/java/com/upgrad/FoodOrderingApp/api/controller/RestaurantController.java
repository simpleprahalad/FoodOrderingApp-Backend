package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    ItemService itemService;

    @Autowired
    CustomerService customerService;

    /**
     * Get all the restaurants in order of their ratings
     * @return ResponseEntity of type RestaurantListResponse
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/restaurant",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {
        //Get all restaurants order by rating as a list of RestaurantEntity
        List<RestaurantEntity> restaurants = restaurantService.restaurantsByRating();
        return getRestaurantListResponseEntity(restaurants);
    }

    /**
     * Get all the restaurants corresponding to the given name will be returned in alphabetical order of their names.
     * List of categories should be displayed in a categories string
     * @param restaurantName
     * @return ResponseEntity of type RestaurantListResponse
     * @throws RestaurantNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/restaurant/name/{restaurant_name}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@PathVariable("restaurant_name") final String restaurantName)
            throws RestaurantNotFoundException {
        //Get all restaurants by name order by name as a list of RestaurantEntity
        List<RestaurantEntity> restaurants = restaurantService.restaurantsByName(restaurantName);
        return getRestaurantListResponseEntity(restaurants);
    }

    /**
     * Get all the restaurants under given category in alphabetical order
     * @param categoryId
     * @return ResponseEntity of type RestaurantListResponse
     * @throws CategoryNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/restaurant/category/{category_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> restaurantByCategory(@PathVariable("category_id") final String categoryId)
            throws CategoryNotFoundException {
        //Get all restaurants by category order by name as a list of RestaurantEntity
        List<RestaurantEntity> restaurants = restaurantService.restaurantByCategory(categoryId);
        return getRestaurantListResponseEntity(restaurants);
    }

    /**
     * If the restaurant id entered by the customer matches any restaurant in the database, it should retrieve that restaurant’s details
     * Result includes category with related items
     * @param restaurantId
     * @return ResponseEntity of type RestaurantDetailsResponse
     * @throws RestaurantNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/restaurant/{restaurant_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantByUuid(@PathVariable("restaurant_id") final String restaurantId)
            throws RestaurantNotFoundException {
        //Get all restaurants by category order by name as a list of RestaurantEntity
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);

        //Declare list of RestaurantListResponse
        RestaurantDetailsResponse restaurantDetails = populateRestaurantDetailsObject(restaurantEntity);
        List<CategoryList> categoriesList = new ArrayList<>();
        //Fetch all category entity by restaurant id
        List<CategoryEntity> categoriesByRestaurant = categoryService.getCategoriesByRestaurant(restaurantId);
        //Iterate through categories
        for (CategoryEntity categoryEntity : categoriesByRestaurant) {
            CategoryList categoryList = new CategoryList();
            UUID uuid = UUID.fromString(categoryEntity.getUuid());
            categoryList.setId(uuid);
            categoryList.setCategoryName(categoryEntity.getCategoryName());
            //Fetch all item entities for given restaurant and with above category
            List<ItemEntity> itemEntities = itemService.getItemsByCategoryAndRestaurant(restaurantId, categoryEntity.getUuid());
            final List<ItemList> itemLists = new ArrayList<>();
            for (ItemEntity item : itemEntities) {
                //Populate item list object
                populateItemListObject(itemLists, item);
            }
            categoryList.setItemList(itemLists);
            categoriesList.add(categoryList);
        }
        restaurantDetails.setCategories(categoriesList);
        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetails, HttpStatus.OK);
    }

    /**
     * If the restaurant id entered by the customer matches any restaurant in the database,
     * It will update that restaurant’s rating in the database along with the number of customers who have rated it.
     * @param authorization
     * @param restaurantUuid
     * @param customerRating
     * @return ResponseEntity of type RestaurantUpdatedResponse
     * @throws AuthorizationFailedException
     * @throws RestaurantNotFoundException
     * @throws InvalidRatingException
     */
    @RequestMapping(method = RequestMethod.PUT,
            path = "/restaurant/{restaurant_id}",
            params = "customer_rating",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(@RequestHeader("authorization") final String authorization,
                                                                             @PathVariable(value = "restaurant_id") final String restaurantUuid,
                                                                             @RequestParam(value = "customer_rating") final Double customerRating)
            throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {
        //Validate authorization code
        String accessToken = authorization.split("Bearer ")[1];
        customerService.getCustomer(accessToken);

        //Get restaurant entity from restaurant id
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);
        //Update rating
        RestaurantEntity updatedRestaurantEntity = restaurantService.updateRestaurantRating(restaurantEntity, customerRating);
        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse();
        restaurantUpdatedResponse.setId(UUID.fromString(restaurantUuid));
        restaurantUpdatedResponse.setStatus("RESTAURANT RATING UPDATED SUCCESSFULLY");

        return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);
    }

    /**
     * Populate RestaurantListResponse object
     * @param restaurants
     * @return ResponseEntity of type RestaurantListResponse
     */
    private ResponseEntity<RestaurantListResponse> getRestaurantListResponseEntity(List<RestaurantEntity> restaurants) {
        List<RestaurantList> restaurantLists = new ArrayList<>();
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();
        for (RestaurantEntity restaurantEntity : restaurants) {
            RestaurantList restaurant = populateRestaurantListObject(restaurantEntity);
            //Get Category names of that restaurant
            List<CategoryEntity> categoriesList = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
            restaurant.setCategories(getCommaSeparatedCategoryName(categoriesList));
            restaurantLists.add(restaurant);
        }
        restaurantListResponse.restaurants(restaurantLists);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    /**
     * Populate item list
     * @param itemLists
     * @param item
     */
    static void populateItemListObject(List<ItemList> itemLists, ItemEntity item) {
        ItemList itemList = new ItemList();
        UUID itemUuid = UUID.fromString(item.getUuid());
        itemList.setId(itemUuid);
        itemList.setItemName(item.getItemName());
        itemList.setPrice(item.getPrice());
        itemList.setItemType(ItemList.ItemTypeEnum.fromValue(item.getType().getValue()));
        itemLists.add(itemList);
    }

    /**
     * Populate restaurant list object
     * @param restaurantEntity
     * @return RestaurantList
     */
    private RestaurantList populateRestaurantListObject(RestaurantEntity restaurantEntity) {
        RestaurantList restaurant = new RestaurantList()
                .id(UUID.fromString(restaurantEntity.getUuid()))
                .restaurantName(restaurantEntity.getRestaurantName())
                .photoURL(restaurantEntity.getPhotoUrl())
                .customerRating(getConvertedRating(restaurantEntity.getCustomerRating()))
                .averagePrice(restaurantEntity.getAvgPrice())
                .numberCustomersRated(restaurantEntity.getNumberCustomersRated());

        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = populateAddressObject(restaurantEntity.getAddress());
        restaurant.setAddress(restaurantDetailsResponseAddress);
        return restaurant;
    }

    /**
     * Populate restaurant details object
     * @param restaurantEntity
     * @return RestaurantDetailsResponse
     */
    private RestaurantDetailsResponse populateRestaurantDetailsObject(RestaurantEntity restaurantEntity) {
        RestaurantDetailsResponse restaurantDetails = new RestaurantDetailsResponse()
                .id(UUID.fromString(restaurantEntity.getUuid()))
                .restaurantName(restaurantEntity.getRestaurantName())
                .photoURL(restaurantEntity.getPhotoUrl())
                .customerRating(getConvertedRating(restaurantEntity.getCustomerRating()))
                .averagePrice(restaurantEntity.getAvgPrice())
                .numberCustomersRated(restaurantEntity.getNumberCustomersRated());

        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = populateAddressObject(restaurantEntity.getAddress());
        restaurantDetails.setAddress(restaurantDetailsResponseAddress);
        return restaurantDetails;
    }

    /**
     * Populate RestaurantDetailsResponseAddress object
     * @param addressEntity
     * @return RestaurantDetailsResponseAddress
     */
    private RestaurantDetailsResponseAddress populateAddressObject(AddressEntity addressEntity) {
        StateEntity stateEntity = addressEntity.getState();
        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState()
                .stateName(stateEntity.getStateName())
                .id(UUID.fromString(stateEntity.getUuid()));
        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
                .id(UUID.fromString(addressEntity.getUuid()))
                .flatBuildingName(addressEntity.getFlatBuilNo())
                .locality(addressEntity.getLocality())
                .city(addressEntity.getCity())
                .pincode(addressEntity.getPincode())
                .state(restaurantDetailsResponseAddressState);
        return restaurantDetailsResponseAddress;
    }

    /**
     * Get comma separated categories
     * @param categoriesList
     * @return String
     */
    private String getCommaSeparatedCategoryName(List<CategoryEntity> categoriesList) {
        List<String> categoryNames = new ArrayList<>();
        categoriesList.forEach(categoryEntity -> {
            categoryNames.add(categoryEntity.getCategoryName());
        });
        return String.join(", ", categoryNames);
    }

    /**
     * Get rating from Double to BigDecimal
     * @param rating
     * @return BigDecimal rating
     */
    private BigDecimal getConvertedRating(double rating) {
        BigDecimal ratingInBigDecimal = BigDecimal.valueOf(rating);
        ratingInBigDecimal = ratingInBigDecimal.setScale(1, BigDecimal.ROUND_UP);
        return ratingInBigDecimal;
    }
}
