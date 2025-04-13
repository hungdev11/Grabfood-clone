package com.api.service.Imp;

import com.api.dto.request.AddressRequest;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.entity.Address;
import com.api.repository.AddressRepository;
import com.api.service.AddressService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImp implements AddressService {
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public long addNewAddress(AddressRequest addressRequest) {
        log.info("Adding new address");
        return addressRepository.save(Address.builder()
                        .ward(addressRequest.getWard())
                        .district(addressRequest.getDistrict())
                        .province(addressRequest.getProvince())
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

}
