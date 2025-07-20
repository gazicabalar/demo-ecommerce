package com.ecommerce.project.service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);
    List<AddressDTO> getAddresses();
    AddressDTO getAddressesById(Long id);
    List<AddressDTO> getUserAddressesById(User user);
    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);
    String deleteAddress(Long addressId);
}
