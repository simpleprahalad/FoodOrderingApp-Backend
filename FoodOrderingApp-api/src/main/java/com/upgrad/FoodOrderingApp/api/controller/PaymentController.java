package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Get all payments
     * @return ResponseEntity of type PaymentListResponse
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/payment",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> getAllPaymentMethods() {
        final List<PaymentEntity> paymentDetails =
                paymentService.getAllPaymentMethods();
        final PaymentListResponse paymentResponseBody = getAllPaymentMethodsResponseBody(paymentDetails);
        return new ResponseEntity<>(paymentResponseBody, HttpStatus.OK);
    }

    /**
     * Populate PaymentListResponse object
     * @param paymentEntities
     * @return PaymentListResponse
     */
    private PaymentListResponse getAllPaymentMethodsResponseBody(final List<PaymentEntity> paymentEntities) {
        final PaymentListResponse paymentListResponse = new PaymentListResponse();

        for (PaymentEntity paymentEntity : paymentEntities) {
            final PaymentResponse paymentType = new PaymentResponse();
            paymentType.setId(UUID.fromString(paymentEntity.getUuid()));
            paymentType.setPaymentName(paymentEntity.getPaymentName());
            paymentListResponse.addPaymentMethodsItem(paymentType);
        }

        return paymentListResponse;
    }

}
