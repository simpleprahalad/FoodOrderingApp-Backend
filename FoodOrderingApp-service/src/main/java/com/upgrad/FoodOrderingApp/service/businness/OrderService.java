package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private OrderDao orderDao; // save order => new OrderEntity fill data.

    @Autowired
    private AddressDao addressDao;  //Create a query based on address uuid.

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Transactional
    public OrderEntity saveOrder(OrderEntity orderEntity)
            throws CouponNotFoundException, AddressNotFoundException, PaymentMethodNotFoundException, RestaurantNotFoundException, AuthorizationFailedException {
        if (orderEntity.getCoupon() == null) {
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
        }

        if (orderEntity.getAddress() == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }

        if (!orderEntity.getAddress().getCustomer().equals(orderEntity.getCustomer())) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }

        if (orderEntity.getPayment() == null) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }

        if (orderEntity.getRestaurant() == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }
        return orderDao.saveOrder(orderEntity);
    }

    public CouponEntity getCouponByCouponName(final String couponName) throws CouponNotFoundException {
        if (couponName.isEmpty()) {
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }

        CouponEntity coupon = couponDao.getCouponByCouponName(couponName);
        if (coupon == null) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }

        return coupon;
    }

    public List<OrderEntity> getOrdersByCustomers(final String customerUuid) {
        return orderDao.getAllOrdersOfCustomerByUuid(customerUuid);
    }

    public CouponEntity getCouponByCouponId(final String couponId)
            throws CouponNotFoundException {
        CouponEntity coupon = couponDao.getCouponByCouponUuid(couponId);
        if (coupon == null) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }
        return coupon;
    }

    @Transactional
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        orderItemDao.saveOrderItem(orderItemEntity);
        return orderItemEntity;
    }
}
