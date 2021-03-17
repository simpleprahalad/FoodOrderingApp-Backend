package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private CustomerDao customerDao;

    /**
     * @param customerEntity
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signup(CustomerEntity customerEntity) {
        String password = customerEntity.getPassword();
        String[] encryptedText = cryptographyProvider.encrypt(password);
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);
        return customerDao.createCustomer(customerEntity);
    }

    /**
     * @param contactNumber
     * @return
     */
    public CustomerEntity getCustomerByContactNumber(final String contactNumber) {
        return customerDao.getCustomerByContactNumber(contactNumber);
    }

}
