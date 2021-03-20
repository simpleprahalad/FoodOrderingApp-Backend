package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponDetailsEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    public CouponDetailsEntity getCouponByCouponName(final String couponName) throws CouponNotFoundException {
        return orderDao.getCouponByCouponName(couponName);
    }

    public CouponDetailsEntity getCouponByCouponId(final UUID couponId) throws CouponNotFoundException {
        return orderDao.getCouponByCouponId(couponId);
    }
    
}
