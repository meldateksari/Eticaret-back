package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.model.Address;
import com.meldateksari.eticaret.model.User;
import com.meldateksari.eticaret.repository.AddressRepository;
import com.meldateksari.eticaret.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public Address createAddress(Long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        address.setUser(user);
        return addressRepository.save(address);
    }

    public List<Address> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    public Address getDefaultAddressForUser(Long userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId);
    }

    public Address updateAddress(Long id, Address updatedAddress) {
        Address existing = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        existing.setAddressLine1(updatedAddress.getAddressLine1());
        existing.setAddressLine2(updatedAddress.getAddressLine2());
        existing.setCity(updatedAddress.getCity());
        existing.setState(updatedAddress.getState());
        existing.setZipCode(updatedAddress.getZipCode());
        existing.setCountry(updatedAddress.getCountry());
        existing.setIsDefault(updatedAddress.getIsDefault());
        return addressRepository.save(existing);
    }

    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
}
