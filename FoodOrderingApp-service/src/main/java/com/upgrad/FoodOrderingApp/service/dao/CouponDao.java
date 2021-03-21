package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Repository
public class CouponDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CouponEntity getCouponByCouponName(final String couponName) throws CouponNotFoundException {
        try {
            return entityManager.createNamedQuery("getCouponByName", CouponEntity.class).setParameter("coupon_name", couponName).getSingleResult();
        } catch (NoResultException nre) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }
    }

    public CouponEntity getCouponByCouponId(final UUID couponId) throws CouponNotFoundException {
        try {
            return entityManager.createNamedQuery("getCouponById", CouponEntity.class).setParameter("coupon_id", couponId).getSingleResult();
        } catch (NoResultException nre) {
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
        }
    }
}
