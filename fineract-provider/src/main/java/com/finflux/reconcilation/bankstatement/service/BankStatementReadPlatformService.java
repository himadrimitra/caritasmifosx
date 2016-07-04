package com.finflux.reconcilation.bankstatement.service;

import java.io.File;
import java.util.List;

import com.finflux.reconcilation.bankstatement.data.BankStatementData;
import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;

public interface BankStatementReadPlatformService {

    public File retrieveFile(Long documentId);

    public List<BankStatementData> retrieveAllBankStatements();

    public BankStatementData getBankStatement(final Long bankStatementId);

    public List<BankStatementDetailsData> changedBankStatementDetailsData(final Long bankStatementId);

    public List<BankStatementDetailsData> retrieveBankStatementDetailsData(final Long bankStatementId, String command);

    public List<BankStatementData> retrieveBankStatementsByAssociatedBank(final Long bankId);
}
