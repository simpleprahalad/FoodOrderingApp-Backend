package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.LogoutBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.SignupBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.UtilityService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    SignupBusinessService signupBusinessService;

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private LogoutBusinessService logoutBusinessService;

    private boolean isEmailValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    private boolean isContactNumberValid(String contactNumber) {
        Pattern p = Pattern.compile("\\A[0-9]{10}\\z");
        Matcher m = p.matcher(contactNumber);
        return (m.find() && m.group().equals(contactNumber));
    }

    private boolean isPasswordWeak(String password) {
        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[A-Z])"
                + "(?=.*[#@$%&*!^])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher m = p.matcher(password);

        // Return if the password
        // matched the ReGex
        return m.matches();
    }

    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/customer/signup",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(
            final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        if (signupBusinessService.getCustomerByContactNumber(signupCustomerRequest.getContactNumber()) != null)
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        else if ((signupCustomerRequest.getFirstName() == null) ||
                (signupCustomerRequest.getContactNumber() == null) ||
                (signupCustomerRequest.getEmailAddress() == null) ||
                (signupCustomerRequest.getPassword() == null)) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        } else if (isEmailValid(signupCustomerRequest.getEmailAddress()) == false) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        } else if (isContactNumberValid(signupCustomerRequest.getContactNumber()) == false) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        } else if (isPasswordWeak(signupCustomerRequest.getPassword()) == false) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        } else {
            final CustomerEntity customerEntity = new CustomerEntity();
            customerEntity.setUuid(UUID.randomUUID().toString());
            customerEntity.setFirstname(signupCustomerRequest.getFirstName());
            customerEntity.setLastname(signupCustomerRequest.getLastName());
            customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
            customerEntity.setPassword(signupCustomerRequest.getPassword());
            customerEntity.setContactnumber(signupCustomerRequest.getContactNumber());
            final CustomerEntity createdCustomerEntity = signupBusinessService.signup(customerEntity);
            SignupCustomerResponse customerResponse =
                    new SignupCustomerResponse()
                            .id(createdCustomerEntity.getUuid())
                            .status("CUSTOMER SUCCESSFULLY REGISTERED");
            return new ResponseEntity<SignupCustomerResponse>(customerResponse, HttpStatus.CREATED);
        }
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST,
            path = "/customer/login",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {
        // Basic authentication format validation
        CustomerAuthTokenEntity customerAuthToken = utilityService.getAuthorizationToken(authorization);
        CustomerEntity customer = customerAuthToken.getCustomer();

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(customer.getUuid());
        loginResponse.setFirstName(customer.getFirstname());
        loginResponse.setLastName(customer.getLastname());
        loginResponse.setEmailAddress(customer.getEmail());
        loginResponse.setContactNumber(customer.getContactnumber());
        loginResponse.setMessage("LOGGED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", customerAuthToken.getAccessToken());

        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST,
            path = "/customer/logout",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {
        CustomerEntity customerEntity = logoutBusinessService.logout(authorization);

        LogoutResponse logoutResponse = new LogoutResponse()
                .id(customerEntity.getUuid()).message("LOGGED OUT SUCCESSFULLY");

        return new ResponseEntity<LogoutResponse>(logoutResponse, null, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT,
            path = "/customer",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> update(@RequestHeader("authorization") final String authorization,
                                                         @RequestBody(required = false) UpdateCustomerRequest updateCustomerRequest)
            throws AuthorizationFailedException, UpdateCustomerException {
        if (updateCustomerRequest.getFirstName() == null) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }

        //Access the accessToken from the request Header
        String accessToken = authorization.split("Bearer ")[1];

        CustomerAuthTokenEntity customerAuthTokenEntity = utilityService.getValidCustomerAuthToken(accessToken);

        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        updateCustomerResponse.setFirstName(customerAuthTokenEntity.getCustomer().getFirstname());
        updateCustomerResponse.setLastName(customerAuthTokenEntity.getCustomer().getLastname());
        updateCustomerResponse.setId(customerAuthTokenEntity.getCustomer().getUuid());
        updateCustomerResponse.status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");


        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, null, HttpStatus.OK);
    }
}
