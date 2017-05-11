package com.finflux.vouchers.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.vouchers.exception.InvalidVoucherTypeException;

@Service
public class VouchersWritePlatformServiceJpaRepositoryImpl implements VouchersWritePlatformService {

    private final VoucherServiceFactory voucherServiceFactory;

    @Autowired
    public VouchersWritePlatformServiceJpaRepositoryImpl(final VoucherServiceFactory voucherServiceFactory) {
        this.voucherServiceFactory = voucherServiceFactory;
    }

    @Override
    public CommandProcessingResult createVoucher(final String voucherType, final JsonCommand command) {
        final VoucherService service = this.voucherServiceFactory.findVoucherService(voucherType);
        if (service == null) { throw new InvalidVoucherTypeException(voucherType); }
        return service.createVoucher(command.json());
    }

    @Override
    public CommandProcessingResult updateVoucher(final String voucherType, Long voucherId, JsonCommand command) {
        final VoucherService service = this.voucherServiceFactory.findVoucherService(voucherType);
        if (service == null) { throw new InvalidVoucherTypeException(voucherType); }
        return service.updateVoucher(voucherId, command.json()) ;
    }
}
