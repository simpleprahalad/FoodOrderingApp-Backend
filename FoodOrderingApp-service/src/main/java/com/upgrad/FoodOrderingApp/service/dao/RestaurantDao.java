package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @return List of all restaurants entities
     */
    public List<RestaurantEntity> getAllRestaurants() {
        try {
            return entityManager.createNamedQuery("allRestaurants", RestaurantEntity.class)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * @return List of all restaurants entities
     */
    public List<RestaurantEntity> getRestaurantByName(final String restaurantName) {
        try {
            return entityManager.createNamedQuery("restaurantsByName", RestaurantEntity.class)
                    .setParameter("restaurantName", restaurantName)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * @return List of all restaurants entities by id
     */
    public List<RestaurantEntity> getRestaurantByUuid(final String restaurantId) {
        try {
            return entityManager.createNamedQuery("restaurantByUuid", RestaurantEntity.class)
                    .setParameter("restaurantUuid", restaurantId)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
