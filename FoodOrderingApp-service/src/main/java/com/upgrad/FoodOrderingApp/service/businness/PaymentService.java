package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    public List<PaymentEntity> getPaymentDetails(final String authorization)
            throws AuthorizationFailedException {
        // isUserAuthorized(authorization);
        return paymentDao.getAllPaymentMethods();
    }

    private void isUserAuthorized(final String authorization) throws AuthorizationFailedException {
        //        UserAuthEntity userAuthToken = paymentDao.getUserAuthToken(authorization);
        //        if (userAuthToken == null) {
        //            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        //        }
    }
}
