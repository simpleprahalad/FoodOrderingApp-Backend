package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public CouponEntity getCouponByCouponName(final String couponName) throws CouponNotFoundException {
        return couponDao.getCouponByCouponName(couponName);
    }

    public List<OrdersEntity> getAllOrdersOfCustomer(final CustomerEntity customerEntity) {
        return orderDao.getAllOrdersOfCustomerByUuid(customerEntity.getUuid());
    }

    public String saveOrder(final CustomerEntity customer, final String addressId, final List<ItemEntity> itemQuantities,
                            final UUID restaurantId, final UUID paymentId, final UUID couponId, final BigDecimal bill,
                            final BigDecimal discount)
            throws AddressNotFoundException, AuthorizationFailedException, CouponNotFoundException,
            PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException {

        validateCouponValidity(couponId.toString());
        validateIfAddressIsPresent(addressId);

        //TODO: Below code is yet to be verified.
        validateIfAddressPresentBelongsToCustomer(customer, addressId);
        validatePaymentMethod(paymentId);
        validateIfRestaurantIsPresent(restaurantId);
        validateIfItemsArePresent(itemQuantities);

        //TODO: orderDao.saveOrder

        return "";
    }

    private void validateIfItemsArePresent(final List<ItemEntity> itemQuantities) throws ItemNotFoundException {
        for (ItemEntity itemEntity : itemQuantities) {
            if (null == itemDao.getItemByItemId(UUID.fromString(itemEntity.getUuid()))) {
                throw new ItemNotFoundException("INF-003", "No item by this id exist");
            }
        }
    }

    private void validateIfRestaurantIsPresent(final UUID restaurantId) throws RestaurantNotFoundException {
        if (null == restaurantDao.getRestaurantByUuid(restaurantId.toString())) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }
    }

    private void validatePaymentMethod(UUID paymentId) throws PaymentMethodNotFoundException {
        if (null == paymentDao.getPaymentMethodByUuid(paymentId)) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }
    }

    private void validateCouponValidity(final String couponId) throws CouponNotFoundException {
        final CouponEntity couponByCouponId = couponDao.getCouponByCouponId(couponId);
        if (null == couponByCouponId) {
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
        }
    }

    private void validateIfAddressPresentBelongsToCustomer(final CustomerEntity customer, final String addressId)
            throws AuthorizationFailedException {
        final AddressEntity dbAddressEntity = addressDao.getAddressesByUuid(addressId);
        final boolean noAddressMatched = customer.getAddresses()
                .stream()
                .noneMatch(addressEntity -> addressEntity.getUuid() == dbAddressEntity.getUuid());
        if (noAddressMatched) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }
    }

    private void validateIfAddressIsPresent(final String addressId) throws AddressNotFoundException {
        if (null == addressDao.getAddressesByUuid(addressId)) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }
    }


//    public CouponEntity getCouponByCouponId(final UUID couponId) throws CouponNotFoundException {
//        return couponDao.getCouponByCouponId(couponId);
//    }

//    private void validateCoupon(final UUID couponId) throws CouponNotFoundException {
//        orderService.getCouponByCouponId(couponId);
//    }


//    private void validatePaymentMethod(final UUID paymentId) throws PaymentMethodNotFoundException {
//        final boolean anyPaymentMethodFound = paymentService.getAllPaymentMethods()
//                .stream()
//                .anyMatch(paymentEntity -> paymentEntity.getUuid() == paymentId);
//
//        if (!anyPaymentMethodFound) {
//            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
//        }
//    }
//
//    private void validateRestaurant(final UUID restaurantId) throws RestaurantNotFoundException {
//        restaurantService.restaurantByUuid(restaurantId.toString());
//    }
//
//
//    private void validateOrderedItem(final List<ItemQuantity> itemQuantities) throws ItemNotFoundException {
//        //TODO:
//        ///item/restaurant/{restaurant_id}
//        //itemService.getAllItemsfromResturant()
//        //Compare if item not found throw exception
//    }


}
