package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AddressDao {
    @PersistenceContext
    private EntityManager entityManager;

    public String saveAddress(AddressEntity address) {
        entityManager.persist(address);
        return address.getUuid();
    }

    public List<AddressEntity> getAddressesByCustomerUuid(String uuid) {
        List<AddressEntity> customerAddresses = entityManager.createNamedQuery("getAddressesByCustomerUuid", AddressEntity.class)
                .setParameter("customerUuid", uuid).getResultList();
        return customerAddresses;
    }

    public AddressEntity getAddressesByUuid(String addressId) {
        try {
            return entityManager.createNamedQuery("getAddressByUuid", AddressEntity.class).setParameter("uuid", addressId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
