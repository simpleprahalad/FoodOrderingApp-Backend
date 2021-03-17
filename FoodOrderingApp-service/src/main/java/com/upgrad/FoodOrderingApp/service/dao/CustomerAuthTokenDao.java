package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerAuthTokenDao {
    @PersistenceContext
    private EntityManager entityManager;

    public CustomerAuthTokenEntity getCustomerAuthTokenByAccessToken(final String authorization) {
        try {
            return entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthTokenEntity.class)
                    .setParameter("accessToken", authorization).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void updateCustomerAuthToken (final CustomerAuthTokenEntity customerAuthTokenEntity) {
        entityManager.merge(customerAuthTokenEntity);
    }
}
