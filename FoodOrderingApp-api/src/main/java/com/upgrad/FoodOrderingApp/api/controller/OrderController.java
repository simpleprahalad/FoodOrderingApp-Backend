package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping
public class OrderController {

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/order/coupon/{coupon_name}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByName(@PathVariable("coupon_name") final String couponName,
                                                                 @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, CouponNotFoundException {

        //Validate customer state
        String accessToken = authorization.split("Bearer ")[1];
        utilityService.validateAccessToken(accessToken);

        final CouponEntity coupon = orderService.getCouponByCouponName(couponName);
        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
        couponDetailsResponse.setId(UUID.fromString(coupon.getUuid()));
        couponDetailsResponse.couponName(coupon.getCouponName());
        couponDetailsResponse.percent(coupon.getPercent());

        return new ResponseEntity<>(couponDetailsResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST,
            path = "/order",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization,
                                                       @RequestBody final SaveOrderRequest saveOrderRequest)
            throws AuthorizationFailedException, AddressNotFoundException, CouponNotFoundException,
            PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException {

        //Validate customer state
        String accessToken = authorization.split("Bearer ")[1];
        final CustomerEntity customer = utilityService.validateAccessToken(accessToken).getCustomer();

        String addressUuid = saveOrderRequest.getAddressId();
        String paymentUuid = saveOrderRequest.getPaymentId().toString();
        BigDecimal bill = saveOrderRequest.getBill();
        BigDecimal discount = saveOrderRequest.getDiscount();
        String couponUuid = saveOrderRequest.getCouponId().toString();
        String restaurantUuid = saveOrderRequest.getRestaurantId().toString();

        OrdersEntity order = orderService.saveOrder(bill, couponUuid, discount, paymentUuid, customer, addressUuid, restaurantUuid);

        //Populating order
        for (ItemQuantity itemQuantity: saveOrderRequest.getItemQuantities()){
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrder(order);
            ItemEntity item = itemService.getItemByUuid(itemQuantity.getItemId().toString());
            orderItemEntity.setItem(item);
            orderItemEntity.setQuantity(itemQuantity.getQuantity());
            orderItemEntity.setPrice(itemQuantity.getPrice());
            orderService.saveOrderItem(orderItemEntity);
        }

        //Return the response payload
        final SaveOrderResponse saveOrderResponse = new SaveOrderResponse();
        saveOrderResponse.setId(order.getUuid());
        saveOrderResponse.status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<>(saveOrderResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET,
            path = "/order",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getPreviousOrders(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        String accessToken = authorization.split("Bearer ")[1];

        //Validate customer state and get it.
        final CustomerAuthEntity customerAuthEntity = utilityService.validateAccessToken(accessToken);
        final CustomerEntity customer = customerAuthEntity.getCustomer();

        final List<OrderList> orderList = orderService.getAllOrdersOfCustomer(customer)
                .stream()
                .sorted(Comparator.comparing(OrdersEntity::getDate)) //Reorder based on date of order creation
                .flatMap((Function<OrdersEntity, Stream<OrderList>>) ordersEntity -> Stream.of(prepareOrderListObject(ordersEntity)))
                .collect(Collectors.toList());

        //Return the response payload
        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();
        customerOrderResponse.setOrders(orderList);
        return new ResponseEntity<>(customerOrderResponse, HttpStatus.OK);
    }

    private OrderList prepareOrderListObject(final OrdersEntity ordersEntity) {
        final OrderList orderList = new OrderList();
        orderList.bill(ordersEntity.getBill());
        orderList.discount(ordersEntity.getDiscount());
        orderList.date(ordersEntity.getDate().toString());

        orderList.coupon(prepareOrderListCoupon(ordersEntity));
        orderList.customer(prepareOrderListCustomer(ordersEntity.getCustomer()));
        orderList.address(prepareOrderListAddress(ordersEntity.getAddress()));

        return orderList;
    }

    private OrderListAddress prepareOrderListAddress(final AddressEntity address) {
        final OrderListAddress orderListAddress = new OrderListAddress();
        orderListAddress.city(address.getCity());
        orderListAddress.flatBuildingName(address.getFlatBuildingName());
        orderListAddress.id(UUID.fromString(address.getUuid()));
        orderListAddress.locality(address.getLocality());
        orderListAddress.pincode(address.getPincode());
        orderListAddress.setState(prepareOrderListAddressState(address.getState()));
        return orderListAddress;
    }

    private OrderListAddressState prepareOrderListAddressState(final StateEntity state) {
        OrderListAddressState orderListAddressState = new OrderListAddressState();
        orderListAddressState.setStateName(state.getStateName());
        orderListAddressState.setId(UUID.fromString(state.getUuid()));
        return orderListAddressState;
    }

    private OrderListCustomer prepareOrderListCustomer(final CustomerEntity customer) {
        final OrderListCustomer orderListCustomer = new OrderListCustomer();
        orderListCustomer.setId(UUID.fromString(customer.getUuid()));
        orderListCustomer.setFirstName(customer.getFirstName());
        orderListCustomer.setLastName(customer.getLastName());
        orderListCustomer.setContactNumber(customer.getContactnumber());
        orderListCustomer.setEmailAddress(customer.getEmail());
        return orderListCustomer;
    }

    private OrderListCoupon prepareOrderListCoupon(final OrdersEntity ordersEntity) {
        final CouponEntity couponEntity = ordersEntity.getCoupon();
        OrderListCoupon orderListCoupon = new OrderListCoupon();
        orderListCoupon.setCouponName(couponEntity.getCouponName());
        orderListCoupon.setId(UUID.fromString(couponEntity.getUuid()));
        orderListCoupon.setPercent(couponEntity.getPercent());
        return orderListCoupon;
    }

}
