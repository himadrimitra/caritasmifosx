package com.finflux.vouchers.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.vouchers.constants.VoucherType;
import com.finflux.vouchers.service.VouchersWritePlatformService;

@Service
@CommandType(entity = "BANKPAYMENT_VOUCHER", action = "REVERSE")
public class ReverseBankPaymentVoucherCommandHandler implements NewCommandSourceHandler {

    private final VouchersWritePlatformService voucherWritePlatformService ;
    
    @Autowired
    public ReverseBankPaymentVoucherCommandHandler(final VouchersWritePlatformService voucherWritePlatformService) {
        this.voucherWritePlatformService = voucherWritePlatformService ;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.voucherWritePlatformService.reverseVoucher(VoucherType.BANK_PAYMENT.getCode(), command.entityId(), command) ;
    }
}