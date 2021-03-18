package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.AuthenticationService;
import com.upgrad.FoodOrderingApp.service.businness.SignupBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    SignupBusinessService signupBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

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

    @RequestMapping(method = RequestMethod.POST,
            path = "/customer/login",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {
        // Basic authentication format validation
        if (authorization != null && authorization.startsWith("Basic ")) {
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodeText = new String(decode);
            String[] decodeArray = decodeText.split(":");

            CustomerAuthTokenEntity customerAuthToken = authenticationService.authenticate(decodeArray[0], decodeArray[1]);
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
        } else {
            throw new AuthenticationFailedException("ATH-003",
                    "Incorrect format of decoded customer name and password");
        }
    }
}