package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.service.businness.CategoryBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/")
public class CategoryController {

    @Autowired
    CategoryBusinessService categoryBusinessService;

    @RequestMapping(method = RequestMethod.GET,
            value = "/category",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CategoryListResponse>> getAllCategories() {

        //Get all categories as a list of CategoryEntity
        List<CategoryEntity> categories = categoryBusinessService.getAllCategories();

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
}
