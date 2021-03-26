package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired
    private CustomerAuthDao customerAuthDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider; //Provides coding and decoding for the password

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

    private boolean isEmailValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    private boolean isContactNumberValid(String contactNumber) {
        Pattern p = Pattern.compile("\\A[0-9]{10}\\z");
        Matcher m = p.matcher(contactNumber);
        return (m.find() && m.group().equals(contactNumber));
    }

    @javax.transaction.Transactional
    public CustomerAuthEntity validateAccessToken(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthTokenByAccessToken(accessToken);

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

    public CustomerEntity getCustomer(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthTokenByAccessToken(accessToken);
        return customerAuthEntity.getCustomer();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {

        //calls getCustomerByContactNumber method of customerDao to check if customer already exists.
        CustomerEntity existingCustomerEntity = customerDao.getCustomerByContactNumber(customerEntity.getContactnumber());

        if (existingCustomerEntity != null) {//Checking if Customer already Exists if yes throws exception.
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number");
        }

        if (!isEmailValid(customerEntity.getEmail())) {//Checking if email is valid
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        if (!isContactNumberValid(customerEntity.getContactnumber())) {//Checking if Contact is valid
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }

        if (!isPasswordWeak(customerEntity.getPassword())) {//Checking if Password is valid.
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }

        //If all condition are satisfied the password is encoded using passwordCryptographyProvider and encoded password adn salt is added to the customerentity and persisited.
        String[] encryptedPassword = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedPassword[0]);
        customerEntity.setPassword(encryptedPassword[1]);

        //Calls createCustomer of customerDao to create the customer.
        CustomerEntity createdCustomerEntity = customerDao.createCustomer(customerEntity);

        return createdCustomerEntity;
    }

    /**
     * @param contactNumber
     * @param password
     * @return
     * @throws AuthenticationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String contactNumber, final String password) throws AuthenticationFailedException {
        CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(contactNumber);
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthToken = new CustomerAuthEntity();
            customerAuthToken.setCustomer(customerEntity);
            customerAuthToken.setCustomer(customerEntity);
            customerAuthToken.setUuid(UUID.randomUUID().toString());
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            customerAuthToken.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
            customerAuthToken.setLoginAt(now);
            customerAuthToken.setExpiresAt(expiresAt);
            customerDao.createAuthToken(customerAuthToken);

            return customerAuthToken;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthTokenByAccessToken(accessToken);

        //Paremters are checked as below if the conditions are not satisfied it throws exception.
        if (customerAuthEntity == null) {//Checking if customerAuthEntity exist
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null) {//Checking customerAuthEntity is logout or not
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if (customerAuthEntity.getExpiresAt().compareTo(now) < 0) {//Checking accessToken Expiry
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        //Setting the logout time to now.
        customerAuthEntity.setLogoutAt(ZonedDateTime.now());

        //Calls customerLogout of customerAuthDao to update the CustomerAuthEntity and logsout the customer.
        CustomerAuthEntity upatedCustomerAuthEntity = customerAuthDao.customerLogout(customerAuthEntity);
        return upatedCustomerAuthEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(CustomerEntity customerEntity) {
        //Calls updateCustomer of customerDao to update the customer data in the DB
        CustomerEntity updatedCustomer = customerDao.updateCustomer(customerEntity);
        return updatedCustomer;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(
            final String oldPassword,
            final String newPassword,
            CustomerEntity customerEntity) throws UpdateCustomerException {

        if (!isPasswordWeak(newPassword)) {//Checking if the Password is Weak.
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }

        //Encrypting the oldpassword enter by user.
        String encryptedOldPassword = passwordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());

        //Checking the oldPassword is correct as stored in the DB
        if (encryptedOldPassword.equals(customerEntity.getPassword())) {
            CustomerEntity tobeUpdatedCustomerEntity = customerDao.getCustomerByUuid(customerEntity.getUuid());

            //Encrypting newPassword to store in the DB
            String[] encryptedPassword = passwordCryptographyProvider.encrypt(newPassword);
            tobeUpdatedCustomerEntity.setSalt(encryptedPassword[0]);
            tobeUpdatedCustomerEntity.setPassword(encryptedPassword[1]);

            //Updating the Customer with the new password and salt.
            CustomerEntity updatedCustomerEntity = customerDao.updateCustomer(tobeUpdatedCustomerEntity);

            return updatedCustomerEntity;
        } else {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }
    }
}
