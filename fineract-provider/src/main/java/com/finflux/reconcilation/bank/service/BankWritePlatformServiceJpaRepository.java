/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bank.service;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bank.data.BankDataValidator;
import com.finflux.reconcilation.bank.domain.Bank;
import com.finflux.reconcilation.bank.domain.BankRepositoryWrapper;
import com.finflux.reconcilation.bank.exception.BankAssociatedToBankStatementCanNotBeDeletedOrUpdated;
import com.finflux.reconcilation.bankstatement.data.BankStatementData;
import com.finflux.reconcilation.bankstatement.service.BankStatementReadPlatformService;

@Service
public class BankWritePlatformServiceJpaRepository implements BankWritePlatformService {

    private final PlatformSecurityContext context;
    private final BankRepositoryWrapper bankRepository;
    private final BankDataValidator bankDataValidator;
    private final GLAccountRepositoryWrapper glAccountRepository;
    private final BankStatementReadPlatformService bankStatementReadPlatformService;

    @Autowired
    public BankWritePlatformServiceJpaRepository(final PlatformSecurityContext context, final BankRepositoryWrapper bankRepository,
            final BankDataValidator bankDataValidator, final GLAccountRepositoryWrapper glAccountRepository,
            final BankStatementReadPlatformService bankStatementReadPlatformService) {
        this.context = context;
        this.bankRepository = bankRepository;
        this.bankDataValidator = bankDataValidator;
        this.glAccountRepository = glAccountRepository;
        this.bankStatementReadPlatformService = bankStatementReadPlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult createBank(JsonCommand command) {

        this.bankDataValidator.validate(command);
        final String name = command.stringValueOfParameterNamed("name");
        Long glAccountId = command.longValueOfParameterNamed("glAccount");
        Boolean supportSimplifiedStatement = false;
        if(command.parameterExists(ReconciliationApiConstants.supportSimplifiedStatement)){
        	supportSimplifiedStatement = command.booleanObjectValueOfParameterNamed(ReconciliationApiConstants.supportSimplifiedStatement);
        }
        GLAccount glAccount = this.glAccountRepository.findOneWithNotFoundDetection(glAccountId);
        Bank bank = Bank.instance(name, glAccount, supportSimplifiedStatement);
        this.bankRepository.save(bank);
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(bank.getId()) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult updateBank(Long bankId, JsonCommand command) {
        this.context.authenticatedUser();
        this.bankDataValidator.validate(command);
        Bank bank = this.bankRepository.findOneWithNotFoundDetection(bankId);
        final Map<String, Object> changes = bank.getBankActualChanges(command);
        if (changes.containsKey(ReconciliationApiConstants.nameParamName)) {
            String name = command.stringValueOfParameterNamed(ReconciliationApiConstants.nameParamName);
            bank.setName(name);
        }
        if (changes.containsKey(ReconciliationApiConstants.glAccountParamName)) {
            Long glAccountId = command.longValueOfParameterNamed(ReconciliationApiConstants.glAccountParamName);
            GLAccount glAccount = this.glAccountRepository.findOneWithNotFoundDetection(glAccountId);
            bank.setGlAccount(glAccount);
        }
        if (changes.containsKey(ReconciliationApiConstants.supportSimplifiedStatement)) {
            boolean supportSimplifiedStatement = command.booleanPrimitiveValueOfParameterNamed(ReconciliationApiConstants.supportSimplifiedStatement);
            bank.setSupportSimplifiedStatement(supportSimplifiedStatement);
        }
        if (!changes.isEmpty()) {
            this.bankRepository.save(bank);
        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(bankId) //
                .with(changes).build();
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteBank(Long bankId) {
        final Bank bank = this.bankRepository.findOneWithNotFoundDetection(bankId);
        List<BankStatementData> associatedBankStatements = this.bankStatementReadPlatformService
                .retrieveBankStatementsByAssociatedBank(bankId);
        if (associatedBankStatements.size() > 0) { throw new BankAssociatedToBankStatementCanNotBeDeletedOrUpdated(); }
        if (bank != null) {
            this.bankRepository.delete(bank);
        }

        return new CommandProcessingResultBuilder() //
                .withEntityId(bankId) //
                .build();
    }

}
