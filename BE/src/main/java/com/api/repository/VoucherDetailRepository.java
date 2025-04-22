package com.api.repository;

import com.api.entity.Food;
import com.api.entity.Voucher;
import com.api.entity.VoucherDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VoucherDetailRepository extends JpaRepository<VoucherDetail,Long> {
    boolean existsByVoucherId(Long id);
    List<VoucherDetail> findByStartDateAndEndDateAndVoucherId(LocalDateTime startDate, LocalDateTime endDate, Long voucherId);

    VoucherDetail findByVoucherIdAndEndDateAfter(Long voucherId, LocalDateTime currentDateTime);

    List<VoucherDetail> findByVoucherInAndFoodInAndStartDateBeforeAndEndDateAfter(
            List<Voucher> vouchers,
            List<Food> foods,
            LocalDateTime now1,
            LocalDateTime now2
    );


}
