/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.investment.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.investment.api.InvestmentConstants;
import org.apache.fineract.portfolio.investment.data.InvestmentDataValidator;
import org.apache.fineract.portfolio.investment.domain.Investment;
import org.apache.fineract.portfolio.investment.domain.InvestmentRepositoryWrapper;
import org.apache.fineract.portfolio.investment.exception.InvestmentAlreadyExistsException;
import org.apache.fineract.portfolio.investment.exception.InvestmentCloseDateOnOrAfterInvestmentStartDateException;
import org.apache.fineract.portfolio.investment.exception.MoreThanLoanAmmountException;
import org.apache.fineract.portfolio.investment.exception.NoFundsAvailableException;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformServiceImpl;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;

@Service
public class InvestmentWritePlatformServiceRepositoryImpl implements InvestmentWritePlatformService {

    private final PlatformSecurityContext context;
    private final InvestmentRepositoryWrapper repositoryWrapper;
    private final SavingsAccountRepositoryWrapper savingsaccount;
    private final SavingsAccountRepository savingAccount;
    private final LoanRepositoryWrapper loanRepository;
    private final InvestmentReadPlatformService savingInvestment;
    private final SavingsAccountReadPlatformServiceImpl savingAccountDetails;
    private final LoanReadPlatformService loanReadPlatformService;
    private final InvestmentDataValidator fromApiJsonDeserializer;
    private final InvestmentReadPlatformService investmentReadPlatformService;

    @Autowired
    public InvestmentWritePlatformServiceRepositoryImpl(final PlatformSecurityContext context,
            final InvestmentRepositoryWrapper repositoryWrapper, final SavingsAccountRepositoryWrapper savingsaccount,
            final LoanRepositoryWrapper loanRepository, final SavingsAccountRepository savingAccount,
            final InvestmentReadPlatformService savingInvestment, final SavingsAccountReadPlatformServiceImpl savingAccountDetails,
            final LoanReadPlatformService loanReadPlatformService, final InvestmentDataValidator fromApiJsonDeserializer,
            final InvestmentReadPlatformService investmentReadPlatformService) {

        this.context = context;
        this.repositoryWrapper = repositoryWrapper;
        this.loanRepository = loanRepository;
        this.savingsaccount = savingsaccount;
        this.savingAccount = savingAccount;
        this.savingInvestment = savingInvestment;
        this.savingAccountDetails = savingAccountDetails;
        this.loanReadPlatformService = loanReadPlatformService;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.investmentReadPlatformService = investmentReadPlatformService;

    }

    @Override
    public CommandProcessingResult addSavingsInvestment(final Long savingsId, final JsonCommand command) {
        // TODO Auto-generated method stub

        this.context.authenticatedUser();

        this.fromApiJsonDeserializer.validateForCreateSavingInvestment(command.json());

        final Long savingId = command.longValueOfParameterNamed("savingId");
        final String[] loanIds = command.arrayValueOfParameterNamed("loanId");
        final String[] investedAmounts = command.arrayValueOfParameterNamed("investedAmounts");
        List<Long> savingInvestedAmount = new ArrayList<Long>();
        List<Long> loanInvestedAmount = new ArrayList<Long>();
        final List<Long> investedAmount = new ArrayList<Long>();
        final List<Long> loanId = new ArrayList<Long>();
        List<Long> existingLoanIds = new ArrayList<Long>();
        final List<Long> newLoanIds = new ArrayList<Long>();
        final List<Long> newInvestedAmount = new ArrayList<Long>();
        Long totalAmount = null;

        final String[] startDate = command.arrayValueOfParameterNamed("startDate");

        final JsonArray dateL = command.arrayOfParameterNamed("startDate");
        final List<Date> sDate = new ArrayList<Date>();
        final List<Date> cDate = new ArrayList<Date>();
        final List<Date> newStartDate = new ArrayList<Date>();
        final List<Date> newCloseDate = new ArrayList<Date>();

        final DateFormat formateDate = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

        BigDecimal minRequiredBalance = BigDecimal.ZERO;
        final SavingsAccount account = this.savingAccount.findOne(savingId);
        BigDecimal availableMinBal = BigDecimal.ZERO;

        if (account.getMinRequiredBalance() != null) {
            availableMinBal = account.getMinRequiredBalance();
        }
        ;

        Long totalInvestment = 0L;

        Long id = 0L;
        Long savingSum = 0L;
        Long loanSum = 0L;
        int check = 0;
        boolean isSavingInvestmentIsAlreadyDoneWithSameDate = false;

        Date date = new Date();

        if (loanIds != null) {
            for (final String Id : loanIds) {
                id = Long.parseLong(Id);
                loanId.add(id);
            }
            for (final String investment : investedAmounts) {
                totalAmount = Long.parseLong(investment);
                investedAmount.add(totalAmount);
            }
            for (final String start : startDate) {

                try {
                    date = formateDate.parse(start);

                } catch (final ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                sDate.add(date);

            }

            existingLoanIds = this.savingInvestment.retriveLoanIdBySavingId(savingId);
            savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingId);

            for (final Long investment : savingInvestedAmount) {
                savingSum = savingSum + investment;
            }

            final SavingsAccountData savingBalance = this.savingAccountDetails.retrieveOne(savingId);
            final BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();

            if (!(savingAccountBalance.compareTo(BigDecimal.ZERO) > 0)) { throw new NoFundsAvailableException(); }

            if (existingLoanIds.isEmpty()) {
                for (int i = 0; i < investedAmount.size(); i++) {
                    loanInvestedAmount = this.savingInvestment.retriveInvestedAmountByLoanId(loanId.get(i));
                    for (final Long investment : loanInvestedAmount) {
                        loanSum = loanSum + investment;
                    }

                    final LoanAccountData loanBalance = this.loanReadPlatformService.retrieveOne(loanId.get(i));
                    final BigDecimal loanAccountBalance = loanBalance.getTotalOutstandingAmount();

                    if (investedAmount.get(i) <= (savingAccountBalance.longValue() - savingSum)
                            && (investedAmount.get(i) <= (loanAccountBalance.longValue() - loanSum))) {
                        newLoanIds.add(loanId.get(i));
                        newInvestedAmount.add(investedAmount.get(i));
                        savingSum = savingSum + investedAmount.get(i);
                        minRequiredBalance = new BigDecimal(savingSum);
                        newStartDate.add(sDate.get(i));
                        // newCloseDate.add(cDate.get(i));
                    } else {
                        check++;
                    }
                }

                BigDecimal newMinBal = BigDecimal.ZERO;
                newMinBal = availableMinBal.add(minRequiredBalance);

                account.setMinRequiredBalance(newMinBal);
                this.savingAccount.save(account);

            }

            else {
                for (int i = 0; i < loanId.size(); i++) {
                    int count = 0;
                    for (int j = 0; j < existingLoanIds.size(); j++) {
                        if (loanId.get(i).equals(existingLoanIds.get(j))) {
                            //
                            count++;
                        }
                    }

                    if (count == 0) {
                        loanInvestedAmount = this.savingInvestment.retriveInvestedAmountByLoanId(loanId.get(i));
                        for (final Long investment : loanInvestedAmount) {
                            loanSum = loanSum + investment;
                        }

                        final LoanAccountData loanBalance = this.loanReadPlatformService.retrieveOne(loanId.get(i));
                        final BigDecimal loanAccountBalance = loanBalance.getTotalOutstandingAmount();

                        if ((investedAmount.get(i) <= (savingAccountBalance.longValue() - savingSum))
                                && (investedAmount.get(i) <= (loanAccountBalance.longValue() - loanSum))) {
                            newLoanIds.add(loanId.get(i));
                            newInvestedAmount.add(investedAmount.get(i));
                            savingSum = savingSum + investedAmount.get(i);
                            // minRequiredBalance = new BigDecimal(savingSum);
                            newStartDate.add(sDate.get(i));
                        } else {
                            check++;
                        }
                        totalInvestment = totalInvestment + investedAmount.get(i);
                    }
                    /*
                     * else{ throw new InvestmentAlreadyExistsException(); }
                     */

                    if (count > 0) {

                        final LocalDate investmentStartDate = new LocalDate(sDate.get(i));
                        isSavingInvestmentIsAlreadyDoneWithSameDate = this.investmentReadPlatformService
                                .isSavingInvestmentAlreadyDoneWithSameDate(savingId, investmentStartDate);
                        if (!(isSavingInvestmentIsAlreadyDoneWithSameDate)) {

                            loanInvestedAmount = this.savingInvestment.retriveInvestedAmountByLoanId(loanId.get(i));
                            for (final Long investment : loanInvestedAmount) {
                                loanSum = loanSum + investment;
                            }

                            final LoanAccountData loanBalance = this.loanReadPlatformService.retrieveOne(loanId.get(i));
                            final BigDecimal loanAccountBalance = loanBalance.getTotalOutstandingAmount();

                            if ((investedAmount.get(i) <= (savingAccountBalance.longValue() - savingSum))) {
                                newLoanIds.add(loanId.get(i));
                                newInvestedAmount.add(investedAmount.get(i));
                                savingSum = savingSum + investedAmount.get(i);
                                // minRequiredBalance = new
                                // BigDecimal(savingSum);
                                newStartDate.add(sDate.get(i));
                            } else {
                                check++;
                            }
                            totalInvestment = totalInvestment + investedAmount.get(i);
                        }

                    }

                }

                // BigDecimal newMinBal =
                // availableMinBal.add(minRequiredBalance);
                final BigDecimal availbaleMinBalance = account.getMinRequiredBalance();
                final BigDecimal newBalance = availbaleMinBalance.add(new BigDecimal(totalInvestment));
                account.setMinRequiredBalance(newBalance);
                this.savingAccount.save(account);
            }

            for (int i = 0; i < newLoanIds.size(); i++) {
                if (check == 0 || isSavingInvestmentIsAlreadyDoneWithSameDate == false) {
                    final Investment savingInvestment = new Investment(savingId, newLoanIds.get(i), newInvestedAmount.get(i),
                            newStartDate.get(i), null);
                    this.repositoryWrapper.save(savingInvestment);
                } else {
                    throw new NoFundsAvailableException();
                }

            }

        }
        return new CommandProcessingResultBuilder().build();
    }

    @Override
    public CommandProcessingResult addLoanInvestment(final Long loanId, final JsonCommand command) {
        // TODO Auto-generated method stub

        this.context.authenticatedUser();
        this.fromApiJsonDeserializer.validateForCreateLoanInvestment(command.json());

        final Long loanid = command.longValueOfParameterNamed("loanId");
        final String[] savingIds = command.arrayValueOfParameterNamed("savingId");
        final String[] investedAmounts = command.arrayValueOfParameterNamed("investedAmounts");
        final List<Long> investedAmount = new ArrayList<Long>();
        final List<Long> savingId = new ArrayList<Long>();
        List<Long> savingInvestedAmount = new ArrayList<Long>();
        List<Long> loanInvestedAmount = new ArrayList<Long>();
        List<Long> existingSavingId = new ArrayList<Long>();
        final List<Long> newSavingId = new ArrayList<Long>();
        final List<Long> newInvestedAmount = new ArrayList<Long>();
        Long id = 0L;
        Long totalAmount = 0L;
        Long savingSum = 0L;
        Long loanSum = 0L;
        int check = 0;
        boolean isLoanInvestmentIsAlreadyDoneWithSameDate = false;

        final String[] startDate = command.arrayValueOfParameterNamed("startDate");

        final List<Date> sDate = new ArrayList<Date>();
        final List<Date> cDate = new ArrayList<Date>();
        final List<Date> newStartDate = new ArrayList<Date>();
        final List<Date> newCloseDate = new ArrayList<Date>();
        Date date = new Date();

        BigDecimal minRequiredBalance = BigDecimal.ZERO;

        Long totalInvestment = 0L;

        final DateFormat formateDate = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

        if (savingIds != null) {
            for (final String Id : savingIds) {
                id = Long.parseLong(Id);
                savingId.add(id);
            }
            for (final String investment : investedAmounts) {
                totalAmount = Long.parseLong(investment);
                investedAmount.add(totalAmount);
            }

            for (final String start : startDate) {

                try {
                    date = formateDate.parse(start);

                } catch (final ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                sDate.add(date);

            }

        }

        existingSavingId = this.savingInvestment.retriveSavingIdByLoanId(loanid);
        loanInvestedAmount = this.savingInvestment.retriveInvestedAmountByLoanId(loanid);

        for (final Long investment : loanInvestedAmount) {
            loanSum = loanSum + investment;
        }

        final LoanAccountData loanBalance = this.loanReadPlatformService.retrieveOne(loanid);
        final BigDecimal loanAccountBalance = loanBalance.getTotalOutstandingAmount();

        if (existingSavingId.isEmpty()) {
            for (int i = 0; i < investedAmount.size(); i++) {
                savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingId.get(i));
                for (final Long investment : savingInvestedAmount) {
                    savingSum = savingSum + investment;
                }

                final SavingsAccountData savingBalance = this.savingAccountDetails.retrieveOne(savingId.get(i));
                final BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();
                if (investedAmount.get(i) <= (loanAccountBalance.longValue() - loanSum)
                        && investedAmount.get(i) <= (savingAccountBalance.longValue() - savingSum)) {
                    newSavingId.add(savingId.get(i));
                    newInvestedAmount.add(investedAmount.get(i));
                    loanSum = loanSum + investedAmount.get(i);
                    newStartDate.add(sDate.get(i));
                    // newCloseDate.add(cDate.get(i));
                } else {
                    check++;
                }
            }
        } else {

            for (int i = 0; i < savingId.size(); i++) {
                int count = 0;
                for (int j = 0; j < existingSavingId.size(); j++) {
                    if (savingId.get(i).equals(existingSavingId.get(j))) {
                        //
                        count++;
                    }
                }

                if (count == 0) {
                    savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingId.get(i));
                    for (final Long investment : savingInvestedAmount) {
                        savingSum = savingSum + investment;
                    }

                    final SavingsAccountData savingBalance = this.savingAccountDetails.retrieveOne(savingId.get(i));
                    final BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();
                    if (investedAmount.get(i) <= (loanAccountBalance.longValue() - loanSum)
                            && investedAmount.get(i) <= (savingAccountBalance.longValue() - savingSum)) {
                        newSavingId.add(savingId.get(i));
                        newInvestedAmount.add(investedAmount.get(i));
                        loanSum = loanSum + investedAmount.get(i);
                        newStartDate.add(sDate.get(i));
                        // newCloseDate.add(cDate.get(i));
                    } else {
                        check++;
                    }
                }

                if (count > 0) {

                    final LocalDate investmentStartDate = new LocalDate(sDate.get(i));

                    isLoanInvestmentIsAlreadyDoneWithSameDate = this.investmentReadPlatformService
                            .isLoanInvestmentAlreadyDoneOnSameDate(loanid, investmentStartDate);
                    if (!(isLoanInvestmentIsAlreadyDoneWithSameDate)) {

                        savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingId.get(i));
                        for (final Long investment : savingInvestedAmount) {
                            savingSum = savingSum + investment;
                        }

                        final SavingsAccountData savingBalance = this.savingAccountDetails.retrieveOne(savingId.get(i));
                        final BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();
                        if (investedAmount.get(i) <= (loanAccountBalance.longValue() - loanSum)
                                && investedAmount.get(i) <= (savingAccountBalance.longValue() - savingSum)) {
                            newSavingId.add(savingId.get(i));
                            newInvestedAmount.add(investedAmount.get(i));
                            loanSum = loanSum + investedAmount.get(i);
                            newStartDate.add(sDate.get(i));

                        }

                    }

                }
                /*
                 * else{ throw new InvestmentAlreadyExistsException(); }
                 */

                totalInvestment = investedAmount.get(i) + totalInvestment;
            }
        }

        for (int i = 0; i < newSavingId.size(); i++) {
            final SavingsAccount account = this.savingAccount.findOne(newSavingId.get(i));

            if (!(account.getMinRequiredBalance() == null)) {
                minRequiredBalance = account.getMinRequiredBalance();
            }
            final BigDecimal investementAmount = new BigDecimal(newInvestedAmount.get(i));
            final BigDecimal newMinBal = minRequiredBalance.add(investementAmount);
            account.setMinRequiredBalance(newMinBal);

            if (check == 0 || isLoanInvestmentIsAlreadyDoneWithSameDate == false) {
                final Investment savingInvestment = new Investment(newSavingId.get(i), loanid, newInvestedAmount.get(i),
                        newStartDate.get(i), null);
                this.repositoryWrapper.save(savingInvestment);
                this.savingAccount.save(account);
            } else {
                throw new NoFundsAvailableException();
            }

        }

        return new CommandProcessingResultBuilder().build();

    }

    @Override

    public CommandProcessingResult updateSavingInvestment(final Long savingsAccountId, final JsonCommand command) {
        List<Long> savingInvestedAmount = new ArrayList<Long>();
        List<Long> loanInvestedAmount = new ArrayList<Long>();

        Long id = 0L;
        Long newid = 0L;
        Long loanSum = 0L;
        Long savingSum = 0L;
        final Date date = new Date();

        final Long loanId = command.longValueOfParameterNamed("loanId");
        final Long ammount = command.longValueOfParameterNamed("investedAmounts");
        final Long oldAmount = command.longValueOfParameterNamed("oldAmount");
        final Long oldLoanId = command.longValueOfParameterNamed("oldLoanId");
        final String investmentStartDate = command.stringValueOfParameterNamed("startDate");
        final SavingsAccount account = this.savingAccount.findOne(savingsAccountId);
        final BigDecimal availableMinRequiredBal = account.getMinRequiredBalance();

        id = this.savingInvestment.retriveSavingInvestmentIdForUpdate(savingsAccountId, oldLoanId, investmentStartDate);
        final Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
        final Long availableInvestedAmount = savingInvestment.getInvestedAmount();

        final Investment investment = this.repositoryWrapper.findWithNotFoundDetection(id);
        final Map<String, Object> changes = investment.update(command);
        final int x = changes.size();
        if (changes.containsKey(InvestmentConstants.loanIdParamName)) {

            final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.loanIdParamName);
            newid = this.savingInvestment.retriveSavingInvestmentId(savingsAccountId, newValue, null);
            if (newid == null) {
                investment.updateLoanId(newValue);
            } else {
                throw new InvestmentAlreadyExistsException();
            }
        }
        if (changes.containsKey(InvestmentConstants.SAVINGINVESTMENT_RESOURCE_NAME)) {
            final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.SAVINGINVESTMENT_RESOURCE_NAME);
            final SavingsAccountData savingBalance = this.savingAccountDetails.retrieveOne(savingsAccountId);
            final BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();
            savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingsAccountId);

            for (final Long savingammount : savingInvestedAmount) {
                savingSum = savingSum + savingammount;
            }

            final LoanAccountData loanBalance = this.loanReadPlatformService.retrieveOne(loanId);
            final BigDecimal loanAccountBalance = loanBalance.getTotalOutstandingAmount();
            loanInvestedAmount = this.savingInvestment.retriveInvestedAmountByLoanId(loanId);
            for (final Long loanammount : loanInvestedAmount) {
                loanSum = loanSum + loanammount;
            }
            if ((newValue <= loanAccountBalance.longValue() - loanSum + oldAmount)
                    && (newValue <= savingAccountBalance.longValue() - savingSum + oldAmount)) {

                BigDecimal newMinBalance = null;
                if (ammount > availableInvestedAmount) {
                    final Long newMinBal = ammount - availableInvestedAmount;
                    newMinBalance = availableMinRequiredBal.add(new BigDecimal(newMinBal));
                } else if (ammount < availableInvestedAmount) {
                    final Long newMinBal = availableInvestedAmount - ammount;
                    newMinBalance = availableMinRequiredBal.subtract(new BigDecimal(newMinBal));
                } else if (ammount == availableInvestedAmount) {
                    newMinBalance = new BigDecimal(ammount);
                }

                account.setMinRequiredBalance(newMinBalance);
                this.savingAccount.saveAndFlush(account);
                investment.updateInvestedAmount(newValue);
            } else {
                if (newValue > (loanAccountBalance.longValue() - loanSum + oldAmount)) { throw new MoreThanLoanAmmountException(); }
                if (newValue > savingAccountBalance.longValue() - savingSum + oldAmount) { throw new NoFundsAvailableException(); }

            }

        }
        if (!changes.isEmpty()) {
            this.repositoryWrapper.saveAndFlush(investment);
        }

        return new CommandProcessingResultBuilder().build();

    }

    @Override

    public CommandProcessingResult updateLoanInvestment(final Long loanId, final JsonCommand command) {
        List<Long> savingInvestedAmount = new ArrayList<Long>();
        List<Long> loanInvestedAmount = new ArrayList<Long>();

        Long id = null;
        Long newid = null;
        Long loanSum = 0L;
        Long savingSum = 0L;

        final Long savingId = command.longValueOfParameterNamed("savingId");
        final Long ammount = command.longValueOfParameterNamed("investedAmounts");
        final Long oldAmount = command.longValueOfParameterNamed("oldAmount");
        final Long oldSavingId = command.longValueOfParameterNamed("oldSavingId");

        final String startDate = command.stringValueOfParameterNamed("startDate");

        id = this.savingInvestment.retriveLoanInvestmentIdForUpdate(loanId, oldSavingId, startDate);
        final Investment loanInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);

        final Investment investment = this.repositoryWrapper.findWithNotFoundDetection(id);
        final Map<String, Object> changes = investment.update(command);
        final int x = changes.size();
        if (changes.containsKey(InvestmentConstants.savingIdParamName)) {

            final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.savingIdParamName);
            newid = this.savingInvestment.retriveLoanInvestmentId(loanId, newValue, startDate);
            if (newid == null) {
                investment.updateSavingId(newValue);
            } else {
                throw new InvestmentAlreadyExistsException();
            }
        }
        if (changes.containsKey(InvestmentConstants.LOANINVESTMENT_RESOURCE_NAME)) {
            final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.SAVINGINVESTMENT_RESOURCE_NAME);
            final SavingsAccountData savingBalance = this.savingAccountDetails.retrieveOne(savingId);
            final BigDecimal savingAccountBalance = savingBalance.getSummary().getAccountBalance();
            savingInvestedAmount = this.savingInvestment.retriveInvestedAmountBySavingId(savingId);

            for (final Long savingammount : savingInvestedAmount) {
                savingSum = savingSum + savingammount;
            }

            final LoanAccountData loanBalance = this.loanReadPlatformService.retrieveOne(loanId);
            final BigDecimal loanAccountBalance = loanBalance.getTotalOutstandingAmount();
            loanInvestedAmount = this.savingInvestment.retriveInvestedAmountByLoanId(loanId);
            for (final Long loanammount : loanInvestedAmount) {
                loanSum = loanSum + loanammount;
            }

            if ((newValue <= loanAccountBalance.longValue() - loanSum + oldAmount)
                    && (newValue <= savingAccountBalance.longValue() - savingSum + oldAmount)) {
                investment.updateInvestedAmount(newValue);
            } else {
                if (newValue > (loanAccountBalance.longValue() - loanSum + oldAmount)) { throw new MoreThanLoanAmmountException(); }
                if (newValue > savingAccountBalance.longValue() - savingSum + oldAmount) { throw new NoFundsAvailableException(); }

            }

        }
        if (!changes.isEmpty()) {
            this.repositoryWrapper.saveAndFlush(investment);
        }

        return new CommandProcessingResultBuilder().build();

    }

    @Override
    public CommandProcessingResult deleteSavingInvestment(final Long savingId, final JsonCommand command) {

        int id;
        Long idAsLongValue = 0L;
        final String startDate = command.stringValueOfParameterNamed("startDate");
        final Long loanId = command.longValueOfParameterNamed("loanId");
        id = this.savingInvestment.retriveSavingInvestmentIdForClose(savingId, loanId, startDate);
        idAsLongValue = new Long(id);
        final Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(idAsLongValue);
        final Long investedAmount = savingInvestment.getInvestedAmount();

        final SavingsAccount account = this.savingAccount.findOne(savingId);
        final BigDecimal availableMinRequiredBal = account.getMinRequiredBalance();
        final BigDecimal newMinBal = availableMinRequiredBal.subtract(new BigDecimal(investedAmount));
        /*
         * Long minBalance = newMinBal.longValue();
         *
         * if(minBalance >= 0){ account.setMinRequiredBalance(newMinBal);
         * this.savingAccount.save(account); }
         */
        this.repositoryWrapper.delete(savingInvestment);

        // TODO Auto-generated method stub
        return new CommandProcessingResultBuilder().build();
    }

    @Override
    public CommandProcessingResult deleteLoanInvestment(final Long loanId, final JsonCommand command) {

        Long id = null;
        final Long savingId = command.longValueOfParameterNamed("savingId");
        final String startDate = command.stringValueOfParameterNamed("startDate");
        id = this.savingInvestment.retriveLoanInvestmentId(loanId, savingId, startDate);
        final Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
        this.repositoryWrapper.delete(savingInvestment);

        return new CommandProcessingResultBuilder().build();
    }

    @Override
    public CommandProcessingResult closeSavingInvestment(final Long savingId, final JsonCommand command) {

        // Long id = 0L;
        final Long loanId = command.longValueOfParameterNamed("loanId");
        final String closeDate = command.stringValueOfParameterNamed("closeDate");
        final String startDate = command.stringValueOfParameterNamed("startDate");
        // String startDate = new String();

        final DateFormat formateDate = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

        try {

            final Date closedDate = formateDate.parse(closeDate);
            final Date startedDate = formateDate.parse(startDate);

            final LocalDate investmentClosedDate = new LocalDate(closedDate);
            final LocalDate investmentStartedDate = new LocalDate(startedDate);

            if (investmentClosedDate.isAfter(investmentStartedDate) || investmentClosedDate.equals(investmentStartedDate)) {

                final Long id = this.savingInvestment.retriveSavingInvestmentId(savingId, loanId, investmentStartedDate);
                final Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
                savingInvestment.setCloseDate(closedDate);
                final Long investedAmount = savingInvestment.getInvestedAmount();
                final SavingsAccount account = this.savingAccount.findOne(savingId);

                final BigDecimal availableMinRequiredBal = account.getMinRequiredBalance();
                final BigDecimal newMinBal = availableMinRequiredBal.subtract(new BigDecimal(investedAmount));
                final Long minBal = newMinBal.longValue();

                if (minBal >= 0) {
                    account.setMinRequiredBalance(newMinBal);
                    this.savingAccount.save(account);
                }

                this.repositoryWrapper.save(savingInvestment);
            } else {
                throw new InvestmentCloseDateOnOrAfterInvestmentStartDateException();
            }
        } catch (final ParseException e) {
            // TODO Auto-generated catch blocky
            e.printStackTrace();
        }

        return new CommandProcessingResultBuilder().build();
    }

    @Override
    public CommandProcessingResult closeLoanInvestment(final Long loanId, final JsonCommand command) {

        final Long savingId = command.longValueOfParameterNamed("savingId");
        final String closeDate = command.stringValueOfParameterNamed("closeDate");
        final String startDate = command.stringValueOfParameterNamed("startDate");

        final DateFormat formateDate = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

        try {

            final Date closedDate = formateDate.parse(closeDate);
            final Date startedDate = formateDate.parse(startDate);

            final LocalDate investmentClosedDate = new LocalDate(closedDate);
            final LocalDate investmentStartDate = new LocalDate(startedDate);

            if (investmentClosedDate.isAfter(investmentStartDate) || investmentClosedDate.equals(investmentStartDate)) {

                final Long id = this.savingInvestment.retriveSavingInvestmentId(savingId, loanId, investmentStartDate);
                final Investment loanInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
                loanInvestment.setCloseDate(closedDate);
                final Long investedAmount = loanInvestment.getInvestedAmount();
                final SavingsAccount account = this.savingAccount.findOne(savingId);
                final BigDecimal availableMinRequiredBal = account.getMinRequiredBalance();
                final BigDecimal newMinBal = availableMinRequiredBal.subtract(new BigDecimal(investedAmount));
                final Long minBal = newMinBal.longValue();

                if (minBal >= 0) {
                    account.setMinRequiredBalance(newMinBal);
                    this.savingAccount.save(account);
                }

                this.repositoryWrapper.save(loanInvestment);

            } else {
                throw new InvestmentCloseDateOnOrAfterInvestmentStartDateException();
            }
        } catch (final ParseException e) {
            // TODO Auto-generated catch blocky
            e.printStackTrace();
        }

        return new CommandProcessingResultBuilder().build();

    }
}
