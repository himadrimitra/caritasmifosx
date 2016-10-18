/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface GroupLoanIndividualMonitoringTransactionWritePlatformService {

    CommandProcessingResult repayGLIM(Long loanId, JsonCommand command, boolean isRecoveryRepayment);

    CommandProcessingResult waiveInterest(Long loanId, JsonCommand command);

    CommandProcessingResult waiveCharge(Long loanId, JsonCommand command);

    CommandProcessingResult writeOff(Long loanId, JsonCommand command);
}
