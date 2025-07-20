package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(ModelMapper modelMapper, AddressRepository addressRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);


        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        List<AddressDTO> addressDTOS = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();

        return addressDTOS;
    }

    @Override
    public AddressDTO getAddressesById(Long id) {
        Address address = addressRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Address", "id", id)
        );

        AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);

        return addressDTO;
    }

    @Override
    public List<AddressDTO> getUserAddressesById(User user) {
        List<Address> addresses = user.getAddresses();

        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address", "id", addressId));

        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setCountry(addressDTO.getCountry());
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setState(addressDTO.getState());
        addressFromDatabase.setPostCode(addressDTO.getPostCode());
        addressFromDatabase.setBuildingName(addressDTO.getBuildingName());

        Address updatedAddress = addressRepository.save(addressFromDatabase);

        User user = addressFromDatabase.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDatabase = addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address", "id", addressId)
        );

        User user = addressFromDatabase.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(addressFromDatabase);

        return "Address successfully deleted with addressId: " + addressId;
    }
}
