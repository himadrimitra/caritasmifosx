package com.finflux.reconcilation.bankstatement.domain;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.bankstatement.exception.BankStatementNotFoundException;

@Service
public class BankStatementRepositoryWrapper {

    @SuppressWarnings("unused")
    private final PlatformSecurityContext context;
    private final BankStatementRepository bankStatementRepository;

    @Autowired
    private BankStatementRepositoryWrapper(final PlatformSecurityContext context, final BankStatementRepository bankStatementRepository) {
        this.context = context;
        this.bankStatementRepository = bankStatementRepository;
    }

    public void save(final BankStatement bankStatement) {
        this.bankStatementRepository.save(bankStatement);
    }

    public void delete(final BankStatement bankStatement) {
        this.bankStatementRepository.delete(bankStatement);
    }

    public BankStatement findOneWithNotFoundDetection(final Long id){
        final BankStatement bankStatement = this.bankStatementRepository.findOne(id);
        if (bankStatement == null) { throw new BankStatementNotFoundException(id); }
        return bankStatement;
    }
}
