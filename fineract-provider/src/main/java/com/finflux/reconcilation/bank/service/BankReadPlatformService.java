/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bank.service;

import java.util.List;

import com.finflux.reconcilation.bank.data.BankData;

public interface BankReadPlatformService {

    public List<BankData> retrieveAllBanks();

    public BankData getBank(final Long bankId);

    public List<BankData> getBankByGLAccountId(final Long gLAccountId);

}
