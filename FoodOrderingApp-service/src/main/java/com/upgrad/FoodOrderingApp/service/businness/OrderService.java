package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
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
    public OrderEntity saveOrder(BigDecimal bill, String couponUuid, BigDecimal discount, String paymentUuid, CustomerEntity customer, String addressUuid, String restaurantUuid) throws CouponNotFoundException, AddressNotFoundException, AuthorizationFailedException, PaymentMethodNotFoundException, RestaurantNotFoundException {
        CouponEntity coupon = couponDao.getCouponByCouponUuid(couponUuid);
        AddressEntity address = addressDao.getAddressByUuid(addressUuid);
        PaymentEntity payment = paymentDao.getPaymentByUuid(paymentUuid);
        RestaurantEntity restaurant = restaurantDao.getRestaurantByUuid(restaurantUuid);

        if(coupon == null) {
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
        }
        if (address == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }
        /** TBD **/
        //Change this from Many to One address
//        if(!address.getCustomers().get(0).equals(customer)){
//            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
//        }
        if (payment == null) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }
        if(restaurant == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }

        OrderEntity order = new OrderEntity();

        order.setBill(bill);
        order.setCoupon(coupon);
        order.setDiscount(discount);
        order.setDate(new Date());
        order.setPayment(payment);
        order.setCustomer(customer);
        order.setAddress(address);
        order.setRestaurant(restaurant);

        order.setUuid(UUID.randomUUID().toString());
        orderDao.saveOrder(order);

        return order;
    }

    public CouponEntity getCouponByCouponName(final String couponName) throws CouponNotFoundException {
        if (couponName.isEmpty()) {
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }

        CouponEntity coupon = couponDao.getCouponByCouponName(couponName);
        if(coupon == null){
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }

        return coupon;
    }

    public List<OrderEntity> getAllOrdersOfCustomer(final CustomerEntity customerEntity) {
        return orderDao.getAllOrdersOfCustomerByUuid(customerEntity.getUuid());
    }

    @Transactional
    public void saveOrderItem(OrderItemEntity orderItemEntity) {
        orderItemDao.saveOrderItem(orderItemEntity);
    }
}
