package com.finflux.vouchers.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoucherRepository extends JpaRepository<Voucher, Long>, JpaSpecificationExecutor<Voucher> {

    @Query("select count(voucher) from Voucher voucher where voucher.voucherType=:voucherType and voucher.financialYear=:financialYear")
    Integer retrieveVouchersCount(@Param("voucherType") Integer voucherType, @Param("financialYear") String financialYear);
    
    Voucher findVoucherByRelatedVoucherId(@Param("relatedVoucherId") Long relatedVoucherId);
}
