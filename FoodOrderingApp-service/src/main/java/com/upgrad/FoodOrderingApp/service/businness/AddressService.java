package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AddressService {

    @Autowired
    private StateDao stateDao;

    @Autowired
    private AddressDao addressDao;

    public StateEntity getStateByUUID(final String stateUuid) throws AddressNotFoundException {
        final StateEntity stateEntity = stateDao.getStateByUuid(stateUuid);
        if (null == stateEntity) {
            throw new AddressNotFoundException("ANF-002", "No state by this id.");
        }
        return stateEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(final CustomerEntity customerEntity, final AddressEntity addressEntity) throws SaveAddressException {
        if (addressEntity.getFlatBuilNo() == null || addressEntity.getLocality() == null || addressEntity.getCity() == null) {
            throw new SaveAddressException("SAR-001", "No field can be empty.");
        } else if (!isValidPincode(addressEntity.getPincode())) {
            throw new SaveAddressException("SAR-002", "Invalid pincode.");
        }
        addressEntity.addCustomer(customerEntity);
        return addressDao.saveAddress(addressEntity);
    }

    public AddressEntity deleteAddress(final AddressEntity addressEntity) {
        return addressDao.deleteAddress(addressEntity);
    }

    public AddressEntity getAddressByUUID(final String addressId, final CustomerEntity customer) throws AuthorizationFailedException, AddressNotFoundException {
        if (addressId.isEmpty()) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }

        final AddressEntity searchedAddress = addressDao.getAddressesByCustomerUuid(customer.getUuid())
                .stream()
                .filter(addressEntity -> addressEntity.getUuid().equalsIgnoreCase(addressId))
                .findFirst()
                .orElse(null);
        if (null == searchedAddress) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }
        return searchedAddress;
    }

    private boolean isValidPincode(String pincode) {
        String regexForDigitsOnly = "[0-9]+";
        Pattern pattern = Pattern.compile(regexForDigitsOnly);
        Matcher matcher = pattern.matcher(pincode);
        Boolean isPincodeLengthSix = pincode.length() == 6;
        Boolean isDigitOnly = matcher.matches();

        return (isDigitOnly && isPincodeLengthSix);
    }

    public List<AddressEntity> getAllAddress(final CustomerEntity customerEntity) {
        return customerEntity.getAddresses();
    }

    public List<StateEntity> getAllStates() {
        return stateDao.getAllStates();
    }
}
