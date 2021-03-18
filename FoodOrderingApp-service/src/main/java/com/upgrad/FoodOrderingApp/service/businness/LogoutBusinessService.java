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

    @Autowired
    private UtilityService utilityService;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity logout(final String authorization) throws AuthorizationFailedException {

        CustomerAuthTokenEntity  customerAuthTokenEntity = utilityService.bearerAuthenticate(authorization);
        System.out.println(customerAuthTokenEntity == null);
        System.out.println("Service - after utility");

        if(customerAuthTokenEntity == null) {
            System.out.println("Service - if 1");
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in");
        } else if (customerAuthTokenEntity.getLogoutAt().isBefore(ZonedDateTime.now())) {
            System.out.println("Service - if 2");
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint");
        }
        else if(customerAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint");
        } else {
            System.out.println("Service - Else");
            customerAuthTokenEntity.setLogoutAt(ZonedDateTime.now());
            customerAuthTokenDao.updateCustomerAuthToken(customerAuthTokenEntity);
            return customerAuthTokenEntity.getCustomer();
        }
    }

}
