package com.api.mapper.Imp;

import com.api.dto.request.VoucherRequest;
import com.api.dto.response.VoucherResponse;
import com.api.mapper.VoucherMapper;

import com.api.entity.Voucher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.Mapping;

@RequiredArgsConstructor
@Slf4j
public class VoucherMapperImp implements VoucherMapper {
    @Override
    public Voucher toVoucher(VoucherRequest request) {
        Voucher voucher = Voucher.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .minRequire(request.getMinRequire())
                .type(request.getType())
                .applyType(request.getApplyType())
                .status(request.getStatus())
                .value(request.getValue())
                .build();
        return voucher;
    }

    @Override
    public VoucherResponse toVoucherResponse(Voucher voucher) {
        VoucherResponse voucherResponse =
                VoucherResponse.builder()
                        .code(voucher.getCode())
                        .description(voucher.getDescription())
                        .id(voucher.getId())
                        .minRequire(voucher.getMinRequire())
                        .status(voucher.getStatus())
                        .applyType(voucher.getApplyType())
                        .type(voucher.getType())
                        .value(voucher.getValue())
                .build();
        return voucherResponse;
    }


}
