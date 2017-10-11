package com.finflux.vouchers.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.vouchers.domain.Voucher;
import com.finflux.vouchers.domain.VoucherRepositoryWrapper;
import com.finflux.vouchers.exception.InvalidVoucherTypeException;
import com.finflux.vouchers.exception.VoucherAlreadyReversedException;

@Service
public class VouchersWritePlatformServiceJpaRepositoryImpl implements VouchersWritePlatformService {

    private final VoucherServiceFactory voucherServiceFactory;
    private final VoucherRepositoryWrapper voucherRepostiroyWrapper;
    
    @Autowired
    public VouchersWritePlatformServiceJpaRepositoryImpl(final VoucherServiceFactory voucherServiceFactory,
            final VoucherRepositoryWrapper voucherRepostiroyWrapper) {
        this.voucherServiceFactory = voucherServiceFactory;
        this.voucherRepostiroyWrapper = voucherRepostiroyWrapper ;
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

    @Override
    @Transactional
    public CommandProcessingResult reverseVoucher(final String voucherType, final Long voucherId, JsonCommand command) {
        final Voucher voucher = this.voucherRepostiroyWrapper.findVoucher(voucherId) ;
        if(voucher.isReversed()) {
            throw new VoucherAlreadyReversedException(voucherId) ;
        }
        final VoucherService service = this.voucherServiceFactory.findVoucherService(voucherType);
        if (service == null) { throw new InvalidVoucherTypeException(voucherType); }
        return service.reverseVoucher(voucher, command) ;
    }
}
