package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UtilityService {
    @Autowired
    private CustomerAuthDao customerAuthDao;

    //To validate the Authorization format
    public boolean isValidAuthorizationFormat(String authorization) throws AuthenticationFailedException {
        try {
            byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedAuth = new String(decoded);
            String[] decodedArray = decodedAuth.split(":");
            String username = decodedArray[0];
            String password = decodedArray[1];
            return true;
        } catch (ArrayIndexOutOfBoundsException exc) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
    }

    //To validate Customer update request
    public boolean isValidUpdateCustomerRequest(String firstName) throws UpdateCustomerException {
        if (firstName == null || firstName == "") {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        return true;
    }

    //To validate the password Update Request.
    public boolean isValidUpdatePasswordRequest(String oldPassword, String newPassword) throws UpdateCustomerException {
        if (oldPassword == null || oldPassword == "") {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        if (newPassword == null || newPassword == "") {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        return true;
    }

    public boolean isValidSignupRequest(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if (customerEntity.getFirstName() == null || customerEntity.getFirstName() == "") {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        if (customerEntity.getPassword() == null || customerEntity.getPassword() == "") {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        if (customerEntity.getEmail() == null || customerEntity.getEmail() == "") {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        if (customerEntity.getContactnumber() == null || customerEntity.getContactnumber() == "") {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        return true;
    }

    public boolean isPasswordWeak(String password) {
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

    public boolean isEmailValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public boolean isContactNumberValid(String contactNumber) {
        Pattern p = Pattern.compile("\\A[0-9]{10}\\z");
        Matcher m = p.matcher(contactNumber);
        return (m.find() && m.group().equals(contactNumber));
    }

    public CustomerAuthEntity validateAccessToken(final String accessToken) throws AuthorizationFailedException {
        final CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthTokenByAccessToken(accessToken);
        //Checking if Customer not logged In
        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        //Checking if customer is logged Out
        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002",
                    "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        //Checking accessToken is Expired.
        if (customerAuthEntity.getExpiresAt().compareTo(now) <= 0) {
            throw new AuthorizationFailedException("ATHR-003",
                    "Your session is expired. Log in again to access this endpoint.");
        }
        return customerAuthEntity;
    }

}
