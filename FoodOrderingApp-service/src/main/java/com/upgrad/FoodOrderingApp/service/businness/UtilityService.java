package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthTokenDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Base64;

@Service
public class UtilityService {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CustomerAuthTokenDao customerAuthTokenDao;

    //To validate the Authorization format
    public CustomerAuthTokenEntity getAuthorizationToken(final String authorization) {

        try {
            if (authorization != null && authorization.startsWith("Basic ")) {
                byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
                String decodeText = new String(decode);
                String[] decodeArray = decodeText.split(":");

                CustomerAuthTokenEntity customerAuthToken = authenticationService.authenticate(decodeArray[0], decodeArray[1]);
                return customerAuthToken;
            } else {
                return null;
            }
        } catch (AuthenticationFailedException afe) {
            return null;
        }
    }

    public CustomerAuthTokenEntity getCustomerAuthToken(final String accessToken) {
        CustomerAuthTokenEntity customerAuthTokenEntity = customerAuthTokenDao
                .getCustomerAuthTokenByAccessToken(accessToken);
        return customerAuthTokenEntity;
    }

    public CustomerAuthTokenEntity getValidCustomerAuthToken(final String authorization) throws AuthorizationFailedException {
        CustomerAuthTokenEntity customerAuthTokenEntity = getCustomerAuthToken(authorization);

        if (customerAuthTokenEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        else if (customerAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        } else if (customerAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        return customerAuthTokenEntity;
    }
}
