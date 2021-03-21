package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    public CouponEntity getCouponByCouponName(final String couponName) throws CouponNotFoundException {
        return orderDao.getCouponByCouponName(couponName);
    }

    public CouponEntity getCouponByCouponId(final UUID couponId) throws CouponNotFoundException {
        return orderDao.getCouponByCouponId(couponId);
    }

    public void getAllOrders(final String customerUuid) {

    }
}
