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

    /**
     * @param stateUuid
     * @return
     * @throws AddressNotFoundException
     * @throws SaveAddressException
     */
    public StateEntity getStateByUUID(final String stateUuid) throws AddressNotFoundException, SaveAddressException {
        if (stateUuid == null || stateUuid.isEmpty()) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }
        final StateEntity stateEntity = stateDao.getStateByUuid(stateUuid);
        if (null == stateEntity) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return stateEntity;
    }

    /**
     * @param customerEntity
     * @param addressEntity
     * @return
     * @throws SaveAddressException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(final CustomerEntity customerEntity, final AddressEntity addressEntity)
            throws SaveAddressException {
        String flatBuilNo = addressEntity.getFlatBuilNo();
        String locality = addressEntity.getLocality();
        String city = addressEntity.getCity();
        String pinCode = addressEntity.getPincode();
        if (flatBuilNo == null || flatBuilNo.isEmpty() ||
                locality == null || locality.isEmpty() ||
                city == null || city.isEmpty() ||
                pinCode == null || pinCode.isEmpty()) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        } else if (!isValidPinCode(pinCode)) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }
        addressEntity.setCustomer(customerEntity);
        return addressDao.saveAddress(addressEntity);
    }

    /**
     * @param addressEntity
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(final AddressEntity addressEntity) {
        return addressDao.deleteAddress(addressEntity);
    }

    /**
     * @param addressId
     * @param customer
     * @return
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
    public AddressEntity getAddressByUUID(final String addressId, final CustomerEntity customer) throws AuthorizationFailedException, AddressNotFoundException {
        if (addressId == null || addressId.isEmpty()) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }

        if (null == addressDao.getAddressByUuid(addressId)) {
            throw new AuthorizationFailedException("ATHR-003", "No address by this id");
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

    /**
     * @param customerEntity
     * @return
     */
    public List<AddressEntity> getAllAddress(final CustomerEntity customerEntity) {
        return customerEntity.getAddresses();
    }

    /**
     * @returnNo field should be empty
     */
    public List<StateEntity> getAllStates() {
        return stateDao.getAllStates();
    }

    /**
     * This method checks the strength of pinCode and approve accordingly
     *
     * @param pinCode
     * @return
     */
    private boolean isValidPinCode(final String pinCode) {
        String regexForDigitsOnly = "[0-9]+";
        Pattern pattern = Pattern.compile(regexForDigitsOnly);
        Matcher matcher = pattern.matcher(pinCode);
        Boolean isPinCodeLengthSix = pinCode.length() == 6;
        Boolean isDigitOnly = matcher.matches();
        return (isDigitOnly && isPinCodeLengthSix);
    }
}
