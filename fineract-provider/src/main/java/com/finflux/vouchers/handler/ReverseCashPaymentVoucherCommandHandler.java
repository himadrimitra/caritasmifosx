package com.finflux.vouchers.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CASHPAYMENT_VOUCHER", action = "REVERSE")
public class ReverseCashPaymentVoucherCommandHandler implements NewCommandSourceHandler {

    public ReverseCashPaymentVoucherCommandHandler() {}

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return null;
    }
}