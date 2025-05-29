package com.api.service;

import com.api.dto.request.UpdateShipperProfileRequest;
import com.api.entity.Shipper;

public interface ShipperService {
    Shipper getShipperByAccountId(Long accountId);

    Shipper getShipperById(Long shipperId);

    Shipper getShipperByPhone(String phone);

    Shipper updateShipperOnlineStatus(Long shipperId, Boolean isOnline);

    Shipper updateShipperLocation(Long shipperId, Double latitude, Double longitude);

    // New methods for Shipper Management APIs
    Shipper updateShipperProfile(Long shipperId, UpdateShipperProfileRequest request);

    Shipper getAuthenticatedShipperProfile(String phone);
}