package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoryDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @return List of all category entities
     */
    public List<CategoryEntity> getAllCategories() {
        try {
            return entityManager.createNamedQuery("allCategories", CategoryEntity.class)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * @return Get category details
     */
    public CategoryEntity getCategoryDetails(final String categoryId) {
        try {
            return entityManager.createNamedQuery("categoryDetails", CategoryEntity.class)
                    .setParameter("categoryId", categoryId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * @return Get category details
     */
    public List<String> getCategoryNamesOfRestaurant(final int restaurantId) {
        try {
            List<CategoryEntity> categoryList = entityManager.createNamedQuery("categoryListOfRestaurant", CategoryEntity.class)
                    .setParameter("restaurantId", restaurantId)
                    .getResultList();
            List<String> categoryNameList = new ArrayList<>();
            for (CategoryEntity category: categoryList) {
                categoryNameList.add(category.getCategoryName());
            }
            return categoryNameList;
        } catch (NoResultException nre) {
            return null;
        }
    }


}
