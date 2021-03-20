package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponDetailsEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CouponDetailsEntity getCouponByCouponName(final String couponName) throws CouponNotFoundException {
        try {
            return entityManager.createNamedQuery("getCouponByName", CouponDetailsEntity.class).setParameter("coupon_name", couponName).getSingleResult();
        } catch (NoResultException nre) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }
    }
}
