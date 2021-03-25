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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AddressService {

    @Autowired
    private StateDao stateDao;

    @Autowired
    private AddressDao addressDao;

    public StateEntity getStateByUUID(final String stateUuid) {
        return stateDao.getStateByUuid(stateUuid);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String saveAddress(CustomerEntity customer, String flatBuildingName, String locality,
                              String city, String pinCode, String stateUuid)
            throws SaveAddressException, AddressNotFoundException {
        StateEntity state = getStateByUUID(stateUuid);
        if (flatBuildingName == null || locality == null || city == null || stateUuid == null) {
            throw new SaveAddressException("SAR-001", "No field can be empty.");
        } else if (!isValidPincode(pinCode)) {
            throw new SaveAddressException("SAR-002", "Invalid pincode.");
        } else if (state == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id.");
        }

        AddressEntity address = new AddressEntity();
        address.setUuid(UUID.randomUUID().toString());
        address.addCustomer(customer);
        address.setFlatBuildingName(flatBuildingName);
        address.setLocality(locality);
        address.setCity(city);
        address.setPincode(pinCode);
        address.setState(state);
        address.setActive(1);

        String uuid = addressDao.saveAddress(address);
        return uuid;
    }

    public List<StateEntity> getStateList() {
        return stateDao.getAllStates();
    }

    @Transactional
    public String deleteAddress(final CustomerEntity customer, final String addressId) throws AuthorizationFailedException, AddressNotFoundException {
        if (addressId.isEmpty()) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }
        AddressEntity searchedEntity = validateInputAddressBelongsToCustomer(customer, addressId);
        AddressEntity deletedAddress = addressDao.deleteAddress(searchedEntity);
        return deletedAddress.getUuid();
    }

    private AddressEntity validateInputAddressBelongsToCustomer(final CustomerEntity customer, final String addressId) throws AuthorizationFailedException {
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
}
