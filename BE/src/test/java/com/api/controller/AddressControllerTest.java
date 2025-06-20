package com.api.controller;

import com.api.dto.request.AddressRequest;
import com.api.dto.response.AddressResponse;
import com.api.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AddressRequest addressRequest;
    private AddressResponse addressResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(addressController).build();
        objectMapper = new ObjectMapper();

        // Initialize test data matching the actual DTO fields
        addressRequest = AddressRequest.builder()
                .province("Test Province")
                .district("Test District")
                .ward("Test Ward")
                .detail("123 Test Street")
                .isDefault(true)
                .latitude(10.123456)
                .longitude(106.789012)
                .build();

        addressResponse = AddressResponse.builder()
                .id(1L)
                .detail("123 Test Street")
                .displayName("123 Test Street, Test Ward, Test District, Test Province")
                .isDefault(true)
                .lat(10.123456)
                .lon(106.789012)
                .build();
    }

    @Test
    @DisplayName("Should create address and return 201 Created")
    void createAddress_ShouldReturnCreatedAddress() throws Exception {
        // Given
        Long userId = 1L;
        when(addressService.createAddress(eq(userId), any(AddressRequest.class))).thenReturn(addressResponse);

        // When & Then
        mockMvc.perform(post("/users/{userId}/addresses", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(addressResponse.getId()))
                .andExpect(jsonPath("$.detail").value(addressResponse.getDetail()))
                .andExpect(jsonPath("$.displayName").value(addressResponse.getDisplayName()))
                .andExpect(jsonPath("$.default").value(addressResponse.isDefault()))
                .andExpect(jsonPath("$.lat").value(addressResponse.getLat()))
                .andExpect(jsonPath("$.lon").value(addressResponse.getLon()));

        verify(addressService).createAddress(eq(userId), any(AddressRequest.class));
    }

    @Test
    @DisplayName("Should update address and return 200 OK")
    void updateAddress_ShouldReturnUpdatedAddress() throws Exception {
        // Given
        Long userId = 1L;
        Long addressId = 1L;
        when(addressService.updateAddress(eq(userId), eq(addressId), any(AddressRequest.class))).thenReturn(addressResponse);

        // When & Then
        mockMvc.perform(put("/users/{userId}/addresses/{addressId}", userId, addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressResponse.getId()))
                .andExpect(jsonPath("$.detail").value(addressResponse.getDetail()))
                .andExpect(jsonPath("$.displayName").value(addressResponse.getDisplayName()))
                .andExpect(jsonPath("$.default").value(addressResponse.isDefault()))
                .andExpect(jsonPath("$.lat").value(addressResponse.getLat()))
                .andExpect(jsonPath("$.lon").value(addressResponse.getLon()));

        verify(addressService).updateAddress(eq(userId), eq(addressId), any(AddressRequest.class));
    }

    @Test
    @DisplayName("Should delete address and return 204 No Content")
    void deleteAddress_ShouldReturnNoContent() throws Exception {
        // Given
        Long userId = 1L;
        Long addressId = 1L;
        doNothing().when(addressService).deleteAddress(userId, addressId);

        // When & Then
        mockMvc.perform(delete("/users/{userId}/addresses/{addressId}", userId, addressId))
                .andExpect(status().isNoContent());

        verify(addressService).deleteAddress(userId, addressId);
    }

    @Test
    @DisplayName("Should get all addresses and return 200 OK")
    void getAllAddresses_ShouldReturnAddressList() throws Exception {
        // Given
        Long userId = 1L;
        AddressResponse addressResponse2 = AddressResponse.builder()
                .id(2L)
                .detail("456 Test Street")
                .displayName("456 Test Street, Test Ward, Test District, Test Province")
                .isDefault(false)
                .lat(10.654321)
                .lon(106.210987)
                .build();

        List<AddressResponse> addressResponses = Arrays.asList(addressResponse, addressResponse2);
        when(addressService.getAllAddressesByUser(userId)).thenReturn(addressResponses);

        // When & Then
        mockMvc.perform(get("/users/{userId}/addresses", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(addressResponse.getId()))
                .andExpect(jsonPath("$[0].detail").value(addressResponse.getDetail()))
                .andExpect(jsonPath("$[0].displayName").value(addressResponse.getDisplayName()))
                .andExpect(jsonPath("$[0].default").value(addressResponse.isDefault()))
                .andExpect(jsonPath("$[1].id").value(addressResponse2.getId()))
                .andExpect(jsonPath("$[1].detail").value(addressResponse2.getDetail()))
                .andExpect(jsonPath("$[1].default").value(addressResponse2.isDefault()));

        verify(addressService).getAllAddressesByUser(userId);
    }

    @Test
    @DisplayName("Should set default address and return 200 OK")
    void setDefaultAddress_ShouldReturnUpdatedAddress() throws Exception {
        // Given
        Long userId = 1L;
        Long addressId = 1L;
        when(addressService.setDefaultAddress(userId, addressId)).thenReturn(addressResponse);

        // When & Then
        mockMvc.perform(patch("/users/{userId}/addresses/{addressId}/default", userId, addressId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressResponse.getId()))
                .andExpect(jsonPath("$.detail").value(addressResponse.getDetail()))
                .andExpect(jsonPath("$.default").value(addressResponse.isDefault()));

        verify(addressService).setDefaultAddress(userId, addressId);
    }
}