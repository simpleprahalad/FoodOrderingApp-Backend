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

    /**
     *
     * @param address
     * @return AddressEntity
     */
    public AddressEntity saveAddress(final AddressEntity address) {
        entityManager.persist(address);
        return address;
    }

    /**
     *
     * @param customerUuid
     * @return List<AddressEntity>
     */
    public List<AddressEntity> getAddressesByCustomerUuid(final String customerUuid) {
        return entityManager
                .createNamedQuery("getAddressesByCustomerUuid", AddressEntity.class)
                .setParameter("customerUuid", customerUuid).getResultList();
    }

    /**
     *
     * @param addressEntity
     * @return AddressEntity
     */
    public AddressEntity deleteAddress(final AddressEntity addressEntity) {
        entityManager.remove(addressEntity);
        return addressEntity;
    }

    /**
     *
     * @param addressId
     * @return AddressEntity
     */
    public AddressEntity getAddressByUuid(final String addressId) {
        try {
            return entityManager.createNamedQuery("getAddressByUuid", AddressEntity.class).setParameter("uuid", addressId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
