/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringData;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringRepaymentScheduleData;
import org.joda.time.LocalDate;

public interface GroupLoanIndividualMonitoringReadPlatformService {

    Collection<GroupLoanIndividualMonitoringData> retrieveAll();

    GroupLoanIndividualMonitoringData retrieveOne(final Long id);

    Collection<GroupLoanIndividualMonitoringData> retrieveAllByLoanId(final Long loanId);

    GroupLoanIndividualMonitoringData retrieveByLoanAndClientId(final Long loanId, final Long clientId);

    Collection<GroupLoanIndividualMonitoringData> retrieveSelectedClientsByLoanId(Long loanId);

    Collection<GroupLoanIndividualMonitoringData> retrieveWaiveInterestTemplate(Long loanId);

    Collection<GroupLoanIndividualMonitoringData> retrieveWaiveChargeDetails(final Long loanId);

    Collection<GroupLoanIndividualMonitoringData> retrieveAllActiveGlimByLoanId(final Long loanId);

    Collection<GroupLoanIndividualMonitoringData> retrieveRecoveryGlimByLoanId(final Long loanId);

    GroupLoanIndividualMonitoringRepaymentScheduleData retriveGlimRepaymentScheduleById(final Long glimId, BigDecimal disbursedAmount,
            LocalDate disbursedDate);

}
