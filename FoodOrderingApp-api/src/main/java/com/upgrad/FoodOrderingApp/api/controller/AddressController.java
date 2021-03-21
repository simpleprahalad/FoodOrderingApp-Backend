package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.UtilityService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AddressController {

    @Autowired
    private UtilityService utilityService;

    @Autowired
    AddressBusinessService addressBusinessService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/address",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public SaveAddressRequest saveAddress(@RequestHeader("authorization") final String authorization,
                                          final SaveAddressRequest saveAddressRequest) throws AuthorizationFailedException {
        //Access the accessToken from the request Header
        String accessToken = authorization.split("Bearer ")[1];
        CustomerAuthTokenEntity customerAuthTokenEntity = utilityService.getValidCustomerAuthToken(authorization);



        return null;
    }
}
