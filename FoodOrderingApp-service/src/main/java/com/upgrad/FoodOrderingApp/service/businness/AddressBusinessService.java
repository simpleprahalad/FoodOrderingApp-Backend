package com.upgrad.FoodOrderingApp.service.businness;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.jvm.hotspot.debugger.Address;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AddressBusinessService {

    @Autowired
    StateDao stateDao;

    @Autowired
    AddressDao addressDao;

    public String saveAddress(CustomerEntity customer, String flatBuildingName, String locality,
                              String city, String pincode, String stateUuid)
            throws SaveAddressException, AddressNotFoundException {
        StateEntity state = stateDao.getStateByUuid(stateUuid);

        if(flatBuildingName == null || locality == null || city == null || stateUuid == null) {
            throw new SaveAddressException("SAR-001", "No field can be empty.");
        } else if (!isValidPincode(pincode)) {
            throw new SaveAddressException("SAR-002", "Invalid pincode.");
        } else if (state == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }

        AddressEntity address = new AddressEntity();
        address.setUuid(UUID.randomUUID().toString());
        address.addCustomer(customer);
        address.setFlatBuildingName(flatBuildingName);
        address.setLocality(locality);
        address.setCity(city);
        address.setPincode(pincode);
        address.setState(state);

        String uuid = addressDao.saveAddress(address);
        return uuid;
    }

    private boolean isValidPincode(String pincode) {
        String regexForDigitsOnly = "[0-9]+";
        Pattern pattern = Pattern.compile(regexForDigitsOnly);
        Matcher matcher = pattern.matcher(pincode);
        Boolean isPincodeLengthSix = pincode.length() == 6;
        Boolean isDigitOnly = matcher.matches();

        return (isDigitOnly && isPincodeLengthSix);
    }
}
