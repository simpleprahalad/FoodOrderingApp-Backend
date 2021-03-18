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

    @RequestMapping(method = RequestMethod.GET,
            value = "/category",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CategoryListResponse>> getAllCategories() {

        //Get all categories as a list of CategoryEntity
        List<CategoryEntity> categories = categoryService.getAllCategoriesOrderedByName();

        //Declare list of CategoriesListResponse
        List<CategoryListResponse> allCategoriesResponseList = new ArrayList<>();

        for (CategoryEntity categoryEntity: categories) {
            CategoryListResponse categoryResponse = new CategoryListResponse();
            UUID uuid = UUID.fromString(categoryEntity.getUuid());
            categoryResponse.setId(uuid);
            categoryResponse.setCategoryName(categoryEntity.getCategoryName());
            allCategoriesResponseList.add(categoryResponse);
        }

        return new ResponseEntity<List<CategoryListResponse>>(allCategoriesResponseList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/category/{category_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable("category_id") final String categoryId)
     throws CategoryNotFoundException {

        //Throw exception if category is null
        if(categoryId == null) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        //Get all categories as a list of CategoryEntity
        CategoryEntity category = categoryService.getCategoryById(categoryId);
        if(category == null) {
            //Throw exception if there are no categories available by the id provided
            throw new CategoryNotFoundException( "CNF-002", "No category by this id");
        }

        //Get all items belong to this category
        List<ItemEntity> items = category.getItems();

        //Declare and initialize of CategoryDetailsResponse
        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse();
        UUID uuid = UUID.fromString(category.getUuid());
        categoryDetailsResponse.setId(uuid);
        categoryDetailsResponse.setCategoryName(category.getCategoryName());
        final List<ItemList> itemLists = new ArrayList<>(items.size());
        for (ItemEntity item : items) {
            ItemList itemList = new ItemList();
            UUID itemUuid = UUID.fromString(item.getUuid());
            itemList.setId(itemUuid);
            itemList.setItemName(item.getItemName());
            itemList.setPrice(item.getPrice());
            itemList.setItemType(ItemList.ItemTypeEnum.fromValue(item.getType().getValue()));
            itemLists.add(itemList);
        }
        categoryDetailsResponse.setItemList(itemLists);

        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);
    }
}
