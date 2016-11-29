package com.finflux.portfolio.bank.service;

import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

import com.finflux.portfolio.bank.domain.BankAccountDetailAssociations;
import com.finflux.portfolio.bank.domain.BankAccountDetailEntityType;

public interface BankAccountDetailsWriteService {

    CommandProcessingResult create(JsonCommand command);

    CommandProcessingResult update(JsonCommand command);

    CommandProcessingResult delete(Long bankAccountDetailId);

    CommandProcessingResult delete(JsonCommand jsonCommand);

    Map<String, Object> updateBankAccountDetail(BankAccountDetailEntityType entityType, Long entityId, String json);

    BankAccountDetailAssociations createBankAccountDetailAssociation(BankAccountDetailEntityType entityType, Long entityId, String json);

    Long deleteBankDetailAssociation(BankAccountDetailEntityType entityType, Long entityId);

}
