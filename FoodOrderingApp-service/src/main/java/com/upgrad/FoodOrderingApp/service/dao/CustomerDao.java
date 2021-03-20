package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @param customerEntity
     * @return
     */
    public CustomerEntity createCustomer(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    /**
     * @param contactNumber
     * @return
     */
    public CustomerEntity getCustomerByContactNumber(final String contactNumber) {
        try {
            return entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class)
                    .setParameter("contact_number", contactNumber).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * @param userAuthTokenEntity
     * @return
     */
    public CustomerAuthEntity createAuthToken(final CustomerAuthEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    public CustomerEntity updateCustomer(CustomerEntity customerToBeUpdated) {
        entityManager.merge(customerToBeUpdated);
        return customerToBeUpdated;
    }

    //To get Customer By Uuid if no results return null
    public CustomerEntity getCustomerByUuid(final String uuid) {
        try {
            CustomerEntity customer = entityManager.createNamedQuery("customerByUuid",
                    CustomerEntity.class).setParameter("uuid", uuid).getSingleResult();
            return customer;
        } catch (NoResultException nre) {
            return null;
        }
    }
}
