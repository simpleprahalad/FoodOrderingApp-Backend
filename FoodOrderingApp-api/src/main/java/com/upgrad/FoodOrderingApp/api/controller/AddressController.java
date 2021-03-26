package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@RequestMapping("/")
public class AddressController {

    @Autowired
    private CustomerService customerService;

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

        CustomerAuthEntity customerAuthEntity = customerService.validateAccessToken(accessToken);
        CustomerEntity customer = customerAuthEntity.getCustomer();
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

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/address/customer",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<AddressList> saveAddress(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        String accessToken = authorization.split("Bearer ")[1];
        CustomerAuthEntity customerAuthEntity = customerService.validateAccessToken(accessToken);
        CustomerEntity customer = customerAuthEntity.getCustomer();

        List<AddressEntity> addresses = customer.getAddresses();
        AddressListResponse addressListResponse = new AddressListResponse();

        for (AddressEntity a : addresses) {
            AddressListState state = new AddressListState().
                    id(UUID.fromString((a.getState().getUuid()))).
                    stateName(a.getState().getStateName());

            AddressList address = new AddressList().
                    id(UUID.fromString(a.getUuid())).
                    flatBuildingName(a.getFlatBuildingName()).
                    locality(a.getLocality()).
                    city(a.getCity()).
                    pincode(a.getPincode()).
                    state(state);

            addressListResponse.addAddressesItem(address);
        }
        return addressListResponse.getAddresses();
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/address/{address_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@RequestHeader("authorization") final String authorization,
                                                               @PathVariable("address_id") final String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {
        final String accessToken = authorization.split("Bearer ")[1];
        final CustomerAuthEntity customerAuthEntity = customerService.validateAccessToken(accessToken);
        final CustomerEntity customer = customerAuthEntity.getCustomer();

        final String deletedAddressUuid = addressBusinessService.deleteAddress(customer, addressId);

        final DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse()
                .id(UUID.fromString(deletedAddressUuid))
                .status("ADDRESS DELETED SUCCESSFULLY");
        return new ResponseEntity<>(deleteAddressResponse, HttpStatus.OK);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/states",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getStates() {
        final List<StatesList> states = addressBusinessService.getStateList()
                .stream()
                .flatMap((Function<StateEntity, Stream<StatesList>>) stateEntity -> Stream.of(new StatesList()
                        .id(UUID.fromString(stateEntity.getUuid()))
                        .stateName(stateEntity.getStateName())))
                .collect(Collectors.toList());

        final StatesListResponse statesListResponse = new StatesListResponse().states(states);
        return new ResponseEntity<>(statesListResponse, HttpStatus.OK);
    }
}
