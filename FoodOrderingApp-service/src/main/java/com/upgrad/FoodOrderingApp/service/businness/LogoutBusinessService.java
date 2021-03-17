package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthTokenDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class LogoutBusinessService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerAuthTokenDao customerAuthTokenDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthTokenEntity logout(final String authorization) throws AuthorizationFailedException {
        CustomerAuthTokenEntity  customerAuthTokenEntity = customerAuthTokenDao.getCustomerAuthTokenByAccessToken(authorization);

        if(customerAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in");
        } else if (customerAuthTokenEntity.getLogoutAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint");
        }
        else if(customerAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint");
        } else {
            customerAuthTokenEntity.setLogoutAt(ZonedDateTime.now());
            customerAuthTokenDao.updateCustomerAuthToken(customerAuthTokenEntity);
            return customerAuthTokenEntity;
        }
    }

}
