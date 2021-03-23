package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

@Repository
public class PaymentDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<PaymentEntity> getAllPaymentMethods() {
        try {
            return entityManager.createNamedQuery("allPayments", PaymentEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public PaymentEntity getPaymentMethodByUuid(final UUID paymentId) {
        try {
            return entityManager.createNamedQuery("paymentByUuid", PaymentEntity.class)
                    .setParameter("uuid", paymentId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
