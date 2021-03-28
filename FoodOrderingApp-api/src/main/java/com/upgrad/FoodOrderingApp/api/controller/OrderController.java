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

    /**
     * Depedency of all business service used in order controller/endpoints have been injected
     * using @Autowired annotation
     */
    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private RestaurantService restaurantService;

    /**
     *
     * @param couponName
     * @param authorization
     * @return CouponDetailsResponse with HTTP status OK
     * @throws AuthorizationFailedException
     * @throws CouponNotFoundException
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/order/coupon/{coupon_name}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByName(@PathVariable("coupon_name") final String couponName,
                                                                 @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, CouponNotFoundException {

        //Split the Bearer prefix to extract access token from authorization header
        String accessToken = authorization.split("Bearer ")[1];
        //Validate customer state
        customerService.getCustomer(accessToken);

        //Get coupon by name from order business service
        final CouponEntity coupon = orderService.getCouponByCouponName(couponName);

        //New CouponDetailResponse object is instantiated
        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
        //couponDetailsResponse data is extracted from coupon acquired from business service
        couponDetailsResponse.setId(UUID.fromString(coupon.getUuid()));
        couponDetailsResponse.couponName(coupon.getCouponName());
        couponDetailsResponse.percent(coupon.getPercent());

        return new ResponseEntity<>(couponDetailsResponse, HttpStatus.OK);
    }

    /**
     *
     * @param authorization
     * @param saveOrderRequest
     * @return UUID of saved order entity with HTTP status CREATED
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     * @throws CouponNotFoundException
     * @throws PaymentMethodNotFoundException
     * @throws RestaurantNotFoundException
     * @throws ItemNotFoundException
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST,
            path = "/order",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization,
                                                       @RequestBody final SaveOrderRequest saveOrderRequest)
            throws AuthorizationFailedException, AddressNotFoundException, CouponNotFoundException,
            PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException {

        //Split the Bearer prefix to extract access token from authorization header
        String accessToken = authorization.split("Bearer ")[1];
        //Validate customer state
        final CustomerEntity customer = customerService.getCustomer(accessToken);

        //Data is extracted from SaveOrderRequest object which is received as request body in JSON format
        String addressUuid = saveOrderRequest.getAddressId();
        String paymentUuid = saveOrderRequest.getPaymentId().toString();
        Double bill = saveOrderRequest.getBill().doubleValue();
        Double discount = saveOrderRequest.getDiscount().doubleValue();
        String couponUuid = saveOrderRequest.getCouponId().toString();
        String restaurantUuid = saveOrderRequest.getRestaurantId().toString();

        //Entity objects are fetched from their respective services.
        //Service method calls also raise respective exceptions as per requirement
        CouponEntity coupon = orderService.getCouponByCouponId(couponUuid);
        PaymentEntity payment = paymentService.getPaymentByUUID(paymentUuid);
        AddressEntity address = addressService.getAddressByUUID(addressUuid, customer);
        RestaurantEntity restaurant = restaurantService.restaurantByUUID(restaurantUuid);

        //New order enitity object is created and populated using setters and info extracted and parsed
        // from request object
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

        //Order is saved using saveOrder service method
        OrderEntity savedOrder = orderService.saveOrder(order);

        //Populating order item object from list of ItemQuantity from request object
        //Since an order could have multiple items - info is parsed and saved via a for loop
        for (ItemQuantity itemQuantity : saveOrderRequest.getItemQuantities()) {
            //Data for OrderItemEntity is taken from parsing individual ItemQuantity objects
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrder(order);
            //This method of item service get item based on UUID.
            //This method also raises an exception if the invalid UUID is supplied in incoming request
            ItemEntity item = itemService.getItemByUuid(itemQuantity.getItemId().toString());
            orderItemEntity.setItem(item);
            orderItemEntity.setQuantity(itemQuantity.getQuantity());
            orderItemEntity.setPrice(itemQuantity.getPrice());
            //This method save/persists the order item object thus created
            orderService.saveOrderItem(orderItemEntity);
        }

        //Return the response payload
        final SaveOrderResponse saveOrderResponse = new SaveOrderResponse();
        //Response gives the UUID of the order entity persisted in database for future references
        saveOrderResponse.setId(savedOrder.getUuid());
        saveOrderResponse.status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<>(saveOrderResponse, HttpStatus.CREATED);
    }

    /**
     *
     * @param authorization
     * @return List of past orders sorted by their order date (newest order first) of
     *  logged-in user with HTTP status OK
     * @throws AuthorizationFailedException
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,
            path = "/order",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getPreviousOrders(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        //Split the Bearer prefix to extract access token from authorization header
        String accessToken = authorization.split("Bearer ")[1];
        //Validate customer state and get it.
        final CustomerEntity customer = customerService.getCustomer(accessToken);

        //This method extracts the list of OrderEnitity objects from order service
        //The list is parsed and sorted - newest order first (as per requirement) using streams
        // and helper private method prepareOrderListObject
        final List<OrderList> orderList = orderService.getOrdersByCustomers(customer.getUuid())
                .stream()
                .sorted(Comparator.comparing(OrderEntity::getDate)) //Reorder based on date of order creation
                .flatMap((Function<OrderEntity, Stream<OrderList>>) ordersEntity -> Stream.of(prepareOrderListObject(ordersEntity)))
                .collect(Collectors.toList());


        //Return the response payload
        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();
        customerOrderResponse.orders(orderList);
        return new ResponseEntity<>(customerOrderResponse, HttpStatus.OK);
    }

    /**
     * Helper private method
     * @param orderEntity
     * @return orderList response created /parsed from OrderEntity
     */
    private OrderList prepareOrderListObject(final OrderEntity orderEntity) {
        final OrderList orderList = new OrderList();
        orderList.id(UUID.fromString(orderEntity.getUuid()));
        orderList.bill( BigDecimal.valueOf(orderEntity.getBill()));
        orderList.discount( BigDecimal.valueOf(orderEntity.getDiscount()));
        orderList.date(orderEntity.getDate().toString());

        orderList.coupon(prepareOrderListCoupon(orderEntity));
        orderList.customer(prepareOrderListCustomer(orderEntity.getCustomer()));
        orderList.address(prepareOrderListAddress(orderEntity.getAddress()));

        return orderList;
    }

    /**
     *  Helper private method
     * @param address
     * @return OrderListAddress response created/parsed from AddressEntity
     */
    private OrderListAddress prepareOrderListAddress(final AddressEntity address) {
        final OrderListAddress orderListAddress = new OrderListAddress();
        orderListAddress.city(address.getCity());
        orderListAddress.flatBuildingName(address.getFlatBuilNo());
        orderListAddress.id(UUID.fromString(address.getUuid()));
        orderListAddress.locality(address.getLocality());
        orderListAddress.pincode(address.getPincode());
        orderListAddress.setState(prepareOrderListAddressState(address.getState()));
        return orderListAddress;
    }

    /**
     * Helper private method
     * @param state
     * @return OrderListAddressState response created / pasred from StateEntity
     */
    private OrderListAddressState prepareOrderListAddressState(final StateEntity state) {
        OrderListAddressState orderListAddressState = new OrderListAddressState();
        orderListAddressState.setStateName(state.getStateName());
        orderListAddressState.setId(UUID.fromString(state.getUuid()));
        return orderListAddressState;
    }

    /**
     * Helper private method
     * @param customer
     * @return OrderListCustomer response created / parsed from CustomerEntity
     */
    private OrderListCustomer prepareOrderListCustomer(final CustomerEntity customer) {
        final OrderListCustomer orderListCustomer = new OrderListCustomer();
        orderListCustomer.setId(UUID.fromString(customer.getUuid()));
        orderListCustomer.setFirstName(customer.getFirstName());
        orderListCustomer.setLastName(customer.getLastName());
        orderListCustomer.setContactNumber(customer.getContactnumber());
        orderListCustomer.setEmailAddress(customer.getEmail());
        return orderListCustomer;
    }

    /**
     * Helper private method
     * @param orderEntity
     * @return OrderListCoupon response created / parsed from Coupon Entity
     *  (acquired by coupon getter of orderEntity object - passed as parameter)
     */
    private OrderListCoupon prepareOrderListCoupon(final OrderEntity orderEntity) {
        final CouponEntity couponEntity = orderEntity.getCoupon();
        OrderListCoupon orderListCoupon = new OrderListCoupon();
        orderListCoupon.setCouponName(couponEntity.getCouponName());
        orderListCoupon.setId(UUID.fromString(couponEntity.getUuid()));
        orderListCoupon.setPercent(couponEntity.getPercent());
        return orderListCoupon;
    }

}