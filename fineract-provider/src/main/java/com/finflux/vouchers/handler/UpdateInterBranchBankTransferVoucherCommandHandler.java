package com.finflux.vouchers.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.vouchers.constants.VoucherType;
import com.finflux.vouchers.service.VouchersWritePlatformService;

@Service
@CommandType(entity = "INTERBRANCHBANKTRANSFER_VOUCHER", action = "UPDATE")
public class UpdateInterBranchBankTransferVoucherCommandHandler implements NewCommandSourceHandler {

    private final VouchersWritePlatformService voucherWritePlatformService;

    @Autowired
    public UpdateInterBranchBankTransferVoucherCommandHandler(final VouchersWritePlatformService voucherWritePlatformService) {
        this.voucherWritePlatformService = voucherWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.voucherWritePlatformService.updateVoucher(VoucherType.INTER_BRANCH_BANK_TRANSFER.getCode(), command.entityId(), command);
    }
}
