package com.api.controller;

import com.api.dto.request.AddVoucherDetailRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.VoucherDetailResponse;
import com.api.service.VoucherDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/voucherDetails")
public class VoucherDetailController {
    private final VoucherDetailService voucherDetailService;

    @PostMapping
    public ApiResponse<VoucherDetailResponse> addVoucherDetail(@RequestBody AddVoucherDetailRequest request) {
        return ApiResponse.<VoucherDetailResponse>builder()
                .code(200)
                .message("OK")
                .data(voucherDetailService.addVoucherDetails(request))
                .build();
    }
}