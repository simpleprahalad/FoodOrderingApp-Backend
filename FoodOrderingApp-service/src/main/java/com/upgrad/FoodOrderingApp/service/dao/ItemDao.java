package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ItemDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     *
     * @param categoryId
     * @return @return List of Item Entity
     */
    public List<ItemEntity> getItemsForCategory(final String categoryId) {
        try {
            return entityManager.createNamedQuery("itemForCategory", ItemEntity.class)
                    .setParameter("categoryId", categoryId)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
