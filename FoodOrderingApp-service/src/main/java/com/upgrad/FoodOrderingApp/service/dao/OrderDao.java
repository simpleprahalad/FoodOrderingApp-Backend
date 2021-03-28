package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     *
     * @param customerUuid
     * @return List<OrderEntity>
     */
    public List<OrderEntity> getAllOrdersOfCustomerByUuid(final String customerUuid) {
        try {
            return entityManager.createNamedQuery("getAllOrdersOfCustomerByUuid", OrderEntity.class).setParameter("customerUuid", customerUuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     *
     * @param restaurantUuid
     * @return List<OrderEntity>
     */
    public List<OrderEntity> getAllOrdersRestaurantUuid(final String restaurantUuid) {
        try {
            return entityManager.createNamedQuery("getAllOrdersByRestaurantUUid", OrderEntity.class).setParameter("restaurantUuid", restaurantUuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     *
     * @param order
     * @return OrderEntity
     */
    public OrderEntity saveOrder(OrderEntity order) {
        entityManager.persist(order);
        return order;
    }
}
