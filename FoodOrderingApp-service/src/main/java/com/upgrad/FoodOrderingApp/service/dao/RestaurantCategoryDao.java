package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RestaurantCategoryDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @return Get category names of restaurant
     */
    public List<RestaurantCategoryEntity> getCategoriesOfRestaurant(final RestaurantEntity restaurant) {
        try {
            List<RestaurantCategoryEntity> restaurantCategoryEntities = entityManager.createNamedQuery("categoriesOfRestaurant",RestaurantCategoryEntity.class)
                    .setParameter("restaurant",restaurant)
                    .getResultList();
            return restaurantCategoryEntities;
        }catch (NoResultException nre){
            return null;
        }
    }

    /**
     * @return Get restaurants by category
     */
    public List<RestaurantCategoryEntity> getRestaurantsOfCategory(final CategoryEntity category) {
        try {
            List<RestaurantCategoryEntity> restaurantCategoryEntities = entityManager.createNamedQuery("restaurantsByCategory",RestaurantCategoryEntity.class)
                    .setParameter("category", category)
                    .getResultList();
            return restaurantCategoryEntities;
        }catch (NoResultException nre){
            return null;
        }
    }

}
