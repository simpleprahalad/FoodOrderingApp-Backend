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

@Service
public class CustomerService {

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private CustomerAuthDao customerAuthDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider; //Provides coding and decoding for the password

    public CustomerEntity getCustomer(final String accessToken) {
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

        if (!utilityService.isValidSignupRequest(customerEntity)) {//Checking if is Valid Signup Request.
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }

        if (!utilityService.isEmailValid(customerEntity.getEmail())) {//Checking if email is valid
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        if (!utilityService.isContactNumberValid(customerEntity.getContactnumber())) {//Checking if Contact is valid
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }

        if (!utilityService.isPasswordWeak(customerEntity.getPassword())) {//Checking if Password is valid.
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
    public CustomerEntity updateCustomer(CustomerEntity customerEntity) throws UpdateCustomerException {

        //Getting the CustomerEntity by getCustomerByUuid of customerDao
        CustomerEntity customerToBeUpdated = customerDao.getCustomerByUuid(customerEntity.getUuid());

        //Setting the new details to the customer entity .
        customerToBeUpdated.setFirstName(customerEntity.getFirstName());
        customerToBeUpdated.setLastName(customerEntity.getLastName());

        //Calls updateCustomer of customerDao to update the customer data in the DB
        CustomerEntity updatedCustomer = customerDao.updateCustomer(customerEntity);

        return updatedCustomer;
    }

    public CustomerEntity updateCustomerPassword(
            final String oldPassword,
            final String newPassword,
            CustomerEntity customerEntity) throws UpdateCustomerException {

        if (!utilityService.isPasswordWeak(newPassword)) {//Checking if the Password is Weak.
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
