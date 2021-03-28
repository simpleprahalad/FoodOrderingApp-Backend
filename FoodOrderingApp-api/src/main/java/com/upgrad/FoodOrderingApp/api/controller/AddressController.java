package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
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
    private AddressService addressService;

    /**
     * @param authorization
     * @param saveAddressRequest
     * @return
     * @throws AuthorizationFailedException
     * @throws SaveAddressException
     * @throws AddressNotFoundException
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/address",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") final String authorization,
                                                           @RequestBody(required = false) final SaveAddressRequest saveAddressRequest)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {
        //Access the accessToken from the request Header
        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        final AddressEntity addressEntity = new AddressEntity(UUID.randomUUID().toString(),
                saveAddressRequest.getFlatBuildingName(),
                saveAddressRequest.getLocality(),
                saveAddressRequest.getCity(),
                saveAddressRequest.getPincode(),
                addressService.getStateByUUID(saveAddressRequest.getStateUuid()));
        addressEntity.setActive(1);
        
        final AddressEntity savedAddress = addressService.saveAddress(customerEntity, addressEntity);
        final SaveAddressResponse saveAddressResponse = new SaveAddressResponse()
                .id(savedAddress.getUuid())
                .status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<>(saveAddressResponse, HttpStatus.CREATED);
    }

    /**
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/address/customer",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAddressOfCustomer(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        String accessToken = authorization.split("Bearer ")[1];
        final CustomerEntity customer = customerService.getCustomer(accessToken);
        final List<AddressEntity> addresses = addressService.getAllAddress(customer);
        AddressListResponse addressListResponse = new AddressListResponse();
        for (AddressEntity a : addresses) {
            AddressListState state = new AddressListState().
                    id(UUID.fromString((a.getState().getUuid()))).
                    stateName(a.getState().getStateName());

            AddressList address = new AddressList().
                    id(UUID.fromString(a.getUuid())).
                    flatBuildingName(a.getFlatBuilNo()).
                    locality(a.getLocality()).
                    city(a.getCity()).
                    pincode(a.getPincode()).
                    state(state);

            addressListResponse.addAddressesItem(address);
        }
        return new ResponseEntity<>(addressListResponse, HttpStatus.OK);
    }

    /**
     * @param authorization
     * @param addressId
     * @return
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/address/{address_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@RequestHeader("authorization") final String authorization,
                                                               @PathVariable("address_id") final String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {
        final String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customer = customerService.getCustomer(accessToken);

        final AddressEntity addressToBeDeleted = addressService.getAddressByUUID(addressId, customer);
        final AddressEntity deletedAddress = addressService.deleteAddress(addressToBeDeleted);

        final DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse()
                .id(UUID.fromString(deletedAddress.getUuid()))
                .status("ADDRESS DELETED SUCCESSFULLY");
        return new ResponseEntity<>(deleteAddressResponse, HttpStatus.OK);
    }

    /**
     * @return
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/states",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getAllStates() {
        final List<StatesList> states = addressService.getAllStates()
                .stream()
                .flatMap((Function<StateEntity, Stream<StatesList>>) stateEntity -> Stream.of(new StatesList()
                        .id(UUID.fromString(stateEntity.getUuid()))
                        .stateName(stateEntity.getStateName())))
                .collect(Collectors.toList());

        //Strange to return null rather than empty list, just to satisfy a weird unit test case
        final StatesListResponse statesListResponse;
        if (states.isEmpty()) {
            statesListResponse = new StatesListResponse().states(null);
        } else {
            statesListResponse = new StatesListResponse().states(states);
        }
        return new ResponseEntity<>(statesListResponse, HttpStatus.OK);
    }
}
