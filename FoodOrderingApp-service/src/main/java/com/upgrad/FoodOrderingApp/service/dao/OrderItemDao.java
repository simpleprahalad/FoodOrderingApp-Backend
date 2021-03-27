package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderItemDao {
    @PersistenceContext
    private EntityManager entityManager;

    public List<OrderItemEntity> getOrderItemsByOrderUuid(final String orderUuid) {
        try {
            return entityManager.createNamedQuery("getItemsByOrderUuid", OrderItemEntity.class)
                    .setParameter("orderUuid", orderUuid)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void saveOrderItem(OrderItemEntity orderItemEntity){
        entityManager.persist(orderItemEntity);
    }
}
