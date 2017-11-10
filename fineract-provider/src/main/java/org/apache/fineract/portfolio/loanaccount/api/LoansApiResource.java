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
package org.apache.fineract.portfolio.loanaccount.api;

import static org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations.interestType;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.api.JsonQuery;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.data.PortfolioAccountDTO;
import org.apache.fineract.portfolio.account.data.PortfolioAccountData;
import org.apache.fineract.portfolio.account.service.AccountAssociationsReadPlatformService;
import org.apache.fineract.portfolio.account.service.PortfolioAccountReadPlatformService;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.accountdetails.service.AccountDetailsReadPlatformService;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.service.CalendarReadPlatformService;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.collateral.data.CollateralData;
import org.apache.fineract.portfolio.collateral.service.CollateralReadPlatformService;
import org.apache.fineract.portfolio.collaterals.data.PledgeData;
import org.apache.fineract.portfolio.collaterals.service.PledgeReadPlatformService;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.floatingrates.data.InterestRatePeriodData;
import org.apache.fineract.portfolio.fund.api.FundApiConstants;
import org.apache.fineract.portfolio.fund.data.FundData;
import org.apache.fineract.portfolio.fund.service.FundReadPlatformService;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.group.service.GroupReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.DisbursementData;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.data.LoanApprovalData;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTermVariationsData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.PaidInAdvanceData;
import org.apache.fineract.portfolio.loanaccount.data.RepaymentScheduleRelatedLoanData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTermVariationType;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.exception.LoanTemplateTypeRequiredException;
import org.apache.fineract.portfolio.loanaccount.exception.NotSupportedLoanTemplateTypeException;
import org.apache.fineract.portfolio.loanaccount.guarantor.data.GuarantorData;
import org.apache.fineract.portfolio.loanaccount.guarantor.service.GuarantorReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModel;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleCalculationPlatformService;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleHistoryReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanChargeReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductBorrowerCycleVariationData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.data.TransactionProcessingStrategyData;
import org.apache.fineract.portfolio.loanproduct.domain.InterestMethod;
import org.apache.fineract.portfolio.loanproduct.domain.LoanTransactionProcessingStrategy;
import org.apache.fineract.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.note.data.NoteData;
import org.apache.fineract.portfolio.note.domain.NoteType;
import org.apache.fineract.portfolio.note.service.NoteReadPlatformServiceImpl;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.finflux.common.util.FinfluxStringUtils;
import com.finflux.portfolio.loan.purpose.data.LoanPurposeData;
import com.finflux.portfolio.loan.purpose.service.LoanPurposeGroupReadPlatformService;
import com.google.gson.JsonElement;

@Path("/loans")
@Component
@Scope("singleton")
public class LoansApiResource {

    private final Set<String> LOAN_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "accountNo", "status", "externalId", "clientId",
            "group", "loanProductId", "loanProductName", "loanProductDescription", "isLoanProductLinkedToFloatingRate", "fundId",
            "fundName", "loanPurposeId", "loanPurposeName", "loanOfficerId", "loanOfficerName", "currency", "principal", "totalOverpaid",
            "inArrearsTolerance", "termFrequency", "termPeriodFrequencyType", "numberOfRepayments", "repaymentEvery",
            "interestRatePerPeriod", "annualInterestRate", "repaymentFrequencyType", "transactionProcessingStrategyId",
            "transactionProcessingStrategyName", "interestRateFrequencyType", "amortizationType", "interestType",
            "interestCalculationPeriodType", LoanProductConstants.allowPartialPeriodInterestCalcualtionParamName,
            "expectedFirstRepaymentOnDate", "graceOnPrincipalPayment", "recurringMoratoriumOnPrincipalPeriods", "graceOnInterestPayment",
            "graceOnInterestCharged", "interestChargedFromDate", "timeline", "totalFeeChargesAtDisbursement", "summary",
            "repaymentSchedule", "transactions", "charges", "collateral", "guarantors", "meeting", "productOptions",
            "amortizationTypeOptions", "interestTypeOptions", "interestCalculationPeriodTypeOptions", "repaymentFrequencyTypeOptions",
            "repaymentFrequencyNthDayTypeOptions", "repaymentFrequencyDaysOfWeekTypeOptions", "termFrequencyTypeOptions",
            "interestRateFrequencyTypeOptions", "fundOptions", "repaymentStrategyOptions", "chargeOptions", "loanOfficerOptions",
            "loanPurposeOptions", "loanCollateralOptions", "chargeTemplate", "calendarOptions", "syncDisbursementWithMeeting",
            "loanCounter", "loanProductCounter", "notes", "accountLinkingOptions", "linkedAccount", "interestRateDifferential",
            "isFloatingInterestRate", "interestRatesPeriods", LoanApiConstants.canUseForTopup, LoanApiConstants.isTopup,
            LoanApiConstants.loanIdToClose, LoanApiConstants.topupAmount, LoanApiConstants.clientActiveLoanOptions,
            LoanApiConstants.paymentOptionsParamName, "interestRatesListPerPeriod"));

    private final Set<String> LOAN_APPROVAL_DATA_PARAMETERS = new HashSet<>(Arrays.asList("approvalDate", "approvalAmount"));
    private final String resourceNameForPermissions = "LOAN";

    private final PlatformSecurityContext context;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final LoanDropdownReadPlatformService dropdownReadPlatformService;
    private final FundReadPlatformService fundReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final LoanChargeReadPlatformService loanChargeReadPlatformService;
    private final CollateralReadPlatformService loanCollateralReadPlatformService;
    private final LoanScheduleCalculationPlatformService calculationPlatformService;
    private final GuarantorReadPlatformService guarantorReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final GroupReadPlatformService groupReadPlatformService;
    private final DefaultToApiJsonSerializer<LoanAccountData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<LoanApprovalData> loanApprovalDataToApiJsonSerializer;
    private final DefaultToApiJsonSerializer<LoanScheduleData> loanScheduleToApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final FromJsonHelper fromJsonHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CalendarReadPlatformService calendarReadPlatformService;
    private final NoteReadPlatformServiceImpl noteReadPlatformService;
    private final PortfolioAccountReadPlatformService portfolioAccountReadPlatformService;
    private final AccountAssociationsReadPlatformService accountAssociationsReadPlatformService;
    private final LoanScheduleHistoryReadPlatformService loanScheduleHistoryReadPlatformService;
    private final PledgeReadPlatformService pledgeReadPlatformService;
    private final AccountDetailsReadPlatformService accountDetailsReadPlatformService;
    private final LoanPurposeGroupReadPlatformService loanPurposeGroupReadPlatformService;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    private final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory;
    private final ClientReadPlatformService clientReadPlatformService;

    @Autowired
    public LoansApiResource(final PlatformSecurityContext context, final LoanReadPlatformService loanReadPlatformService,
            final LoanProductReadPlatformService loanProductReadPlatformService,
            final LoanDropdownReadPlatformService dropdownReadPlatformService, final FundReadPlatformService fundReadPlatformService,
            final ChargeReadPlatformService chargeReadPlatformService, final LoanChargeReadPlatformService loanChargeReadPlatformService,
            final CollateralReadPlatformService loanCollateralReadPlatformService,
            final LoanScheduleCalculationPlatformService calculationPlatformService,
            final GuarantorReadPlatformService guarantorReadPlatformService,
            final CodeValueReadPlatformService codeValueReadPlatformService, final GroupReadPlatformService groupReadPlatformService,
            final DefaultToApiJsonSerializer<LoanAccountData> toApiJsonSerializer,
            final DefaultToApiJsonSerializer<LoanApprovalData> loanApprovalDataToApiJsonSerializer,
            final DefaultToApiJsonSerializer<LoanScheduleData> loanScheduleToApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper, final FromJsonHelper fromJsonHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final CalendarReadPlatformService calendarReadPlatformService, final NoteReadPlatformServiceImpl noteReadPlatformService,
            final PortfolioAccountReadPlatformService portfolioAccountReadPlatformServiceImpl,
            final AccountAssociationsReadPlatformService accountAssociationsReadPlatformService,
            final LoanScheduleHistoryReadPlatformService loanScheduleHistoryReadPlatformService,
            final PledgeReadPlatformService pledgeReadPlatformService,
            final AccountDetailsReadPlatformService accountDetailsReadPlatformService,
            final LoanPurposeGroupReadPlatformService loanPurposeGroupReadPlatformService,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService,
            final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory,
            final ClientReadPlatformService clientReadPlatformService) {
        this.context = context;
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
        this.fundReadPlatformService = fundReadPlatformService;
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.loanChargeReadPlatformService = loanChargeReadPlatformService;
        this.loanCollateralReadPlatformService = loanCollateralReadPlatformService;
        this.calculationPlatformService = calculationPlatformService;
        this.guarantorReadPlatformService = guarantorReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.groupReadPlatformService = groupReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.loanApprovalDataToApiJsonSerializer = loanApprovalDataToApiJsonSerializer;
        this.loanScheduleToApiJsonSerializer = loanScheduleToApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.fromJsonHelper = fromJsonHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.calendarReadPlatformService = calendarReadPlatformService;
        this.noteReadPlatformService = noteReadPlatformService;
        this.portfolioAccountReadPlatformService = portfolioAccountReadPlatformServiceImpl;
        this.accountAssociationsReadPlatformService = accountAssociationsReadPlatformService;
        this.loanScheduleHistoryReadPlatformService = loanScheduleHistoryReadPlatformService;
        this.pledgeReadPlatformService = pledgeReadPlatformService;
        this.accountDetailsReadPlatformService = accountDetailsReadPlatformService;
        this.loanPurposeGroupReadPlatformService = loanPurposeGroupReadPlatformService;
        this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
        this.loanRepaymentScheduleTransactionProcessorFactory = loanRepaymentScheduleTransactionProcessorFactory;
        this.clientReadPlatformService = clientReadPlatformService;
    }

    /*
     * This template API is used for loan approval, ideally this should be
     * invoked on loan that are pending for approval. But system does not
     * validate the status of the loan, it returns the template irrespective of
     * loan status
     */

    @GET
    @Path("{loanId}/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveApprovalTemplate(@PathParam("loanId") final Long loanId, @QueryParam("templateType") final String templateType,
            @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        LoanApprovalData loanApprovalTemplate = null;

        if (templateType == null) {
            final String errorMsg = "Loan template type must be provided";
            throw new LoanTemplateTypeRequiredException(errorMsg);
        } else if (templateType.equals("approval")) {
            loanApprovalTemplate = this.loanReadPlatformService.retrieveApprovalTemplate(loanId);
        }

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.loanApprovalDataToApiJsonSerializer.serialize(settings, loanApprovalTemplate, this.LOAN_APPROVAL_DATA_PARAMETERS);

    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String template(@QueryParam("clientId") final Long clientId, @QueryParam("groupId") final Long groupId,
            @QueryParam("productId") final Long productId, @QueryParam("templateType") final String templateType,
            @DefaultValue("false") @QueryParam("staffInSelectedOfficeOnly") final boolean staffInSelectedOfficeOnly,
            @DefaultValue("false") @QueryParam("activeOnly") final boolean onlyActive,
            @DefaultValue("false") @QueryParam("fetchRDAccountOnly") final boolean fetchRDAccountOnly,
            @QueryParam("productApplicableForLoanType") final Integer productApplicableForLoanType,
            @QueryParam("entityType") final Integer entityType, @QueryParam("entityId") final Long entityId,
            @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        // options
        Collection<StaffData> allowedLoanOfficers = null;
        Collection<CodeValueData> loanCollateralOptions = null;
        Collection<CalendarData> calendarOptions = null;
        LoanAccountData newLoanAccount = null;
        Long officeId = null;

        if (productId != null) {
            newLoanAccount = this.loanReadPlatformService.retrieveLoanProductDetailsTemplate(productId, clientId, groupId);
        }

        // template
        final Collection<LoanProductData> productOptions = this.loanProductReadPlatformService.retrieveAllLoanProductsForLookup(onlyActive,
                productApplicableForLoanType, entityType, entityId);

        if (templateType == null) {
            final String errorMsg = "Loan template type must be provided";
            throw new LoanTemplateTypeRequiredException(errorMsg);
        } else if (templateType.equals("collateral")) {
            loanCollateralOptions = this.codeValueReadPlatformService.retrieveCodeValuesByCode("LoanCollateral");
            newLoanAccount = LoanAccountData.collateralTemplate(loanCollateralOptions);
        } else {
            // for JLG loan both client and group details are required
            if (templateType.equals("individual") || templateType.equals("jlg")) {

                if (clientId == null) {
                    newLoanAccount = newLoanAccount == null ? LoanAccountData.emptyTemplate() : newLoanAccount;
                } else {
                    final LoanAccountData loanAccountClientDetails = this.loanReadPlatformService.retrieveClientDetailsTemplate(clientId);

                    officeId = loanAccountClientDetails.officeId();
                    newLoanAccount = newLoanAccount == null ? loanAccountClientDetails
                            : LoanAccountData.populateClientDefaults(newLoanAccount, loanAccountClientDetails);
                }

                // if it's JLG loan add group details
                if (templateType.equals("jlg")) {
                    final GroupGeneralData group = this.groupReadPlatformService.retrieveOne(groupId);
                    newLoanAccount = LoanAccountData.associateGroup(newLoanAccount, group);
                    calendarOptions = this.loanReadPlatformService.retrieveCalendars(groupId);
                }

            } else if (templateType.equals("group") || templateType.equals("glim")) {

                final LoanAccountData loanAccountGroupData = this.loanReadPlatformService.retrieveGroupDetailsTemplate(groupId);
                officeId = loanAccountGroupData.groupOfficeId();
                calendarOptions = this.loanReadPlatformService.retrieveCalendars(groupId);
                newLoanAccount = newLoanAccount == null ? loanAccountGroupData
                        : LoanAccountData.populateGroupDefaults(newLoanAccount, loanAccountGroupData);

            } else if (templateType.equals("jlgbulk")) {
                // get group details along with members in that group
                final LoanAccountData loanAccountGroupData = this.loanReadPlatformService.retrieveGroupAndMembersDetailsTemplate(groupId);
                officeId = loanAccountGroupData.groupOfficeId();
                calendarOptions = this.loanReadPlatformService.retrieveCalendars(groupId);
                newLoanAccount = newLoanAccount == null ? loanAccountGroupData
                        : LoanAccountData.populateGroupDefaults(newLoanAccount, loanAccountGroupData);
                if (productId != null) {
                    final Map<Long, Integer> memberLoanCycle = new HashMap<>();
                    final Collection<ClientData> members = loanAccountGroupData.groupData().clientMembers();
                    for (final ClientData clientData : members) {
                        final Integer loanCounter = this.loanReadPlatformService.retriveLoanCounter(clientData.id(), productId);
                        memberLoanCycle.put(clientData.id(), loanCounter);
                    }
                    newLoanAccount = LoanAccountData.associateMemberVariations(newLoanAccount, memberLoanCycle);
                }

            } else {
                final String errorMsg = "Loan template type '" + templateType + "' is not supported";
                throw new NotSupportedLoanTemplateTypeException(errorMsg, templateType);
            }

            allowedLoanOfficers = this.loanReadPlatformService.retrieveAllowedLoanOfficers(officeId, staffInSelectedOfficeOnly);

            Collection<PortfolioAccountData> accountLinkingOptions = null;
            if (clientId != null) {
                final CurrencyData currencyData = newLoanAccount.currency();
                String currencyCode = null;
                if (currencyData != null) {
                    currencyCode = currencyData.code();
                }
                final long[] accountStatus = { SavingsAccountStatusType.ACTIVE.getValue() };
                final Set<Integer> depositAccountTypes = new HashSet<>();
                if (!fetchRDAccountOnly) {
                    depositAccountTypes.add(DepositAccountType.SAVINGS_DEPOSIT.getValue());
                }
                depositAccountTypes.add(DepositAccountType.RECURRING_DEPOSIT.getValue());
                final PortfolioAccountDTO portfolioAccountDTO = new PortfolioAccountDTO(PortfolioAccountType.SAVINGS.getValue(), clientId,
                        currencyCode, accountStatus, depositAccountTypes);
                accountLinkingOptions = this.portfolioAccountReadPlatformService.retrieveAllForLookup(portfolioAccountDTO);

            }

            // add product options, allowed loan officers and calendar options
            // (calendar options will be null in individual loan)
            newLoanAccount = LoanAccountData.associationsAndTemplate(newLoanAccount, productOptions, allowedLoanOfficers, calendarOptions,
                    accountLinkingOptions);
        }
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, newLoanAccount, this.LOAN_DATA_PARAMETERS);
    }

    @GET
    @Path("tasklookup")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllForTaskLookupBySearchParameters(@Context final UriInfo uriInfo,
            @QueryParam("searchConditions") final String searchConditions, @QueryParam("officeId") final Long officeId,
            @QueryParam("staffId") final Long staffId, @QueryParam("groupId") final Long groupId,
            @QueryParam("centerId") final Long centerId, @QueryParam("paymentTypeId") final Long paymentTypeId) {
        final Map<String, String> searchConditionsMap = FinfluxStringUtils.convertJsonStringToMap(searchConditions);
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final Integer offset = null;
        final Integer limit = null;
        final String orderBy = null;
        final String sortOrder = null;
        final SearchParameters searchParameters = SearchParameters.forTask(searchConditionsMap, officeId, staffId, centerId, groupId,
                offset, limit, orderBy, sortOrder, paymentTypeId);
        final Collection<LoanAccountData> loanAccountData = this.loanReadPlatformService
                .retrieveAllForTaskLookupBySearchParameters(searchParameters);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, loanAccountData);
    }

    @GET
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoan(@PathParam("loanId") final Long loanId,
            @DefaultValue("false") @QueryParam("staffInSelectedOfficeOnly") final boolean staffInSelectedOfficeOnly,
            @DefaultValue("false") @QueryParam("isFetchSpecificData") final boolean isFetchSpecificData,
            @DefaultValue("false") @QueryParam("fetchRDAccountOnly") final boolean fetchRDAccountOnly, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final Set<String> mandatoryResponseParameters = new HashSet<>();
        final Set<String> associationParameters = ApiParameterHelper.extractAssociationsForResponseIfProvided(uriInfo.getQueryParameters());
        final MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        LoanAccountData loanBasicDetails = null;

        Collection<LoanTransactionData> loanRepayments = null;
        LoanScheduleData repaymentSchedule = null;
        Collection<LoanChargeData> charges = null;
        Collection<GuarantorData> guarantors = null;
        Collection<CollateralData> collateral = null;
        CalendarData meeting = null;
        Collection<NoteData> notes = null;
        PortfolioAccountData linkedAccount = null;
        Collection<DisbursementData> disbursementData = null;
        Collection<LoanTermVariationsData> emiAmountVariations = null;
        Collection<InterestRatePeriodData> interestRatesPeriods = null;
        Collection<ChargeData> overdueCharges = null;
        Long pledgeId = null;
        /**
         * Template data variables
         */
        Collection<LoanProductData> productOptions = null;
        LoanProductData product = null;
        Collection<EnumOptionData> loanTermFrequencyTypeOptions = null;
        Collection<EnumOptionData> repaymentFrequencyTypeOptions = null;
        Collection<TransactionProcessingStrategyData> repaymentStrategyOptions = null;
        Collection<EnumOptionData> interestRateFrequencyTypeOptions = null;
        Collection<EnumOptionData> amortizationTypeOptions = null;
        Collection<EnumOptionData> interestTypeOptions = null;
        Collection<EnumOptionData> interestCalculationPeriodTypeOptions = null;
        Collection<FundData> fundOptions = null;
        Collection<StaffData> allowedLoanOfficers = null;
        Collection<ChargeData> chargeOptions = null;
        ChargeData chargeTemplate = null;
        Collection<LoanPurposeData> loanPurposeOptions = null;
        Collection<CodeValueData> loanCollateralOptions = null;
        Collection<CalendarData> calendarOptions = null;
        Collection<PortfolioAccountData> accountLinkingOptions = null;
        PaidInAdvanceData paidInAdvanceTemplate = null;
        Collection<PledgeData> loanProductCollateralPledgesOptions = null;
        Collection<EnumOptionData> repaymentFrequencyNthDayTypeOptions = null;
        Collection<EnumOptionData> repaymentFrequencyDayOfWeekTypeOptions = null;
        Collection<LoanAccountSummaryData> clientActiveLoanOptions = null;
        Collection<EnumOptionData> brokenPeriodMethodTypeOptions = null;

        if (!associationParameters.isEmpty()) {
            if (ApiParameterHelper.isExcludeAssociationsForResponseContains(queryParams, "loanBasicDetails")) {
                loanBasicDetails = LoanAccountData.initializeWithId(loanId);
            } else {
                loanBasicDetails = this.loanReadPlatformService.retrieveOne(loanId);
            }
        } else {
            loanBasicDetails = this.loanReadPlatformService.retrieveOne(loanId);
        }

        if (!isFetchSpecificData) {
            if (loanBasicDetails.isInterestRecalculationEnabled()) {
                final Collection<CalendarData> interestRecalculationCalendarDatas = this.calendarReadPlatformService
                        .retrieveCalendarsByEntity(loanBasicDetails.getInterestRecalculationDetailId(),
                                CalendarEntityType.LOAN_RECALCULATION_REST_DETAIL.getValue(), null);
                CalendarData calendarData = null;
                if (!CollectionUtils.isEmpty(interestRecalculationCalendarDatas)) {
                    calendarData = interestRecalculationCalendarDatas.iterator().next();
                }

                final Collection<CalendarData> interestRecalculationCompoundingCalendarDatas = this.calendarReadPlatformService
                        .retrieveCalendarsByEntity(loanBasicDetails.getInterestRecalculationDetailId(),
                                CalendarEntityType.LOAN_RECALCULATION_COMPOUNDING_DETAIL.getValue(), null);
                CalendarData compoundingCalendarData = null;
                if (!CollectionUtils.isEmpty(interestRecalculationCompoundingCalendarDatas)) {
                    compoundingCalendarData = interestRecalculationCompoundingCalendarDatas.iterator().next();
                }
                loanBasicDetails = LoanAccountData.withInterestRecalculationCalendarData(loanBasicDetails, calendarData,
                        compoundingCalendarData);
            }
        }

        if (!associationParameters.isEmpty()) {

            if (associationParameters.contains("all")) {
                associationParameters.addAll(
                        Arrays.asList("repaymentSchedule", "futureSchedule", "originalSchedule", "transactions", "charges", "guarantors",
                                "collateral", "notes", "linkedAccount", "multiDisburseDetails", "interestRatesPeriods", "meeting"));
            }

            ApiParameterHelper.excludeAssociationsForResponseIfProvided(queryParams, associationParameters);

            if (associationParameters.contains("guarantors")) {
                mandatoryResponseParameters.add("guarantors");
                guarantors = this.guarantorReadPlatformService.retrieveGuarantorsForLoan(loanId);
                if (CollectionUtils.isEmpty(guarantors)) {
                    guarantors = null;
                }
            }

            if (associationParameters.contains("transactions")) {
                mandatoryResponseParameters.add("transactions");
                final Collection<LoanTransactionData> currentLoanRepayments = this.loanReadPlatformService.retrieveLoanTransactions(loanId);
                if (!CollectionUtils.isEmpty(currentLoanRepayments)) {
                    loanRepayments = currentLoanRepayments;
                }
            }

            if (associationParameters.contains("hierarchyLookup")) {
                if (loanBasicDetails.clientId() == null) {
                    final GroupGeneralData data = this.groupReadPlatformService
                            .retrieveCenterDetailsWithGroup(loanBasicDetails.groupData());
                    loanBasicDetails = LoanAccountData.populateGroupDetails(loanBasicDetails, data);
                } else {
                    ClientData clientData = ClientData.formClientData(loanBasicDetails.clientId(), loanBasicDetails.getClientName());
                    clientData = this.clientReadPlatformService.retrieveHierarchyLookupForClient(clientData);

                    loanBasicDetails = LoanAccountData.populateClientDetails(loanBasicDetails, clientData);
                }
            }

            if (associationParameters.contains("multiDisburseDetails") || associationParameters.contains("repaymentSchedule")) {
                mandatoryResponseParameters.add("multiDisburseDetails");
                disbursementData = this.loanReadPlatformService.retrieveLoanDisbursementDetails(loanId);
            }

            if (associationParameters.contains("emiAmountVariations") || associationParameters.contains("repaymentSchedule")) {
                mandatoryResponseParameters.add("emiAmountVariations");
                emiAmountVariations = this.loanReadPlatformService.retrieveLoanTermVariations(loanId,
                        LoanTermVariationType.EMI_AMOUNT.getValue());
            }

            if (associationParameters.contains("repaymentSchedule")) {
                mandatoryResponseParameters.add("repaymentSchedule");
                final RepaymentScheduleRelatedLoanData repaymentScheduleRelatedData = loanBasicDetails.repaymentScheduleRelatedData();
                repaymentSchedule = this.loanReadPlatformService.retrieveRepaymentSchedule(loanId, repaymentScheduleRelatedData,
                        disbursementData, loanBasicDetails.getTotalPaidFeeCharges());
                if (loanBasicDetails.isInterestRecalculationEnabled() && loanBasicDetails.isActive()) {
                    if (associationParameters.contains("originalSchedule")) {
                        mandatoryResponseParameters.add("originalSchedule");
                        final LoanScheduleData loanScheduleData = this.loanScheduleHistoryReadPlatformService
                                .retrieveRepaymentArchiveSchedule(loanId, repaymentScheduleRelatedData, disbursementData);
                        loanBasicDetails = LoanAccountData.withOriginalSchedule(loanBasicDetails, loanScheduleData);
                    }
                }
            }

            if (associationParameters.contains("futureSchedule")) {
                if (loanBasicDetails.isInterestRecalculationEnabled() && loanBasicDetails.isActive()) {
                    final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.loanRepaymentScheduleTransactionProcessorFactory
                            .determineProcessor(
                                    new LoanTransactionProcessingStrategy(loanBasicDetails.getTransactionProcessingStrategyCode()));
                    if (loanRepaymentScheduleTransactionProcessor.isInterestFirstRepaymentScheduleTransactionProcessor()) {
                        if (repaymentSchedule == null) {
                            repaymentSchedule = new LoanScheduleData();
                        }
                        mandatoryResponseParameters.add("futureSchedule");
                        this.calculationPlatformService.updateFutureSchedule(repaymentSchedule, loanId);
                    }

                }
            }

            if (associationParameters.contains("charges")) {
                mandatoryResponseParameters.add("charges");
                charges = this.loanChargeReadPlatformService.retrieveLoanCharges(loanId);
                if (CollectionUtils.isEmpty(charges)) {
                    charges = null;
                }
                overdueCharges = this.chargeReadPlatformService.retrieveLoanProductCharges(loanBasicDetails.loanProductId(),
                        ChargeTimeType.OVERDUE_INSTALLMENT);
            }

            if (associationParameters.contains("collateral")) {
                mandatoryResponseParameters.add("collateral");
                collateral = this.loanCollateralReadPlatformService.retrieveCollaterals(loanId);
                if (CollectionUtils.isEmpty(collateral)) {
                    collateral = null;
                }
            }

            if (associationParameters.contains("meeting")) {
                mandatoryResponseParameters.add("meeting");
                meeting = this.calendarReadPlatformService.retrieveCalendarByEntityIdAndEntityType(loanId,
                        CalendarEntityType.LOANS.getValue());
            }

            if (associationParameters.contains("notes")) {
                mandatoryResponseParameters.add("notes");
                notes = this.noteReadPlatformService.retrieveNotesByResource(loanId, NoteType.LOAN.getValue());
                if (CollectionUtils.isEmpty(notes)) {
                    notes = null;
                }
            }

            if (associationParameters.contains("linkedAccount")) {
                mandatoryResponseParameters.add("linkedAccount");
                linkedAccount = this.accountAssociationsReadPlatformService.retriveLoanLinkedAssociation(loanId);
            }

            if (associationParameters.contains("interestRatesPeriods")) {
                mandatoryResponseParameters.add("interestRatesPeriods");
                interestRatesPeriods = this.loanReadPlatformService.retrieveLoanInterestRatePeriodData(loanBasicDetails);
            }

            if (associationParameters.contains("loanApplicationReferenceId")) {
                mandatoryResponseParameters.add("loanApplicationReferenceId");
                final Long loanApplicationReferenceId = this.loanReadPlatformService.retrieveLoanApplicationReferenceId(loanBasicDetails);
                loanBasicDetails.setLoanApplicationReferenceId(loanApplicationReferenceId);
            }
        }
        Collection<Float> interestRatesListPerPeriod = null;
        if (!isFetchSpecificData) {

            pledgeId = this.pledgeReadPlatformService.retrievePledgesByloanId(loanId);

            final boolean template = ApiParameterHelper.template(uriInfo.getQueryParameters());
            if (template) {
                product = this.loanProductReadPlatformService.retrieveLoanProduct(loanBasicDetails.loanProductId());
                loanBasicDetails.setProduct(product);
                final Integer loancounter = this.loanReadPlatformService.retriveLoanCounter(loanBasicDetails.clientId(), product.getId());
                if (product.useBorrowerCycle() && loancounter > 0) {

                    final Collection<LoanProductBorrowerCycleVariationData> interestForVariationsForBorrowerCycle = product
                            .getInterestRateVariationsForBorrowerCycle();
                    interestRatesListPerPeriod = LoanAccountData.fetchLoanCycleInteresetRates(interestForVariationsForBorrowerCycle,
                            loancounter);
                } else {
                    interestRatesListPerPeriod = product.getInterestRatesListPerPeriod();
                }
                Integer entityType = null;
                Long entityId = null;
                if (AccountType.INDIVIDUAL.getValue().equals(loanBasicDetails.getLoanType().getId().intValue())
                        || AccountType.JLG.getValue().equals(loanBasicDetails.getLoanType().getId().intValue())) {
                    entityType = EntityType.CLIENT.getValue();
                    entityId = loanBasicDetails.clientId();
                }
                productOptions = this.loanProductReadPlatformService
                        .retrieveAllLoanProductsForLookup(product.getApplicableForLoanType().getId().intValue(), entityType, entityId);
                loanTermFrequencyTypeOptions = this.dropdownReadPlatformService.retrieveLoanTermFrequencyTypeOptions();
                repaymentFrequencyTypeOptions = this.dropdownReadPlatformService.retrieveRepaymentFrequencyTypeOptions();
                interestRateFrequencyTypeOptions = this.dropdownReadPlatformService.retrieveInterestRateFrequencyTypeOptions();
                loanProductCollateralPledgesOptions = this.pledgeReadPlatformService
                        .retrievePledgesByClientIdAndProductId(loanBasicDetails.clientId(), loanBasicDetails.loanProductId(), loanId);
                repaymentFrequencyNthDayTypeOptions = this.dropdownReadPlatformService.retrieveRepaymentFrequencyOptionsForNthDayOfMonth();
                repaymentFrequencyDayOfWeekTypeOptions = this.dropdownReadPlatformService.retrieveRepaymentFrequencyOptionsForDaysOfWeek();
                brokenPeriodMethodTypeOptions = this.dropdownReadPlatformService.retrieveBrokenPeriodMethodTypeOptions();

                amortizationTypeOptions = this.dropdownReadPlatformService.retrieveLoanAmortizationTypeOptions();
                if (product.isLinkedToFloatingInterestRates()) {
                    interestTypeOptions = Arrays.asList(interestType(InterestMethod.DECLINING_BALANCE));
                } else {
                    interestTypeOptions = this.dropdownReadPlatformService.retrieveLoanInterestTypeOptions();
                }
                interestCalculationPeriodTypeOptions = this.dropdownReadPlatformService.retrieveLoanInterestRateCalculatedInPeriodOptions();

                fundOptions = this.fundReadPlatformService.retrieveAllFunds(FundApiConstants.activeParamName);
                repaymentStrategyOptions = this.dropdownReadPlatformService.retreiveTransactionProcessingStrategies();
                if (product.getMultiDisburseLoan()) {
                    chargeOptions = this.chargeReadPlatformService.retrieveLoanAccountApplicableCharges(loanId,
                            new ChargeTimeType[] { ChargeTimeType.OVERDUE_INSTALLMENT });
                } else {
                    chargeOptions = this.chargeReadPlatformService.retrieveLoanAccountApplicableCharges(loanId,
                            new ChargeTimeType[] { ChargeTimeType.OVERDUE_INSTALLMENT, ChargeTimeType.TRANCHE_DISBURSEMENT });
                }
                chargeTemplate = this.loanChargeReadPlatformService.retrieveLoanChargeTemplate();

                allowedLoanOfficers = this.loanReadPlatformService.retrieveAllowedLoanOfficers(loanBasicDetails.officeId(),
                        staffInSelectedOfficeOnly);

                loanPurposeOptions = this.loanPurposeGroupReadPlatformService.retrieveAllLoanPurposes(null, null, true);
                loanCollateralOptions = this.codeValueReadPlatformService.retrieveCodeValuesByCode("LoanCollateral");
                final CurrencyData currencyData = loanBasicDetails.currency();
                String currencyCode = null;
                if (currencyData != null) {
                    currencyCode = currencyData.code();
                }
                final long[] accountStatus = { SavingsAccountStatusType.ACTIVE.getValue() };
                final Set<Integer> depositAccountTypes = new HashSet<>();
                if (!fetchRDAccountOnly) {
                    depositAccountTypes.add(DepositAccountType.SAVINGS_DEPOSIT.getValue());
                }
                depositAccountTypes.add(DepositAccountType.RECURRING_DEPOSIT.getValue());
                final PortfolioAccountDTO portfolioAccountDTO = new PortfolioAccountDTO(PortfolioAccountType.SAVINGS.getValue(),
                        loanBasicDetails.clientId(), currencyCode, accountStatus, depositAccountTypes);
                accountLinkingOptions = this.portfolioAccountReadPlatformService.retrieveAllForLookup(portfolioAccountDTO);

                if (!associationParameters.contains("linkedAccount")) {
                    mandatoryResponseParameters.add("linkedAccount");
                    linkedAccount = this.accountAssociationsReadPlatformService.retriveLoanLinkedAssociation(loanId);
                }
                if (loanBasicDetails.groupId() != null) {
                    calendarOptions = this.loanReadPlatformService.retrieveCalendars(loanBasicDetails.groupId());
                }

                if (loanBasicDetails.product().canUseForTopup() && loanBasicDetails.clientId() != null) {
                    clientActiveLoanOptions = this.accountDetailsReadPlatformService
                            .retrieveClientActiveLoanAccountSummary(loanBasicDetails.clientId());
                }
                final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
                loanBasicDetails.setPaymentOptions(paymentOptions);
            }

            paidInAdvanceTemplate = this.loanReadPlatformService.retrieveTotalPaidInAdvance(loanId);
        }

        final LoanAccountData loanAccount = LoanAccountData.associationsAndTemplate(loanBasicDetails, repaymentSchedule, loanRepayments,
                charges, collateral, guarantors, meeting, productOptions, loanTermFrequencyTypeOptions, repaymentFrequencyTypeOptions,
                repaymentFrequencyNthDayTypeOptions, repaymentFrequencyDayOfWeekTypeOptions, repaymentStrategyOptions,
                interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions, interestCalculationPeriodTypeOptions,
                fundOptions, chargeOptions, chargeTemplate, allowedLoanOfficers, loanPurposeOptions, loanCollateralOptions, calendarOptions,
                notes, accountLinkingOptions, linkedAccount, disbursementData, emiAmountVariations, overdueCharges, paidInAdvanceTemplate,
                loanProductCollateralPledgesOptions, pledgeId, interestRatesPeriods, clientActiveLoanOptions, brokenPeriodMethodTypeOptions,
                interestRatesListPerPeriod);
        loanAccount.setLoanApplicationReferenceId(loanBasicDetails.getLoanApplicationReferenceId());

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters(),
                mandatoryResponseParameters);

        return this.toApiJsonSerializer.serialize(settings, loanAccount, this.LOAN_DATA_PARAMETERS);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("searchConditions") final String searchConditions,
            @DefaultValue("false") @QueryParam("lookup") final Boolean lookup, @QueryParam("externalId") final String externalId,
            @QueryParam("officeId") final Long officeId, @QueryParam("offset") final Integer offset,
            @QueryParam("limit") final Integer limit, @QueryParam("orderBy") final String orderBy,
            @QueryParam("sortOrder") final String sortOrder, @QueryParam("accountNo") final String accountNo) {
        final Map<String, String> searchConditionsMap = FinfluxStringUtils.convertJsonStringToMap(searchConditions);
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final SearchParameters searchParameters = SearchParameters.forLoans(searchConditionsMap, externalId, offset, limit, orderBy,
                sortOrder, accountNo, officeId);
        final Page<LoanAccountData> loanBasicDetails = this.loanReadPlatformService.retrieveAll(searchParameters, lookup);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, loanBasicDetails, this.LOAN_DATA_PARAMETERS);
    }

    @GET
    @Path("{loanId}/schedulepreview")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoanSchedulePreview(@PathParam("loanId") final Long loanId, @Context final UriInfo uriInfo) {
        final LoanScheduleModel loanSchedule = this.calculationPlatformService.generateLoanScheduleWithCurrentStatus(loanId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.loanScheduleToApiJsonSerializer.serialize(settings, loanSchedule.toData(), new HashSet<String>());
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String calculateLoanScheduleOrSubmitLoanApplication(@QueryParam("command") final String commandParam,
            @Context final UriInfo uriInfo, @DefaultValue("true") @QueryParam("includePastTranches") final boolean includePastTranches,
            final String apiRequestBodyAsJson) {

        if (is(commandParam, "calculateLoanSchedule")) {

            final JsonElement parsedQuery = this.fromJsonHelper.parse(apiRequestBodyAsJson);
            final JsonQuery query = JsonQuery.from(apiRequestBodyAsJson, parsedQuery, this.fromJsonHelper);

            final LoanScheduleModel loanSchedule = this.calculationPlatformService.calculateLoanSchedule(query, true, includePastTranches);

            final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
            return this.loanScheduleToApiJsonSerializer.serialize(settings, loanSchedule.toData(), new HashSet<String>());
        }

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanApplication().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String modifyLoanApplication(@PathParam("loanId") final Long loanId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateLoanApplication(loanId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteLoanApplication(@PathParam("loanId") final Long loanId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteLoanApplication(loanId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String stateTransitions(@PathParam("loanId") final Long loanId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandProcessingResult result = null;

        if (is(commandParam, "reject")) {
            final CommandWrapper commandRequest = builder.rejectLoanApplication(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "withdrawnByApplicant")) {
            final CommandWrapper commandRequest = builder.withdrawLoanApplication(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "approve")) {
            final CommandWrapper commandRequest = builder.approveLoanApplication(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "disburse")) {
            final CommandWrapper commandRequest = builder.disburseLoanApplication(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "disburseToSavings")) {
            final CommandWrapper commandRequest = builder.disburseLoanToSavingsApplication(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        if (is(commandParam, "undoapproval")) {
            final CommandWrapper commandRequest = builder.undoLoanApplicationApproval(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "undodisbursal")) {
            final CommandWrapper commandRequest = builder.undoLoanApplicationDisbursal(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "undolastdisbursal")) {
            final CommandWrapper commandRequest = builder.undoLastDisbursalLoanApplication(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        if (is(commandParam, "assignloanofficer")) {
            final CommandWrapper commandRequest = builder.assignLoanOfficer(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "unassignloanofficer")) {
            final CommandWrapper commandRequest = builder.unassignLoanOfficer(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "recoverGuarantees")) {
            final CommandWrapper commandRequest = builder.recoverFromGuarantor(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        if (result == null) { throw new UnrecognizedQueryParamException("command", commandParam); }

        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}