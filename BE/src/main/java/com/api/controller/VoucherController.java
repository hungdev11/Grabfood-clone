package com.api.controller;

import com.api.dto.request.AddVoucherDetailRequestRes;
import com.api.dto.request.VoucherRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.VoucherResponse;
import com.api.entity.Voucher;
import com.api.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/vouchers")
public class VoucherController {
    private final VoucherService voucherService;

    @PostMapping
    public ApiResponse<VoucherResponse> addNewVoucher(@Validated @RequestBody VoucherRequest request) {
        return ApiResponse.<VoucherResponse>builder()
                .code(200)
                .message("Success")
                .data(voucherService.addVoucher(request))
                .build();
    }

    @PostMapping("/restaurant")
    public ApiResponse<Long> addNewVoucherRestaurant(@Validated @RequestBody VoucherRequest request) {
        return ApiResponse.<Long>builder()
                .code(200)
                .message("Success")
                .data(voucherService.addVoucherRestaurant(request))
                .build();
    }

    @DeleteMapping("/{voucher_id}")
    public ApiResponse deleteVoucher(@PathVariable Long voucher_id) {
        voucherService.deleteVoucher(voucher_id);
        return ApiResponse.builder()
                .code(200)
                .message("OK")
                .build();
    }

    @PutMapping("/{voucher_id}")
    public ApiResponse<Void> updateVoucher(@PathVariable Long voucher_id) {
        voucherService.updateVoucherStatus(voucher_id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Success")
                .build();
    }

    @GetMapping
    public ApiResponse<List<VoucherResponse>> getAllVoucher() {
        return ApiResponse.<List<VoucherResponse>>builder()
                .code(200)
                .message("Success")
                .data(voucherService.getAllVoucher())
                .build();
    }

    @GetMapping("checkApply")
    public ApiResponse<List<VoucherResponse>> getAllVoucherCanApply(@RequestParam BigDecimal totalPrice) {
        return ApiResponse.<List<VoucherResponse>>builder()
                .code(200)
                .message("Success")
                .data(voucherService.getVoucherCanApply(totalPrice))
                .build();
    }

    @GetMapping("/restaurant/{restaurant_id}")
    public ApiResponse<List<VoucherResponse>> getVoucherOfRestaurant(@PathVariable Long restaurant_id)
    {
        return ApiResponse.<List<VoucherResponse>>builder()
                .code(200)
                .message("OK")
                .data(voucherService.getRestaurantVoucher(restaurant_id))
                .build();
    }

    @PostMapping("/extend-voucher")
    public ApiResponse<Boolean> extendVoucher(@RequestBody AddVoucherDetailRequestRes request)
    {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message("OK")
                .data(voucherService.extendVoucher(request))
                .build();
    }

    @DeleteMapping("/{voucherId}/restaurant/{restaurantId}")
    public ApiResponse deleteVoucherRestaurant(@PathVariable Long voucherId, @PathVariable Long restaurantId) {
        return ApiResponse.builder()
                .code(200)
                .message("OK")
                .data(voucherService.deleteVoucherRestaurant(restaurantId, voucherId))
                .build();
    }

    @GetMapping("/admin")
    public ApiResponse<List<VoucherResponse>> getAdminVoucher() {
        return ApiResponse.<List<VoucherResponse>>builder()
                .code(200)
                .message("OK")
                .data(voucherService.getAdminVoucher())
                .build();
    }
}
