package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthTokenDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                System.out.println("Utility If");
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

    public CustomerAuthTokenEntity bearerAuthenticate(final String accessToken) {
        System.out.println("Utility");
        CustomerAuthTokenEntity customerAuthTokenEntity = customerAuthTokenDao
                .getCustomerAuthTokenByAccessToken(accessToken);
        System.out.println(customerAuthTokenEntity == null);
        System.out.println("Utility- done");

        return customerAuthTokenEntity;
    }
}
