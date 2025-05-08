package com.api.controller;

import com.api.dto.request.AddressRequest;
import com.api.dto.response.AddressResponse;
import com.api.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @PathVariable Long userId,
            @RequestBody AddressRequest addressRequest) {
        AddressResponse response = addressService.createAddress(userId, addressRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @RequestBody AddressRequest addressRequest) {
        AddressResponse response = addressService.updateAddress(userId, addressId, addressRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/{addressId}")
//    public ResponseEntity<AddressResponse> getAddress(
//            @PathVariable Long userId,
//            @PathVariable Long addressId) {
//        AddressResponse response = addressService.getAddress(userId, addressId);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses(
            @PathVariable Long userId) {
        List<AddressResponse> addresses = addressService.getAllAddressesByUser(userId);
        return ResponseEntity.ok(addresses);
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        AddressResponse response = addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok(response);
    }
}
