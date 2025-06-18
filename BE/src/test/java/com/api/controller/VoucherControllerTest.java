package com.api.controller;

import com.api.dto.request.AddVoucherDetailRequestRes;
import com.api.dto.request.VoucherRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.VoucherResponse;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.exception.GlobalExceptionHandler;
import com.api.service.VoucherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
class VoucherControllerTest {

    @Mock
    private VoucherService voucherService;

    @InjectMocks
    private VoucherController voucherController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(voucherController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void addNewVoucher_Success() throws Exception {
        // Given
        VoucherRequest request = new VoucherRequest();
        VoucherResponse expectedResponse = new VoucherResponse();
        request.setDescription("description");

        when(voucherService.addVoucher(any(VoucherRequest.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/vouchers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(voucherService).addVoucher(argThat(r -> r.getDescription().equals(request.getDescription())));
    }

    @Test
    void addNewVoucherRestaurant_Success() throws Exception {
        // Given
        VoucherRequest request = new VoucherRequest();
        request.setDescription("description");
        Long expectedId = 1L;

        when(voucherService.addVoucherRestaurant(any(VoucherRequest.class))).thenReturn(expectedId);

        // When & Then
        mockMvc.perform(post("/vouchers/restaurant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").value(expectedId));

        verify(voucherService).addVoucherRestaurant(argThat(r -> r.getDescription().equals(request.getDescription())));
    }

    @Test
    void deleteVoucher_Success() throws Exception {
        // Given
        Long voucherId = 1L;

        doNothing().when(voucherService).deleteVoucher(voucherId);

        // When & Then
        mockMvc.perform(delete("/vouchers/{voucher_id}", voucherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"));

        verify(voucherService).deleteVoucher(voucherId);
    }

    @Test
    void updateVoucher_Success() throws Exception {
        // Given
        Long voucherId = 1L;

        doNothing().when(voucherService).updateVoucherStatus(voucherId);

        // When & Then
        mockMvc.perform(put("/vouchers/{voucher_id}", voucherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(voucherService).updateVoucherStatus(voucherId);
    }

    @Test
    void getAllVoucher_Success() throws Exception {
        // Given
        List<VoucherResponse> vouchers = Arrays.asList(new VoucherResponse(), new VoucherResponse());

        when(voucherService.getAllVoucher()).thenReturn(vouchers);

        // When & Then
        mockMvc.perform(get("/vouchers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(voucherService).getAllVoucher();
    }

    @Test
    void getAllVoucherCanApply_Success() throws Exception {
        // Given
        BigDecimal totalPrice = new BigDecimal("100.00");
        List<VoucherResponse> applicableVouchers = Arrays.asList(new VoucherResponse());

        when(voucherService.getVoucherCanApply(totalPrice)).thenReturn(applicableVouchers);

        // When & Then
        mockMvc.perform(get("/vouchers/checkApply")
                        .param("totalPrice", totalPrice.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(voucherService).getVoucherCanApply(totalPrice);
    }

    @Test
    void getVoucherOfRestaurant_Success() throws Exception {
        // Given
        Long restaurantId = 1L;
        List<VoucherResponse> restaurantVouchers = Arrays.asList(new VoucherResponse());

        when(voucherService.getRestaurantVoucher(restaurantId)).thenReturn(restaurantVouchers);

        // When & Then
        mockMvc.perform(get("/vouchers/restaurant/{restaurant_id}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"));

        verify(voucherService).getRestaurantVoucher(restaurantId);
    }

    @Test
    void extendVoucher_Success() throws Exception {
        // Given
        AddVoucherDetailRequestRes request = new AddVoucherDetailRequestRes();
        request.setVoucher_id(1L);
        Boolean expectedResult = true;

        when(voucherService.extendVoucher(any(AddVoucherDetailRequestRes.class))).thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(post("/vouchers/extend-voucher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(true));

        verify(voucherService).extendVoucher(argThat(r -> r.getVoucher_id().equals(1L)));
    }

    @Test
    void extendVoucher_ReturnsFalse() throws Exception {
        // Given
        AddVoucherDetailRequestRes request = new AddVoucherDetailRequestRes();
        request.setVoucher_id(1L);
        Boolean expectedResult = false;

        when(voucherService.extendVoucher(any(AddVoucherDetailRequestRes.class))).thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(post("/vouchers/extend-voucher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(false));

        verify(voucherService).extendVoucher(argThat(req->req.getVoucher_id().equals(1L)));
    }

    @Test
    void deleteVoucherRestaurant_Success() throws Exception {
        // Given
        Long voucherId = 1L;
        Long restaurantId = 2L;
        Boolean expectedResult = true;

        when(voucherService.deleteVoucherRestaurant(restaurantId, voucherId)).thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(delete("/vouchers/{voucherId}/restaurant/{restaurantId}", voucherId, restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(true));

        verify(voucherService).deleteVoucherRestaurant(restaurantId, voucherId);
    }

    @Test
    void getAdminVoucher_Success() throws Exception {
        // Given
        List<VoucherResponse> adminVouchers = Arrays.asList(new VoucherResponse());

        when(voucherService.getAdminVoucher()).thenReturn(adminVouchers);

        // When & Then
        mockMvc.perform(get("/vouchers/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"));

        verify(voucherService).getAdminVoucher();
    }

    @Test
    void addNewVoucher_ServiceThrowsException() throws Exception {
        // Given
        VoucherRequest request = new VoucherRequest();
        request.setCode("1a");
        request.setDescription("description");
        when(voucherService.addVoucher(any(VoucherRequest.class))).thenThrow(new AppException(ErrorCode.VOUCHER_CODE_EXISTED));

        // When & Then
        mockMvc.perform(post("/vouchers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(voucherService).addVoucher(argThat(r -> r.getCode().equals("1a")));
    }

    @Test
    void addNewVoucherRestaurant_ServiceThrowsException() throws Exception {
        // Given
        VoucherRequest request = new VoucherRequest();
        request.setCode("a");
        request.setRestaurant_id(1L);
        request.setDescription("description");

        when(voucherService.addVoucherRestaurant(any(VoucherRequest.class))).thenThrow(new AppException(ErrorCode.VOUCHER_ID_EXISTED));

        // When & Then
        mockMvc.perform(post("/vouchers/restaurant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(voucherService).addVoucherRestaurant(argThat(req->req.getRestaurant_id().equals(1L) && req.getCode().equals("a")));
    }

    @Test
    void deleteVoucher_ServiceThrowsException() throws Exception {
        // Given
        Long voucherId = 1L;

        doThrow(new AppException(ErrorCode.VOUCHER_ID_EXISTED)).when(voucherService).deleteVoucher(voucherId);

        // When & Then
        mockMvc.perform(delete("/vouchers/{voucher_id}", voucherId))
                .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.message").value(ErrorCode.VOUCHER_ID_EXISTED.getMessage()));

        verify(voucherService).deleteVoucher(voucherId);
    }

    @Test
    void updateVoucher_ServiceThrowsException() throws Exception {
        // Given
        Long voucherId = 1L;

        doThrow(new AppException(ErrorCode.VOUCHER_NOT_FOUND)).when(voucherService).updateVoucherStatus(voucherId);

        // When & Then
        mockMvc.perform(put("/vouchers/{voucher_id}", voucherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ErrorCode.VOUCHER_NOT_FOUND.getMessage()));

        verify(voucherService).updateVoucherStatus(voucherId);
    }

    @Test
    void getAllVoucherCanApply_InvalidTotalPrice() throws Exception {
        // When & Then
        mockMvc.perform(get("/vouchers/checkApply")
                        .param("totalPrice", "invalid"))
                .andExpect(status().isBadRequest());

        verify(voucherService, never()).getVoucherCanApply(any(BigDecimal.class));
    }
}
