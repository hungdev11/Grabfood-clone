package com.api.service.Imp;

import com.api.dto.request.UpdateShipperProfileRequest;
import com.api.entity.Shipper;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.ShipperRepository;
import com.api.service.ShipperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShipperServiceImp implements ShipperService {

    private final ShipperRepository shipperRepository;

    @Override
    public Shipper getShipperByAccountId(Long accountId) {
        return shipperRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public Shipper getShipperById(Long shipperId) {
        return shipperRepository.findById(shipperId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public Shipper getShipperByPhone(String phone) {
        return shipperRepository.findByPhone(phone)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public Shipper updateShipperOnlineStatus(Long shipperId, Boolean isOnline) {
        Shipper shipper = getShipperById(shipperId);
        shipper.setIsOnline(isOnline);
        return shipperRepository.save(shipper);
    }

    @Override
    @Transactional
    public Shipper updateShipperLocation(Long shipperId, Double latitude, Double longitude) {
        Shipper shipper = getShipperById(shipperId);
        shipper.setCurrentLatitude(latitude);
        shipper.setCurrentLongitude(longitude);
        // Update both location fields for compatibility
        shipper.setCurrentLat(latitude);
        shipper.setCurrentLon(longitude);
        return shipperRepository.save(shipper);
    }

    @Override
    @Transactional
    public Shipper updateShipperProfile(Long shipperId, UpdateShipperProfileRequest request) {
        Shipper shipper = getShipperById(shipperId);

        // Update fields if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            shipper.setName(request.getName().trim());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            shipper.setEmail(request.getEmail().trim());
        }
        if (request.getVehicleNumber() != null) {
            shipper.setVehicleNumber(request.getVehicleNumber());
        }
        if (request.getLicensePlate() != null) {
            shipper.setLicensePlate(request.getLicensePlate());
        }

        return shipperRepository.save(shipper);
    }

    @Override
    public Shipper getAuthenticatedShipperProfile(String phone) {
        return getShipperByPhone(phone);
    }
}