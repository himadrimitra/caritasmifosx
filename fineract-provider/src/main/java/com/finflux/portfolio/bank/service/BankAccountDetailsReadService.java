package com.finflux.portfolio.bank.service;

import java.util.Collection;

import com.finflux.task.data.TaskExecutionData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.portfolio.bank.domain.BankAccountDetailEntityType;


public interface BankAccountDetailsReadService {

    BankAccountDetailData retrieveOne(Long id);

    BankAccountDetailData retrieveOneBy(BankAccountDetailEntityType entityType, Long entityId);

    Collection<EnumOptionData> bankAccountTypeOptions();

    TaskExecutionData createOrFetchBankAccountWorkflow(BankAccountDetailEntityType bankEntityType, Long entityId);
}
