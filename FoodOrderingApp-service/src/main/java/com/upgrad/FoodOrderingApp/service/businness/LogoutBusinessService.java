package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthTokenDao;
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
    private CustomerAuthTokenDao customerAuthTokenDao;

    @Autowired
    private UtilityService utilityService;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity logout(final String authorization) throws AuthorizationFailedException {
        CustomerAuthTokenEntity customerAuthTokenEntity = utilityService.getValidCustomerAuthToken(authorization);
        customerAuthTokenEntity.setLogoutAt(ZonedDateTime.now());
        customerAuthTokenDao.updateCustomerAuthToken(customerAuthTokenEntity);
        return customerAuthTokenEntity.getCustomer();
    }
}
