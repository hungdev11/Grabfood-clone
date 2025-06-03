package com.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO để rút tiền từ ví của driver
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequest {
    
    @NotNull(message = "Số tiền rút không được để trống")
    @Min(value = 50000, message = "Số tiền rút tối thiểu là 50,000 VND")
    private Long amount;                   // Số tiền muốn rút (VND)
    
    @NotNull(message = "Tên ngân hàng không được để trống")
    @Size(min = 2, max = 100, message = "Tên ngân hàng phải từ 2-100 ký tự")
    private String bankName;               // Tên ngân hàng
    
    @NotNull(message = "Số tài khoản không được để trống")
    @Size(min = 8, max = 20, message = "Số tài khoản phải từ 8-20 ký tự")
    private String bankAccountNumber;      // Số tài khoản
    
    @NotNull(message = "Tên chủ tài khoản không được để trống")
    @Size(min = 2, max = 100, message = "Tên chủ tài khoản phải từ 2-100 ký tự")
    private String bankAccountHolder;      // Tên chủ tài khoản
    
    private String note;                   // Ghi chú cho giao dịch
    private String withdrawType;           // Loại rút tiền (INSTANT, NORMAL)
    private Boolean saveAsDefault;         // Lưu thông tin ngân hàng làm mặc định
} 