package com.api.service;

import com.api.dto.request.AddressRequest;
import com.api.dto.response.AddressResponse;
import com.api.entity.Address;
import com.api.entity.User;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.AddressRepository;
import com.api.repository.UserRepository;
import com.api.service.Imp.AddressServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressServiceImp addressService;

    private User testUser;
    private Address testAddress;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPhone("0123456789");

        testAddress = Address.builder()
                .province("Test Province")
                .district("Test District")
                .ward("Test Ward")
                .detail("Test Detail")
                .lat(10.0)
                .lon(20.0)
                .isDefault(true)
                .user(testUser)
                .build();
        testAddress.setId(1L);

        addressRequest = new AddressRequest();
        addressRequest.setProvince("Test Province");
        addressRequest.setDistrict("Test District");
        addressRequest.setWard("Test Ward");
        addressRequest.setDetail("Test Detail");
        addressRequest.setLatitude(10.0);
        addressRequest.setLongitude(20.0);
        addressRequest.setDefault(true);
    }

    @Test
    void addNewAddress_Success() {
        // Arrange
        Address savedAddress = Address.builder()
                .ward("Test Ward")
                .district("Test District")
                .province("Test Province")
                .lat(10.0)
                .lon(20.0)
                .detail("")
                .user(null)
                .build();
        savedAddress.setId(1L);

        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setId(1L);
            return address;
        });

        // Act
        long addressId = addressService.addNewAddress(addressRequest);

        // Assert
        assertEquals(1L, addressId);
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void getAddressById_ExistingId_ReturnsAddress() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));

        // Act
        Address result = addressService.getAddressById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Province", result.getProvince());
        verify(addressRepository).findById(1L);
    }

    @Test
    void getAddressById_NonExistingId_ThrowsException() {
        // Arrange
        when(addressRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            addressService.getAddressById(999L);
        });

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
        verify(addressRepository).findById(999L);
    }

    @Test
    void createAddress_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        // The implementation checks if it's the first address using findByUserIdAndIsDefaultTrue
        when(addressRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(null);

        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setId(1L);
            return address;
        });

        // Act
        AddressResponse response = addressService.createAddress(1L, addressRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Ward, Test District, Test Province", response.getDisplayName());
        assertTrue(response.isDefault());

        verify(userRepository).findById(1L);
        // Use this verification instead
        verify(addressRepository).findByUserIdAndIsDefaultTrue(1L);
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void createAddress_WithExistingDefaultAddress_UpdatesDefault() {
        // Arrange
        Address existingDefault = Address.builder()
                .isDefault(true)
                .user(testUser)
                .build();
        existingDefault.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        // Remove the unnecessary stubbing here - the implementation doesn't call findByUserId
        when(addressRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(existingDefault);

        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            if (address.getId() == null) {
                address.setId(1L);
            }
            return address;
        });

        // Act
        AddressResponse response = addressService.createAddress(1L, addressRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertTrue(response.isDefault());

        // Verify existing default was updated
        verify(addressRepository).findByUserIdAndIsDefaultTrue(1L);

        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository, times(2)).save(addressCaptor.capture());

        List<Address> capturedAddresses = addressCaptor.getAllValues();
        assertEquals(2, capturedAddresses.size());

        // First save should be setting the old default to false
        assertFalse(capturedAddresses.get(0).isDefault());
        assertEquals(2L, capturedAddresses.get(0).getId());

        // Second save should be the new address set as default
        assertTrue(capturedAddresses.get(1).isDefault());
    }

    @Test
    void updateAddress_Success() {
        // Arrange
        AddressRequest updateRequest = new AddressRequest();
        updateRequest.setProvince("Updated Province");
        updateRequest.setDistrict("Updated District");
        updateRequest.setWard("Updated Ward");
        updateRequest.setDetail("Updated Detail");
        updateRequest.setLatitude(15.0);
        updateRequest.setLongitude(25.0);
        updateRequest.setDefault(true);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // Act
        AddressResponse response = addressService.updateAddress(1L, 1L, updateRequest);

        // Assert
        assertNotNull(response);

        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).save(addressCaptor.capture());

        Address updatedAddress = addressCaptor.getValue();
        assertEquals("Updated Province", updatedAddress.getProvince());
        assertEquals("Updated District", updatedAddress.getDistrict());
        assertEquals("Updated Ward", updatedAddress.getWard());
        assertEquals("Updated Detail", updatedAddress.getDetail());
        assertEquals(15.0, updatedAddress.getLat());
        assertEquals(25.0, updatedAddress.getLon());
    }

    @Test
    void updateAddress_AddressNotFound_ThrowsException() {
        // Arrange
        when(addressRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            addressService.updateAddress(1L, 999L, addressRequest);
        });

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void updateAddress_UserMismatch_ThrowsException() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(2L);

        Address addressWithDifferentUser = Address.builder()
                .user(differentUser)
                .build();
        addressWithDifferentUser.setId(1L);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(addressWithDifferentUser));

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            addressService.updateAddress(1L, 1L, addressRequest);
        });

        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void deleteAddress_Success() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));

        // Act
        addressService.deleteAddress(1L, 1L);

        // Assert
        verify(addressRepository).delete(testAddress);
    }

    @Test
    void deleteAddress_DefaultWithOtherAddresses_SetsNewDefault() {
        // Arrange
        Address secondAddress = Address.builder()
                .isDefault(false)
                .user(testUser)
                .build();
        secondAddress.setId(2L);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(addressRepository.findByUserId(1L)).thenReturn(Collections.singletonList(secondAddress));

        // Act
        addressService.deleteAddress(1L, 1L);

        // Assert
        verify(addressRepository).delete(testAddress);

        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).save(addressCaptor.capture());

        Address newDefault = addressCaptor.getValue();
        assertEquals(2L, newDefault.getId());
        assertTrue(newDefault.isDefault());
    }

    @Test
    void getAllAddressesByUser_Success() {
        // Arrange
        Address secondAddress = Address.builder()
                .province("Second Province")
                .district("Second District")
                .ward("Second Ward")
                .isDefault(false)
                .user(testUser)
                .build();
        secondAddress.setId(2L);

        List<Address> addresses = Arrays.asList(testAddress, secondAddress);
        when(addressRepository.findByUserId(1L)).thenReturn(addresses);

        // Act
        List<AddressResponse> responses = addressService.getAllAddressesByUser(1L);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());
        assertEquals("Test Ward, Test District, Test Province", responses.get(0).getDisplayName());
        assertEquals("Second Ward, Second District, Second Province", responses.get(1).getDisplayName());
    }

    @Test
    void setDefaultAddress_Success() {
        // Arrange
        Address existingDefault = Address.builder()
                .isDefault(true)
                .user(testUser)
                .build();
        existingDefault.setId(2L);

        Address nonDefaultAddress = Address.builder()
                .isDefault(false)
                .user(testUser)
                .build();
        nonDefaultAddress.setId(1L);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(nonDefaultAddress));
        when(addressRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(existingDefault);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AddressResponse response = addressService.setDefaultAddress(1L, 1L);

        // Assert
        assertNotNull(response);
        assertTrue(response.isDefault());

        // Verify both addresses were saved (old default updated and new default set)
        verify(addressRepository, times(2)).save(any(Address.class));
    }
}