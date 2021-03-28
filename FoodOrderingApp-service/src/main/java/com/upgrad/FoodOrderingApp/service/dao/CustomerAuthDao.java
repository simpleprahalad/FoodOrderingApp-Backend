package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerAuthDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     *
     * @param authorization
     * @return CustomerAuthEntity
     */
    public CustomerAuthEntity getCustomerAuthTokenByAccessToken(final String authorization) {
        try {
            return entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthEntity.class)
                    .setParameter("access_Token", authorization).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     *
     * @param customerAuthEntity
     * @return CustomerAuthEntity
     */
    public CustomerAuthEntity createCustomerAuth(CustomerAuthEntity customerAuthEntity) {
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }

    /**
     *
     * @param customerAuthEntity
     * @return CustomerAuthEntity
     */
    public CustomerAuthEntity customerLogout(CustomerAuthEntity customerAuthEntity) {
        entityManager.merge(customerAuthEntity);
        return customerAuthEntity;
    }
}
