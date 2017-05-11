package com.finflux.vouchers.service;

import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;

import com.finflux.vouchers.data.VoucherData;

public interface VoucherReadPlatformService {

    public VoucherData retrieveVoucheTemplate(final String voucherType);

    public VoucherData retrieveOne(final String voucherType, final Long voucherId);

    public Page<VoucherData> retrieveVouchers(final SearchParameters searchParams);
}