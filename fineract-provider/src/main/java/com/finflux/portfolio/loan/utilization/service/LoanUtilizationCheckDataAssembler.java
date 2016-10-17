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

    public LoanUtilizationCheck assembleCreateForm(final JsonCommand command) {

        final JsonElement element = command.parsedJson();

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

        final LoanUtilizationCheck loanUtilizationCheck = LoanUtilizationCheck.create(toBeAuditedBy, auditeScheduledOn, auditDoneBy,
                auditDoneOn);

        final List<LoanUtilizationCheckDetail> loanUtilizationCheckDetails = assembleLoanUtilizationCheckDetails(loanUtilizationCheck,
                element);

        if (loanUtilizationCheck != null && loanUtilizationCheckDetails != null && loanUtilizationCheckDetails.size() > 0) {
            loanUtilizationCheck.addAllLoanUtilizationCheckDetails(loanUtilizationCheckDetails);
        }
        return loanUtilizationCheck;
    }

    @SuppressWarnings("unused")
    private List<LoanUtilizationCheckDetail> assembleLoanUtilizationCheckDetails(final LoanUtilizationCheck loanUtilizationCheck,
            final JsonElement element) {

        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final List<LoanUtilizationCheckDetail> loanUtilizationCheckDetails = new ArrayList<LoanUtilizationCheckDetail>();
        final JsonArray loanUtilizationCheckDetailsArray = this.fromApiJsonHelper.extractJsonArrayNamed(
                LoanUtilizationCheckApiConstants.loanUtilizationCheckDetailsParamName, element);
        if (loanUtilizationCheckDetailsArray != null && loanUtilizationCheckDetailsArray.size() > 0) {
            for (int i = 0; i < loanUtilizationCheckDetailsArray.size(); i++) {
                final JsonObject loanUtilizationElement = loanUtilizationCheckDetailsArray.get(i).getAsJsonObject();

                final Long loanId = this.fromApiJsonHelper.extractLongNamed(LoanUtilizationCheckApiConstants.loanIdParamName,
                        loanUtilizationElement);
                final Loan loan = this.loanRepository.findOneWithNotFoundDetection(loanId);

                final JsonArray utilizationDetailsArray = this.fromApiJsonHelper.extractJsonArrayNamed(
                        LoanUtilizationCheckApiConstants.utilizationDetailsParamName, loanUtilizationElement);
                if (utilizationDetailsArray != null && utilizationDetailsArray.size() > 0) {
                    for (int j = 0; j < utilizationDetailsArray.size(); j++) {

                        final JsonObject utilizationDetailElement = utilizationDetailsArray.get(j).getAsJsonObject();

                        final Long loanPurposeId = this.fromApiJsonHelper.extractLongNamed(
                                LoanUtilizationCheckApiConstants.loanPurposeIdParamName, utilizationDetailElement);

                        final Boolean isSameAsOriginalPurpose = this.fromApiJsonHelper.extractBooleanNamed(
                                LoanUtilizationCheckApiConstants.isSameAsOriginalPurposeParamName, utilizationDetailElement);

                        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed(
                                LoanUtilizationCheckApiConstants.amountParamName, utilizationDetailElement, locale);

                        final String comment = this.fromApiJsonHelper.extractStringNamed(LoanUtilizationCheckApiConstants.commentParamName,
                                utilizationDetailElement);

                        final LoanUtilizationCheckDetail loanUtilizationCheckDetail = LoanUtilizationCheckDetail.create(
                                loanUtilizationCheck, loan, loanPurposeId, isSameAsOriginalPurpose, amount, comment);

                        loanUtilizationCheckDetails.add(loanUtilizationCheckDetail);
                    }
                } else {
                    final LoanUtilizationCheckDetail loanUtilizationCheckDetail = LoanUtilizationCheckDetail.create(loanUtilizationCheck,
                            loan, null, null, null, null);

                    loanUtilizationCheckDetails.add(loanUtilizationCheckDetail);
                }
            }
        }
        return loanUtilizationCheckDetails;
    }

    public Map<String, Object> assembleUpdateForm(final LoanUtilizationCheck loanUtilizationCheck, final JsonCommand command) {
        final Map<String, Object> changes = loanUtilizationCheck.update(command);
        final List<LoanUtilizationCheckDetail> loanUtilizationCheckDetails = assembleLoanUtilizationCheckDetails(loanUtilizationCheck,
                command.parsedJson());
        loanUtilizationCheck.addAllLoanUtilizationCheckDetails(loanUtilizationCheckDetails);
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