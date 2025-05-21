package com.api.service.Imp;

import com.api.dto.request.AddressRequest;
import com.api.dto.response.AddressResponse;
import com.api.entity.User;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.entity.Address;
import com.api.repository.AddressRepository;
import com.api.repository.UserRepository;
import com.api.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImp implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public long addNewAddress(AddressRequest addressRequest) {
        log.info("Adding new address");
        return addressRepository.save(Address.builder()
                        .ward(addressRequest.getWard())
                        .district(addressRequest.getDistrict())
                        .province(addressRequest.getProvince())
                        .lat(addressRequest.getLatitude())
                        .lon(addressRequest.getLongitude())
                        .detail("")
                        .user(null)
                .build()).getId();
    }

    @Override
    public Address getAddressById(long id) {
        log.info("Get address by id: {}", id);
        return addressRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Address id {} doesn't existed: ", id);
                    return new AppException(ErrorCode.RESOURCE_NOT_FOUND);
                });
    }

    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest addressRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "User not found"));

        Address address = Address.builder()
                .province(addressRequest.getProvince())
                .district(addressRequest.getDistrict())
                .ward(addressRequest.getWard())
                .detail(addressRequest.getDetail())
                .lat(addressRequest.getLatitude())
                .lon(addressRequest.getLongitude())
                .isDefault(addressRequest.isDefault())
                .user(user)
                .build();

        // If this is the first address or set as default, make sure it's the only default
        if (addressRequest.isDefault() || addressRepository.findByUserId(userId).isEmpty()) {
            updateDefaultAddresses(userId);
            address.setDefault(true);
        }

        Address savedAddress = addressRepository.save(address);
        return mapToResponse(savedAddress);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest addressRequest) {
        Address address = getAddressAndVerifyOwnership(userId, addressId);

        address.setProvince(addressRequest.getProvince());
        address.setDistrict(addressRequest.getDistrict());
        address.setWard(addressRequest.getWard());
        address.setDetail(addressRequest.getDetail());
        address.setLat(addressRequest.getLatitude());
        address.setLon(addressRequest.getLongitude());



        if (addressRequest.isDefault() && !address.isDefault()) {
            updateDefaultAddresses(userId);
            address.setDefault(true);
        }

        Address updatedAddress = addressRepository.save(address);
        return mapToResponse(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = getAddressAndVerifyOwnership(userId, addressId);

        // If deleting default address, set another as default if available
        if (address.isDefault()) {
            addressRepository.delete(address);
            List<Address> remainingAddresses = addressRepository.findByUserId(userId);
            if (!remainingAddresses.isEmpty()) {
                Address newDefault = remainingAddresses.get(0);
                newDefault.setDefault(true);
                addressRepository.save(newDefault);
            }
        } else {
            addressRepository.delete(address);
        }
    }

//    @Override
//    public AddressResponse getAddress(Long userId, Long addressId) {
//        Address address = getAddressAndVerifyOwnership(userId, addressId);
//        return mapToResponse(address);
//    }

    @Override
    public List<AddressResponse> getAllAddressesByUser(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {
        Address address = getAddressAndVerifyOwnership(userId, addressId);
        updateDefaultAddresses(userId);
        address.setDefault(true);
        Address updatedAddress = addressRepository.save(address);
        return mapToResponse(updatedAddress);
    }

    private void updateDefaultAddresses(Long userId) {
        Address currentDefault = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        if (currentDefault != null) {
            currentDefault.setDefault(false);
            addressRepository.save(currentDefault);
        }
    }

    private Address getAddressAndVerifyOwnership(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You don't have permission to access this address");
        }

        return address;
    }

    private AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .detail(address.getDetail())
                .displayName(address.getWard()+ ", " + address.getDistrict() + ", " + address.getProvince())
                .isDefault(address.isDefault())
                .lat(address.getLat())
                .lon(address.getLon())
                .build();
    }
}
