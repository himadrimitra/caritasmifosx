package com.finflux.portfolio.loan.utilization.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
import org.apache.fineract.useradministration.exception.UserNotFoundException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loan.utilization.api.LoanUtilizationCheckApiConstants;
import com.finflux.portfolio.loan.utilization.domain.LoanUtilizationCheck;
import com.finflux.portfolio.loan.utilization.domain.LoanUtilizationCheckDetail;
import com.finflux.portfolio.loan.utilization.exception.AuditDateBeforeDisbursementDateException;
import com.finflux.portfolio.loan.utilization.exception.LoanPurpuseAmountMoreThanDisbursalAmountException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class LoanUtilizationCheckDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final AppUserRepositoryWrapper appUserRepository;
    private final LoanRepositoryWrapper loanRepository;
    private final LoanUtilizationCheckReadPlatformService readPlatformService;

    @Autowired
    public LoanUtilizationCheckDataAssembler(final FromJsonHelper fromApiJsonHelper, final AppUserRepositoryWrapper appUserRepository,
            final LoanRepositoryWrapper loanRepository, final LoanUtilizationCheckReadPlatformService readPlatformService) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.appUserRepository = appUserRepository;
        this.loanRepository = loanRepository;
        this.readPlatformService = readPlatformService;
    }

    public List<LoanUtilizationCheck> assembleCreateForm(final JsonCommand command) {
        final List<LoanUtilizationCheck> loanUtilizationChecks = new ArrayList<>();
        final JsonElement parentElement = command.parsedJson();
        final JsonObject parentElementObj = parentElement.getAsJsonObject();
        final Map<Long,BigDecimal> totalLoanAmountUtilizedBasedOnLoanId = new HashMap<>();
        if (parentElement.isJsonObject() && !command.parameterExists(LoanUtilizationCheckApiConstants.loanUtilizationChecksParamName)) {
            final LoanUtilizationCheck loanUtilizationCheck = assembleCreateFormEachObject(parentElement.getAsJsonObject(),totalLoanAmountUtilizedBasedOnLoanId);
            loanUtilizationChecks.add(loanUtilizationCheck);
        } else if (command.parameterExists(LoanUtilizationCheckApiConstants.loanUtilizationChecksParamName)) {
            final JsonArray array = parentElementObj.get(LoanUtilizationCheckApiConstants.loanUtilizationChecksParamName).getAsJsonArray();
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();
                    final LoanUtilizationCheck loanUtilizationCheck = assembleCreateFormEachObject(element,totalLoanAmountUtilizedBasedOnLoanId);
                    loanUtilizationChecks.add(loanUtilizationCheck);
                }
            }
        }
        return loanUtilizationChecks;
    }

    private LoanUtilizationCheck assembleCreateFormEachObject(final JsonObject element, final Map<Long, BigDecimal> totalLoanAmountUtilizedBasedOnLoanId) {
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final Long loanId = this.fromApiJsonHelper.extractLongNamed(LoanUtilizationCheckApiConstants.loanIdParamName, element);
        final Loan loan = this.loanRepository.findOneWithNotFoundDetection(loanId);
        AppUser toBeAuditedBy = null;
        final Long toBeAuditedById = this.fromApiJsonHelper.extractLongNamed(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName,
                element);
        if (toBeAuditedById != null) {
            toBeAuditedBy = this.appUserRepository.findOneWithNotFoundDetection(toBeAuditedById);
            if (toBeAuditedBy == null) { throw new UserNotFoundException(toBeAuditedById); }
        }

        Date auditeScheduledOn = null;
        final LocalDate localDateAuditeScheduledOn = this.fromApiJsonHelper.extractLocalDateNamed(
                LoanUtilizationCheckApiConstants.auditeScheduledOnParamName, element);
        if (localDateAuditeScheduledOn != null) {
            auditeScheduledOn = localDateAuditeScheduledOn.toDate();
        }

        AppUser auditDoneBy = null;
        final Long auditDoneById = this.fromApiJsonHelper
                .extractLongNamed(LoanUtilizationCheckApiConstants.auditDoneByIdParamName, element);
        if (auditDoneById != null) {
            auditDoneBy = this.appUserRepository.findOneWithNotFoundDetection(auditDoneById);
        }

        Date auditDoneOn = null;
        final LocalDate localDateAuditDoneOn = this.fromApiJsonHelper
                .extractLocalDateNamed(LoanUtilizationCheckApiConstants.auditDoneOnParamName, element);

        if (localDateAuditDoneOn
                .isBefore(loan.getDisbursementDate())) { throw new AuditDateBeforeDisbursementDateException(localDateAuditDoneOn); }

        final LoanUtilizationCheck loanUtilizationCheck = LoanUtilizationCheck.create(loan, toBeAuditedBy, auditeScheduledOn, auditDoneBy,
                localDateAuditDoneOn.toDate());

        final LoanUtilizationCheckDetail loanUtilizationCheckDetail = assembleLoanUtilizationCheckDetail(loanUtilizationCheck,
                element.get(LoanUtilizationCheckApiConstants.loanUtilizationDetailsParamName).getAsJsonObject(), locale,totalLoanAmountUtilizedBasedOnLoanId);

        if (loanUtilizationCheck != null && loanUtilizationCheckDetail != null) {
            loanUtilizationCheck.updateLoanUtilizationCheckDetails(loanUtilizationCheckDetail);
        }
        return loanUtilizationCheck;
    }

    private LoanUtilizationCheckDetail assembleLoanUtilizationCheckDetail(final LoanUtilizationCheck loanUtilizationCheck,
            final JsonElement utilizationDetailElement, final Locale locale, final Map<Long, BigDecimal> totalLoanAmountUtilizedBasedOnLoanId) {
        LoanUtilizationCheckDetail loanUtilizationCheckDetail = loanUtilizationCheck.getLoanUtilizationCheckDetail();

        final Long loanPurposeId = this.fromApiJsonHelper.extractLongNamed(LoanUtilizationCheckApiConstants.loanPurposeIdParamName,
                utilizationDetailElement);

        final Boolean isSameAsOriginalPurpose = this.fromApiJsonHelper.extractBooleanNamed(
                LoanUtilizationCheckApiConstants.isSameAsOriginalPurposeParamName, utilizationDetailElement);

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed(LoanUtilizationCheckApiConstants.amountParamName,
                utilizationDetailElement, locale);

        final String comment = this.fromApiJsonHelper.extractStringNamed(LoanUtilizationCheckApiConstants.commentParamName,
                utilizationDetailElement);
        
        BigDecimal totalLoanAmountUtilized = BigDecimal.ZERO;
        if (totalLoanAmountUtilizedBasedOnLoanId.containsKey(loanUtilizationCheck.getLoan().getId())) {
            totalLoanAmountUtilized = totalLoanAmountUtilizedBasedOnLoanId.get(loanUtilizationCheck.getLoan().getId());
        } else {
            totalLoanAmountUtilized = this.readPlatformService.retrieveUtilityAmountByLoanId(loanUtilizationCheck.getLoan().getId());
            if (loanUtilizationCheckDetail != null && loanUtilizationCheckDetail.getId() != null) {
                if (loanUtilizationCheckDetail.getLoanUtilizedAmount() != null) {
                    totalLoanAmountUtilized = totalLoanAmountUtilized.subtract(loanUtilizationCheckDetail.getLoanUtilizedAmount());
                }
            }

        }
        
        if (loanUtilizationCheckDetail != null) {
            loanUtilizationCheckDetail.update(loanUtilizationCheck, loanPurposeId, isSameAsOriginalPurpose, amount, comment);
        } else {
            loanUtilizationCheckDetail = LoanUtilizationCheckDetail.create(loanUtilizationCheck, loanPurposeId, isSameAsOriginalPurpose,
                    amount, comment);
        }
        
        
        if (amount != null) {
            totalLoanAmountUtilized = totalLoanAmountUtilized.add(amount);
            totalLoanAmountUtilizedBasedOnLoanId.put(loanUtilizationCheck.getLoan().getId(), totalLoanAmountUtilized);
        }
        validateTotalLoanAmountUtilizedWithPrincipleAmount(loanUtilizationCheck.getLoan(), totalLoanAmountUtilized);

        
        
        return loanUtilizationCheckDetail;
    }

    public Map<String, Object> assembleUpdateForm(final LoanUtilizationCheck loanUtilizationCheck, final JsonCommand command) {
        final JsonElement parentElement = command.parsedJson();
        final JsonObject parentElementObj = parentElement.getAsJsonObject();
        final JsonObject topLevelJsonElement = parentElement.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final Map<String, Object> changes = loanUtilizationCheck.update(command);
        final Map<Long, BigDecimal> totalLoanAmountUtilizedBasedOnLoanId = new HashMap<>();
        final LoanUtilizationCheckDetail loanUtilizationCheckDetail = assembleLoanUtilizationCheckDetail(loanUtilizationCheck,
                parentElementObj.get(LoanUtilizationCheckApiConstants.loanUtilizationDetailsParamName).getAsJsonObject(), locale,totalLoanAmountUtilizedBasedOnLoanId);
        loanUtilizationCheck.updateLoanUtilizationCheckDetails(loanUtilizationCheckDetail);
        if (changes.containsKey(LoanUtilizationCheckApiConstants.loanIdParamName)) {
            final Long loanId = command.longValueOfParameterNamed(LoanUtilizationCheckApiConstants.loanIdParamName);
            final Loan loan = this.loanRepository.findOneWithNotFoundDetection(loanId);
            loanUtilizationCheck.updateLoan(loan);
        }
        if (changes.containsKey(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName)) {
            final Long toBeAuditedById = command.longValueOfParameterNamed(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName);
            final AppUser toBeAuditedBy = this.appUserRepository.findOneWithNotFoundDetection(toBeAuditedById);
            if (toBeAuditedBy == null) { throw new UserNotFoundException(toBeAuditedById); }
            loanUtilizationCheck.updateToBeAuditedBy(toBeAuditedBy);
        }
        if (changes.containsKey(LoanUtilizationCheckApiConstants.auditDoneByIdParamName)) {
            final Long auditDoneById = command.longValueOfParameterNamed(LoanUtilizationCheckApiConstants.auditDoneByIdParamName);
            final AppUser auditDoneBy = this.appUserRepository.findOneWithNotFoundDetection(auditDoneById);
            loanUtilizationCheck.updateAuditDoneBy(auditDoneBy);
        }
        if (changes.containsKey(LoanUtilizationCheckApiConstants.auditDoneOnParamName)) {
            final LocalDate auditDoneOn = command.localDateValueOfParameterNamed(LoanUtilizationCheckApiConstants.auditDoneOnParamName);
            final Long loanId = command.longValueOfParameterNamed(LoanUtilizationCheckApiConstants.loanIdParamName);
            final Loan loan = this.loanRepository.findOneWithNotFoundDetection(loanId);
            if (auditDoneOn.isBefore(loan.getDisbursementDate())) { throw new AuditDateBeforeDisbursementDateException(auditDoneOn); }
            loanUtilizationCheck.setAuditDoneOn(auditDoneOn.toDate());
        }
        return changes;
    }
    public void validateTotalLoanAmountUtilizedWithPrincipleAmount(Loan loan,BigDecimal amount){
        if(loan.getPrincpal().getAmount().compareTo(amount) < 0){
            throw new LoanPurpuseAmountMoreThanDisbursalAmountException(amount.longValue());
        }
        
    }
}