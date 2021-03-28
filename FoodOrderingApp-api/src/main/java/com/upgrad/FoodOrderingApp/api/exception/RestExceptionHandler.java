package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {
    /**
     *
     * @param exe
     * @param request
     * @return signUpRestrictedException
     */
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode())
                        .message(exe.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     *
     * @param exc
     * @param request
     * @return AuthenticationFailedException
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> AuthenticationFailedException(AuthenticationFailedException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    /**
     *
     * @param exc
     * @param request
     * @return AuthorizationFailedException
     */
    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> AuthorizationFailedException(AuthorizationFailedException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.FORBIDDEN);
    }

    /**
     *
     * @param exc
     * @param request
     * @return CategoryNotFoundException
     */
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> CategoryNotFoundException(CategoryNotFoundException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param exc
     * @param request
     * @return UpdateCustomerException
     */
    @ExceptionHandler(UpdateCustomerException.class)
    public ResponseEntity<ErrorResponse> UpdateCustomerException(UpdateCustomerException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     *
     * @param exc
     * @param request
     * @return RestaurantNotFoundException
     */
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> RestaurantNotFoundException(RestaurantNotFoundException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param exc
     * @param webRequest
     * @return CouponNotFoundException
     */
    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ErrorResponse> CouponNotFoundException(CouponNotFoundException exc, WebRequest webRequest) {
        final ErrorResponse errorResponse = new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param exc
     * @param request
     * @return InvalidRatingException
     */
    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<ErrorResponse> InvalidRatingException(InvalidRatingException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     *
     * @param exc
     * @param request
     * @return SaveAddressException
     */
    @ExceptionHandler(SaveAddressException.class)
    public ResponseEntity<ErrorResponse> SaveAddressException(SaveAddressException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     *
     * @param exc
     * @param request
     * @return AddressNotFoundException
     */
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> AddressNotFoundException(AddressNotFoundException exc, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param exc
     * @param request
     * @return PaymentMethodNotFoundException
     */
    @ExceptionHandler(PaymentMethodNotFoundException.class)
    public ResponseEntity<ErrorResponse> PaymentMethodNotFoundException(PaymentMethodNotFoundException exc, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param exc
     * @param request
     * @return ItemNotFoundException
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> ItemNotFoundException(ItemNotFoundException exc, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }
}
