package com.api.controller;

import com.api.dto.request.VoucherRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.VoucherResponse;
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

    @DeleteMapping("/{voucher_id}")
    public ApiResponse deleteVoucher(@PathVariable Long voucher_id) {
        voucherService.deleteVoucher(voucher_id);
        return ApiResponse.builder().build();
    }

    @PutMapping("/{voucher_id}")
    public ApiResponse<VoucherResponse> updateVoucher(@PathVariable Long voucher_id, @RequestBody VoucherRequest request) {
        return ApiResponse.<VoucherResponse>builder()
                .code(200)
                .message("Success")
                .data(voucherService.updateVoucher(voucher_id,request))
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
}
