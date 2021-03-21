package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private OrderDao orderDao;

    public CouponEntity getCouponByCouponName(final String couponName) throws CouponNotFoundException {
        return couponDao.getCouponByCouponName(couponName);
    }

    public CouponEntity getCouponByCouponId(final UUID couponId) throws CouponNotFoundException {
        return couponDao.getCouponByCouponId(couponId);
    }

    public List<OrdersEntity> getAllOrdersOfCustomer(final CustomerEntity customerEntity) {
        return orderDao.getAllOrdersOfCustomerByUuid(customerEntity.getUuid());
    }
}
