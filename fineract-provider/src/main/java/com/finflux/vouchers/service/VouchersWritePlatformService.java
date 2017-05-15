package com.finflux.vouchers.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface VouchersWritePlatformService {

    CommandProcessingResult createVoucher(final String voucherType, final JsonCommand command);
    
    CommandProcessingResult updateVoucher(final String voucherType, Long voucherId, JsonCommand command) ;
    
    CommandProcessingResult reverseVoucher(final String voucherType, Long voucherId, JsonCommand command) ;
}
