package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.UtilityService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
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
    public SaveAddressResponse saveAddress(@RequestHeader("authorization") final String authorization,
                                          final SaveAddressRequest saveAddressRequest)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {
        //Access the accessToken from the request Header
        String accessToken = authorization.split("Bearer ")[1];
        CustomerAuthTokenEntity customerAuthTokenEntity = utilityService.getValidCustomerAuthToken(accessToken);

        CustomerEntity customer = customerAuthTokenEntity.getCustomer();
        String flatBuildingName = saveAddressRequest.getFlatBuildingName();
        String locality = saveAddressRequest.getLocality();
        String city = saveAddressRequest.getCity();
        String pincode = saveAddressRequest.getPincode();
        String stateUuid = saveAddressRequest.getStateUuid();

        String addressUuid = addressBusinessService.saveAddress(customer, flatBuildingName, locality, city, pincode, stateUuid);
        SaveAddressResponse saveAddressResponse = new SaveAddressResponse();
        saveAddressResponse.id(addressUuid).status("ADDRESS SUCCESSFULLY REGISTERED");

        return saveAddressResponse;
    }
}
