package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class UtilityService {
    @Autowired
    private AuthenticationService authenticationService;

    //To validate the Authorization format
    public CustomerAuthTokenEntity getAuthorizationToken(String authorization) throws AuthenticationFailedException {

        if (authorization != null && authorization.startsWith("Basic ")) {
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodeText = new String(decode);
            String[] decodeArray = decodeText.split(":");

            CustomerAuthTokenEntity customerAuthToken = authenticationService.authenticate(decodeArray[0], decodeArray[1]);
            return customerAuthToken;
        } else {
            throw new AuthenticationFailedException("ATH-003",
                    "Incorrect format of decoded customer name and password");
        }
    }
}
