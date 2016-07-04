package com.finflux.reconcilation.bankstatement.service;

import org.apache.fineract.accounting.glaccount.data.GLAccountDataForLookup;

public interface BankGLAccountReadPlatformService {

    GLAccountDataForLookup retrieveGLAccountByGLCode(String glCode);
}
