package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/payment",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<List<PaymentResponse>> getPaymentResponse(
            /*(@RequestHeader("authorization") final String authorization*/) throws AuthorizationFailedException {
        final List<PaymentEntity> paymentDetails =
                paymentService.getPaymentDetails(/*authorization*/ "");
        List<PaymentResponse> paymentResponseBody = getPaymentDetailResponseBody(paymentDetails);
        return new ResponseEntity<>(paymentResponseBody, HttpStatus.OK);
    }

    private List<PaymentResponse> getPaymentDetailResponseBody(List<PaymentEntity> paymentEntities) {
        final List<PaymentResponse> paymentDetails = new ArrayList<>(paymentEntities.size());
        for (PaymentEntity paymentEntity : paymentEntities) {
            final PaymentResponse paymentType = new PaymentResponse();
            paymentType.setId(paymentEntity.getUuid());
            paymentType.setPaymentName(paymentEntity.getPaymentName());
            paymentDetails.add(paymentType);
        }
        return paymentDetails;
    }
}
