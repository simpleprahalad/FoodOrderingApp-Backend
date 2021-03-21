package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CustomerOrderResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantity;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderRequest;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
public class OrderController {

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/order/coupon/{coupon_name}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByName(@PathVariable("coupon_name") final String couponName,
                                                                 @RequestHeader("authorization") final String accessToken)
            throws AuthorizationFailedException, CouponNotFoundException {

        //Validate customer state
        utilityService.getValidCustomerAuthToken(accessToken);

        if (couponName.isEmpty()) {
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }

        final CouponEntity couponDetails =
                orderService.getCouponByCouponName(couponName);
        final CouponDetailsResponse couponDetailsResponse = getCouponDetailsResponseBody(couponDetails);
        return new ResponseEntity<>(couponDetailsResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST,
            path = "/order",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> saveOrder(@RequestHeader("authorization") final String accessToken,
                                            final SaveOrderRequest saveOrderRequest)
            throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException, PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException {

        //Validate customer state
        utilityService.getValidCustomerAuthToken(accessToken);

        //Validate coupon
        validateCoupon(saveOrderRequest.getCouponId());

        //Validate address
        validateAddress(saveOrderRequest.getAddressId());

        //Validate payment method
        validatePaymentMethod(saveOrderRequest.getPaymentId());

        //validate restaurant
        validateRestaurant(saveOrderRequest.getRestaurantId());

        //Validate ordered items
        validateOrderedItem(saveOrderRequest.getItemQuantities());

        //Update the order
        updateOrder(saveOrderRequest);

        //TODO:
        //Return the response payload
        return new ResponseEntity<>("order-uuid", HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET,
            path = "/order",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getPreviousOrders(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        //Validate customer state
        CustomerAuthTokenEntity validCustomerAuthToken = utilityService.getValidCustomerAuthToken(authorization);
        CustomerEntity customer = validCustomerAuthToken.getCustomer();
        orderService.getAllOrders(customer.getUuid());

        //Return the response payload
        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();
        return new ResponseEntity<>(customerOrderResponse, HttpStatus.OK);
    }

    private CouponDetailsResponse getCouponDetailsResponseBody(final CouponEntity couponEntity) {
        final CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
        couponDetailsResponse.setId(couponEntity.getUuid());
        couponDetailsResponse.couponName(couponEntity.getCouponName());
        couponDetailsResponse.percent(couponEntity.getPercent());
        return couponDetailsResponse;
    }

    private void validateCoupon(final UUID couponId) throws CouponNotFoundException {
        orderService.getCouponByCouponId(couponId);
    }

    private void validateAddress(final String addressId) throws AddressNotFoundException, AuthorizationFailedException {
        //TODO:

        //If the address uuid entered by the customer does not match any address that exists in the database, throw “AddressNotFoundException” with the message code (ANF-003) and message (No address by this id) and their corresponding HTTP status.

        //If the address uuid entered by the customer does not belong to him, throw “AuthorizationFailedException” with the message code (ATHR-004) and message (You are not authorized to view/update/delete any one else's address) and their corresponding HTTP status.
    }


    private void validatePaymentMethod(final UUID paymentId) throws PaymentMethodNotFoundException {
        final boolean anyPaymentMethodFound = paymentService.getAllPaymentMethods()
                .stream()
                .anyMatch(paymentEntity -> paymentEntity.getUuid() == paymentId);

        if (!anyPaymentMethodFound) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }
    }

    private void validateRestaurant(final UUID restaurantId) throws RestaurantNotFoundException {
        restaurantService.restaurantByUuid(restaurantId.toString());
    }


    private void validateOrderedItem(final List<ItemQuantity> itemQuantities) throws ItemNotFoundException {
        //TODO:
        ///item/restaurant/{restaurant_id}
        //itemService.getAllItemsfromResturant()
        //Compare if item not found throw exception
    }


    private void updateOrder(final SaveOrderRequest saveOrderRequest) {
        //TODO:
    }

}
