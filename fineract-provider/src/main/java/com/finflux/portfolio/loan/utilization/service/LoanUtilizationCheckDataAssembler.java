package com.finflux.portfolio.loan.utilization.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepository;
import org.apache.fineract.useradministration.exception.UserNotFoundException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loan.utilization.api.LoanUtilizationCheckApiConstants;
import com.finflux.portfolio.loan.utilization.domain.LoanUtilizationCheck;
import com.finflux.portfolio.loan.utilization.domain.LoanUtilizationCheckDetail;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class LoanUtilizationCheckDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final StaffRepositoryWrapper staffRepository;
    private final AppUserRepository appUserRepository;
    private final LoanRepositoryWrapper loanRepository;

    @Autowired
    public LoanUtilizationCheckDataAssembler(final FromJsonHelper fromApiJsonHelper, final StaffRepositoryWrapper staffRepository,
            final AppUserRepository appUserRepository, final LoanRepositoryWrapper loanRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.staffRepository = staffRepository;
        this.appUserRepository = appUserRepository;
        this.loanRepository = loanRepository;
    }

    public List<LoanUtilizationCheck> assembleCreateForm(final JsonCommand command) {
        final List<LoanUtilizationCheck> loanUtilizationChecks = new ArrayList<>();
        final JsonElement parentElement = command.parsedJson();
        final JsonObject parentElementObj = parentElement.getAsJsonObject();
        if (parentElement.isJsonObject() && !command.parameterExists(LoanUtilizationCheckApiConstants.loanUtilizationChecksParamName)) {
            final LoanUtilizationCheck loanUtilizationCheck = assembleCreateFormEachObject(parentElement.getAsJsonObject());
            loanUtilizationChecks.add(loanUtilizationCheck);
        } else if (command.parameterExists(LoanUtilizationCheckApiConstants.loanUtilizationChecksParamName)) {
            final JsonArray array = parentElementObj.get(LoanUtilizationCheckApiConstants.loanUtilizationChecksParamName).getAsJsonArray();
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject element = array.get(i).getAsJsonObject();
                    final LoanUtilizationCheck loanUtilizationCheck = assembleCreateFormEachObject(element);
                    loanUtilizationChecks.add(loanUtilizationCheck);
                }
            }
        }
        return loanUtilizationChecks;
    }

    private LoanUtilizationCheck assembleCreateFormEachObject(final JsonObject element) {
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final Long loanId = this.fromApiJsonHelper.extractLongNamed(LoanUtilizationCheckApiConstants.loanIdParamName, element);
        final Loan loan = this.loanRepository.findOneWithNotFoundDetection(loanId);
        AppUser toBeAuditedBy = null;
        final Long toBeAuditedById = this.fromApiJsonHelper.extractLongNamed(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName,
                element);
        if (toBeAuditedById != null) {
            toBeAuditedBy = this.appUserRepository.findOne(toBeAuditedById);
            if (toBeAuditedBy == null) { throw new UserNotFoundException(toBeAuditedById); }
        }

        Date auditeScheduledOn = null;
        final LocalDate localDateAuditeScheduledOn = this.fromApiJsonHelper.extractLocalDateNamed(
                LoanUtilizationCheckApiConstants.auditeScheduledOnParamName, element);
        if (localDateAuditeScheduledOn != null) {
            auditeScheduledOn = localDateAuditeScheduledOn.toDate();
        }

        Staff auditDoneBy = null;
        final Long auditDoneById = this.fromApiJsonHelper
                .extractLongNamed(LoanUtilizationCheckApiConstants.auditDoneByIdParamName, element);
        if (auditDoneById != null) {
            auditDoneBy = this.staffRepository.findOneWithNotFoundDetection(auditDoneById);
        }

        Date auditDoneOn = null;
        final LocalDate localDateAuditDoneOn = this.fromApiJsonHelper.extractLocalDateNamed(
                LoanUtilizationCheckApiConstants.auditDoneOnParamName, element);
        if (localDateAuditDoneOn != null) {
            auditDoneOn = localDateAuditDoneOn.toDate();
        }

        final LoanUtilizationCheck loanUtilizationCheck = LoanUtilizationCheck.create(loan, toBeAuditedBy, auditeScheduledOn, auditDoneBy,
                auditDoneOn);

        final LoanUtilizationCheckDetail loanUtilizationCheckDetail = assembleLoanUtilizationCheckDetail(loanUtilizationCheck,
                element.get(LoanUtilizationCheckApiConstants.loanUtilizationDetailsParamName).getAsJsonObject(), locale);

        if (loanUtilizationCheck != null && loanUtilizationCheckDetail != null) {
            loanUtilizationCheck.updateLoanUtilizationCheckDetails(loanUtilizationCheckDetail);
        }
        return loanUtilizationCheck;
    }

    private LoanUtilizationCheckDetail assembleLoanUtilizationCheckDetail(final LoanUtilizationCheck loanUtilizationCheck,
            final JsonElement utilizationDetailElement, final Locale locale) {
        LoanUtilizationCheckDetail loanUtilizationCheckDetail = loanUtilizationCheck.getLoanUtilizationCheckDetail();

        final Long loanPurposeId = this.fromApiJsonHelper.extractLongNamed(LoanUtilizationCheckApiConstants.loanPurposeIdParamName,
                utilizationDetailElement);

        final Boolean isSameAsOriginalPurpose = this.fromApiJsonHelper.extractBooleanNamed(
                LoanUtilizationCheckApiConstants.isSameAsOriginalPurposeParamName, utilizationDetailElement);

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed(LoanUtilizationCheckApiConstants.amountParamName,
                utilizationDetailElement, locale);

        final String comment = this.fromApiJsonHelper.extractStringNamed(LoanUtilizationCheckApiConstants.commentParamName,
                utilizationDetailElement);
        if (loanUtilizationCheckDetail != null) {
            loanUtilizationCheckDetail.update(loanUtilizationCheck, loanPurposeId, isSameAsOriginalPurpose, amount, comment);
        } else {
            loanUtilizationCheckDetail = LoanUtilizationCheckDetail.create(loanUtilizationCheck, loanPurposeId, isSameAsOriginalPurpose,
                    amount, comment);
        }
        return loanUtilizationCheckDetail;
    }

    public Map<String, Object> assembleUpdateForm(final LoanUtilizationCheck loanUtilizationCheck, final JsonCommand command) {
        final JsonElement parentElement = command.parsedJson();
        final JsonObject parentElementObj = parentElement.getAsJsonObject();
        final JsonObject topLevelJsonElement = parentElement.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final Map<String, Object> changes = loanUtilizationCheck.update(command);
        final LoanUtilizationCheckDetail loanUtilizationCheckDetail = assembleLoanUtilizationCheckDetail(loanUtilizationCheck,
                parentElementObj.get(LoanUtilizationCheckApiConstants.loanUtilizationDetailsParamName).getAsJsonObject(), locale);
        loanUtilizationCheck.updateLoanUtilizationCheckDetails(loanUtilizationCheckDetail);
        if (changes.containsKey(LoanUtilizationCheckApiConstants.loanIdParamName)) {
            final Long loanId = command.longValueOfParameterNamed(LoanUtilizationCheckApiConstants.loanIdParamName);
            final Loan loan = this.loanRepository.findOneWithNotFoundDetection(loanId);
            loanUtilizationCheck.updateLoan(loan);
        }
        if (changes.containsKey(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName)) {
            final Long toBeAuditedById = command.longValueOfParameterNamed(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName);
            final AppUser toBeAuditedBy = this.appUserRepository.findOne(toBeAuditedById);
            if (toBeAuditedBy == null) { throw new UserNotFoundException(toBeAuditedById); }
            loanUtilizationCheck.updateToBeAuditedBy(toBeAuditedBy);
        }
        if (changes.containsKey(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName)) {
            final Long toBeAuditedById = command.longValueOfParameterNamed(LoanUtilizationCheckApiConstants.toBeAuditedByIdParamName);
            final AppUser toBeAuditedBy = this.appUserRepository.findOne(toBeAuditedById);
            if (toBeAuditedBy == null) { throw new UserNotFoundException(toBeAuditedById); }
            loanUtilizationCheck.updateToBeAuditedBy(toBeAuditedBy);
        }
        if (changes.containsKey(LoanUtilizationCheckApiConstants.auditDoneByIdParamName)) {
            final Long auditDoneById = command.longValueOfParameterNamed(LoanUtilizationCheckApiConstants.auditDoneByIdParamName);
            final Staff auditDoneBy = this.staffRepository.findOneWithNotFoundDetection(auditDoneById);
            loanUtilizationCheck.updateAuditDoneBy(auditDoneBy);
        }
        return changes;
    }
}