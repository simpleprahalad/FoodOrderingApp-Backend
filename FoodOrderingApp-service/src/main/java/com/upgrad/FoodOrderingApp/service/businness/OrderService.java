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

/**
 * Business service for all things related to order and order controller / endpoints
 */
@Service
public class OrderService {

    /**
     * Depedency of all entity dao used in order business service have been injected
     * using @Autowired annotation
     */
    @Autowired
    private CouponDao couponDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    /**
     *
     * @param orderEntity
     * @return saved order entity
     * @throws CouponNotFoundException
     * @throws AddressNotFoundException
     * @throws PaymentMethodNotFoundException
     * @throws RestaurantNotFoundException
     * @throws AuthorizationFailedException
     */
    @Transactional
    public OrderEntity saveOrder(OrderEntity orderEntity)
            throws CouponNotFoundException, AddressNotFoundException, PaymentMethodNotFoundException, RestaurantNotFoundException, AuthorizationFailedException {
        //Series of if to check if for any invalid input data is supplied in response.
        //All if block checks for invalid data and throw appropriate exceptions.
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
        //Save the order entity using orderDao method and returns the saved order
        return orderDao.saveOrder(orderEntity);
    }

    /**
     *
     * @param couponName
     * @return coupon entity extracted from database using coupon name supplied
     *  through request object via controller
     * @throws CouponNotFoundException
     */
    public CouponEntity getCouponByCouponName(final String couponName) throws CouponNotFoundException {
        //If empty string is passed in request object - this exception is thrown
        if (couponName.isEmpty()) {
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }

        //CouponDAO returns null if no coupon by the name is found in database
        CouponEntity coupon = couponDao.getCouponByCouponName(couponName);
        //If invalid coupon name passed in request object - this exception is thrown
        if (coupon == null) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }

        //Coupon is returned if valid coupon is successfully found in database.
        return coupon;
    }

    /**
     *
     * @param customerUuid
     * @return List of orders of a customer whose UUID is passed
     */
    public List<OrderEntity> getOrdersByCustomers(final String customerUuid) {
        return orderDao.getAllOrdersOfCustomerByUuid(customerUuid);
    }

    /**
     *
     * @param couponId
     * @return CouponEntity object whose coupon ID is passed
     * @throws CouponNotFoundException
     */
    public CouponEntity getCouponByCouponId(final String couponId)
            throws CouponNotFoundException {
        //
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
