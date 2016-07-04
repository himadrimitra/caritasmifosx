package com.finflux.reconcilation.bank.service;

import java.util.List;

import com.finflux.reconcilation.bank.data.BankData;

public interface BankReadPlatformService {

    public List<BankData> retrieveAllBanks();

    public BankData getBank(final Long bankId);

    public List<BankData> getBankByGLAccountId(final Long gLAccountId);

}
