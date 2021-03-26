package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<OrdersEntity> getAllOrdersOfCustomerByUuid(final String customerUuid) {
        try {
            return entityManager.createNamedQuery("getAllOrdersOfCustomerByUuid", OrdersEntity.class).setParameter("customerUuid", customerUuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public String saveOrder(OrdersEntity order) {
        entityManager.persist(order);
        return order.getUuid();
    }
}
