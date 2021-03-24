package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class OrderItemDao {
    @PersistenceContext
    private EntityManager entityManager;

    public void saveOrderItem(OrderItemEntity orderItemEntity){
        entityManager.persist(orderItemEntity);
    }
}
