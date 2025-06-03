// package com.example.demo.controller;

// import com.api.controller.VoucherController;
// import com.api.dto.request.VoucherRequest;
// import com.api.dto.response.ApiResponse;
// import com.api.dto.response.VoucherResponse;
// import com.api.service.VoucherService;
// import com.api.utils.VoucherStatus;
// import com.api.utils.VoucherType;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;

// import java.math.BigDecimal;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.mockito.Mockito.*;

// public class VoucherControllerTest {
//     @Mock
//     private VoucherService voucherService;

//     @InjectMocks
//     private VoucherController voucherController;

//     @BeforeEach
//     void setup() {
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     void addNewVoucher_WhenRequestValid_ReturnsSuccessResponse() {
//         // Arrange
//         VoucherRequest request = VoucherRequest.builder()
//                 .code("GRAB50")
//                 .description("50% off for GrabFood")
//                 .quantity(100)
//                 .minRequire(new BigDecimal("100.00"))
//                 .type(VoucherType.PERCENTAGE)
//                 .value(new BigDecimal("50.00"))
//                 .status(VoucherStatus.ACTIVE)
//                 .restaurant_id(1)
//                 .build();
//         VoucherResponse response = VoucherResponse.builder()
//                 .id(20) // Giả sử ID được thêm sau khi tạo
//                 .code("GRAB50")
//                 .description("50% off for GrabFood")
//                 .minRequire(new BigDecimal("100.00"))
//                 .type(VoucherType.PERCENTAGE)
//                 .value(new BigDecimal("50.00"))
//                 .status(VoucherStatus.ACTIVE)
//                 .restaurant_name("Grab Restaurant") // Giả sử tên nhà hàng
//                 .build();

//         when(voucherService.addVoucher(request)).thenReturn(response);

//         // Act
//         ApiResponse<VoucherResponse> result = voucherController.addNewVoucher(request);

//         // Assert
//         assertNotNull(result);
//         assertEquals(200, result.getCode());
//         assertEquals("Success", result.getMessage());
//         assertEquals(response, result.getData());
//         verify(voucherService, times(1)).addVoucher(request);
//     }
// }
