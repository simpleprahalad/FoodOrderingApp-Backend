package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
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
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ItemService itemService;

    @Autowired

    /**
     *
     * Get all categories saved in the database
     * @Return ResponseEntity of type CategoriesListResponse
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/category",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories() {

        //Get all categories as a list of CategoryEntity
        List<CategoryEntity> categories = categoryService.getAllCategoriesOrderedByName();

        //Declare response categoriesListResponse object
        CategoriesListResponse categoriesListResponse = new CategoriesListResponse();

        //Declare & populate list of CategoryListResponse
        List<CategoryListResponse> allCategoryResponseList = new ArrayList<>();
        for (CategoryEntity categoryEntity: categories) {
            CategoryListResponse categoryListResponse = new CategoryListResponse();
            UUID uuid = UUID.fromString(categoryEntity.getUuid());
            categoryListResponse.id(uuid);
            categoryListResponse.categoryName(categoryEntity.getCategoryName());
            allCategoryResponseList.add(categoryListResponse);
        }

        if (!allCategoryResponseList.isEmpty()) {
            //Add list of CategoryListResponse to CategoriesListResponse
            categoriesListResponse.categories(allCategoryResponseList);
        }
        return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
    }

    /**
     * Get all categories by category UUId with all related items object
     * @param categoryId
     * @return ResponseEntity of type CategoryDetailsResponse
     * @throws CategoryNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/category/{category_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable("category_id") final String categoryId)
     throws CategoryNotFoundException {

        //Get all categories as a list of CategoryEntity
        CategoryEntity category = categoryService.getCategoryById(categoryId);

        //Get all items belong to this category
        List<ItemEntity> items = category.getItems();

        //Declare and initialize of CategoryDetailsResponse
        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse();
        UUID uuid = UUID.fromString(category.getUuid());
        categoryDetailsResponse.setId(uuid);
        categoryDetailsResponse.setCategoryName(category.getCategoryName());
        final List<ItemList> itemLists = new ArrayList<>(items.size());
        for (ItemEntity item : items) {
            RestaurantController.populateItemListObject(itemLists, item);
        }
        categoryDetailsResponse.setItemList(itemLists);

        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);
    }
}
