package com.finflux.portfolio.bank.service;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.portfolio.bank.domain.BankAccountDetailEntityType;


public interface BankAccountDetailsReadService {

    BankAccountDetailData retrieveOne(Long id);

    BankAccountDetailData retrieveOneBy(BankAccountDetailEntityType entityType, Long entityId);

}
