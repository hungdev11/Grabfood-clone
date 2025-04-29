package com.api.service;

import com.api.dto.request.AddressRequest;
import com.api.dto.response.AddressResponse;
import com.api.entity.Address;

import java.util.List;

public interface AddressService {
    long addNewAddress(AddressRequest addressRequest);
    Address getAddressById(long id);
    AddressResponse createAddress(Long userId, AddressRequest addressRequest);
    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest addressRequest);
    void deleteAddress(Long userId, Long addressId);
//    AddressResponse getAddress(Long userId, Long addressId);
    List<AddressResponse> getAllAddressesByUser(Long userId);
    AddressResponse setDefaultAddress(Long userId, Long addressId);
}
