package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
}
