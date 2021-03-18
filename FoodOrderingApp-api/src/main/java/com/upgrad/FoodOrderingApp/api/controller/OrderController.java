package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CustomerOrderResponse;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.entity.CouponDetailsEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/order/coupon/{coupon_name}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByName(@PathVariable("coupon_name") final String couponName,
                                                                 @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        final CouponDetailsEntity couponDetails =
                orderService.getCouponByName(couponName, authorization);
        final CouponDetailsResponse couponDetailsResponse = getCouponDetailsResponseBody(couponDetails);
        return new ResponseEntity<>(couponDetailsResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET,
            path = "/order",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getPreviousOrders(@RequestHeader("authorization") final String authorization) {
        //TODO
        //Get customer-id of the currently logged in customer

        //Querry DB

        //Return the response payload
        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();
        return new ResponseEntity<>(customerOrderResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST,
            path = "/order",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization) {
        //TODO
        //Get customer-id of the currently logged in customer

        //Querry DB

        //Return the response payload
        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();
        return new ResponseEntity<>(customerOrderResponse, HttpStatus.OK);
    }

    private CouponDetailsResponse getCouponDetailsResponseBody(final CouponDetailsEntity couponDetailsEntity) {
        final CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
        couponDetailsResponse.setId(couponDetailsEntity.getUuid());
        couponDetailsResponse.couponName(couponDetailsEntity.getCouponName());
        couponDetailsResponse.percent(couponDetailsEntity.getPercent());
        return couponDetailsResponse;
    }
}
