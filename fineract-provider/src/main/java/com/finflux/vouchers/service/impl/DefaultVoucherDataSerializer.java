package com.finflux.vouchers.service.impl;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.accounting.closure.domain.GLClosure;
import org.apache.fineract.accounting.closure.domain.GLClosureRepository;
import org.apache.fineract.accounting.common.AccountingConstants.FINANCIAL_ACTIVITY;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccount;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccountRepositoryWrapper;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryDetail;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryType;
import org.apache.fineract.accounting.journalentry.exception.JournalEntryInvalidException;
import org.apache.fineract.accounting.journalentry.exception.JournalEntryInvalidException.GL_JOURNAL_ENTRY_INVALID_REASON;
import org.apache.fineract.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.apache.fineract.infrastructure.configuration.domain.GlobalConfigurationRepositoryWrapper;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;

import com.finflux.vouchers.constants.VoucherType;
import com.finflux.vouchers.constants.VouchersApiConstants;
import com.finflux.vouchers.domain.Voucher;
import com.finflux.vouchers.domain.VoucherRepositoryWrapper;
import com.finflux.vouchers.exception.InvalidVoucherRequestParametersException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public abstract class DefaultVoucherDataSerializer {

    protected static final Set<String> supportedParameters = new HashSet<>(Arrays.asList(VouchersApiConstants.officeId_ParamName,
            VouchersApiConstants.currencyCode_ParamName, VouchersApiConstants.narration_ParamName,
            VouchersApiConstants.transactionDate_ParamName, VouchersApiConstants.debitAccounts_ParamName,
            VouchersApiConstants.creditAccounts_ParamName, VouchersApiConstants.glAccountId_ParamName,
            VouchersApiConstants.amount_ParamName, VouchersApiConstants.locale_ParamName, VouchersApiConstants.dateFormat_ParamName));

    protected static final Set<String> interbranch_supportedParameters = new HashSet<>(Arrays.asList(
            VouchersApiConstants.fromOfficeId_ParamName, VouchersApiConstants.toOfficeId_ParamName,
            VouchersApiConstants.currencyCode_ParamName, VouchersApiConstants.narration_ParamName,
            VouchersApiConstants.transactionDate_ParamName, VouchersApiConstants.debitAccounts_ParamName,
            VouchersApiConstants.creditAccounts_ParamName, VouchersApiConstants.glAccountId_ParamName,
            VouchersApiConstants.amount_ParamName, VouchersApiConstants.locale_ParamName, VouchersApiConstants.dateFormat_ParamName));

    protected static final Set<String> supportedParameters_withPaymentDetails = new HashSet<>(Arrays.asList(
            VouchersApiConstants.officeId_ParamName, VouchersApiConstants.currencyCode_ParamName, VouchersApiConstants.narration_ParamName,
            VouchersApiConstants.transactionDate_ParamName, VouchersApiConstants.debitAccounts_ParamName,
            VouchersApiConstants.creditAccounts_ParamName, VouchersApiConstants.glAccountId_ParamName,
            VouchersApiConstants.amount_ParamName, VouchersApiConstants.locale_ParamName, VouchersApiConstants.dateFormat_ParamName,
            VouchersApiConstants.paymentDetails_paramName, VouchersApiConstants.paymentType_ParamName,
            VouchersApiConstants.instrumentionNumber_ParamName, VouchersApiConstants.paymentDate_ParamName,
            VouchersApiConstants.bankName_ParamName, VouchersApiConstants.branchName_paramName));

    protected static final Set<String> interbranch_supportedParameters_withPaymentDetails = new HashSet<>(
            Arrays.asList(VouchersApiConstants.fromOfficeId_ParamName, VouchersApiConstants.toOfficeId_ParamName,
                    VouchersApiConstants.currencyCode_ParamName, VouchersApiConstants.narration_ParamName,
                    VouchersApiConstants.transactionDate_ParamName, VouchersApiConstants.debitAccounts_ParamName,
                    VouchersApiConstants.creditAccounts_ParamName, VouchersApiConstants.glAccountId_ParamName,
                    VouchersApiConstants.amount_ParamName, VouchersApiConstants.locale_ParamName, VouchersApiConstants.dateFormat_ParamName,
                    VouchersApiConstants.paymentDetails_paramName, VouchersApiConstants.paymentType_ParamName,
                    VouchersApiConstants.instrumentionNumber_ParamName, VouchersApiConstants.paymentDate_ParamName,
                    VouchersApiConstants.bankName_ParamName, VouchersApiConstants.branchName_paramName));

    protected static final Set<String> paymentdetails_supportedParameters = new HashSet<>(Arrays.asList(
            VouchersApiConstants.paymentType_ParamName, VouchersApiConstants.instrumentionNumber_ParamName,
            VouchersApiConstants.paymentDate_ParamName, VouchersApiConstants.bankName_ParamName, VouchersApiConstants.branchName_paramName,
            VouchersApiConstants.locale_ParamName, VouchersApiConstants.dateFormat_ParamName));

    private final FromJsonHelper fromApiJsonHelper;
    private final GLAccountRepositoryWrapper glAccountRepositoryWrapper;
    private final PlatformSecurityContext context;
    private final VoucherRepositoryWrapper voucherRepository;
    private final PaymentTypeRepositoryWrapper paymentTyperepositoryWrapper;
    private final GLClosureRepository glClosureRepository;
    private final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepository;
    private final OfficeRepositoryWrapper officeRepositoryWrapper;
    private final GlobalConfigurationRepositoryWrapper globalConfigurationRepositoryWrapper ;
    
    public DefaultVoucherDataSerializer(final FromJsonHelper fromApiJsonHelper, final GLAccountRepositoryWrapper glAccountRepositoryWrapper,
            final PlatformSecurityContext context, final VoucherRepositoryWrapper voucherRepository,
            final PaymentTypeRepositoryWrapper paymentTyperepositoryWrapper, final GLClosureRepository glClosureRepository,
            final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepository,
            final OfficeRepositoryWrapper officeRepositoryWrapper, 
            final GlobalConfigurationRepositoryWrapper globalConfigurationRepositoryWrapper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.glAccountRepositoryWrapper = glAccountRepositoryWrapper;
        this.context = context;
        this.voucherRepository = voucherRepository;
        this.paymentTyperepositoryWrapper = paymentTyperepositoryWrapper;
        this.glClosureRepository = glClosureRepository;
        this.financialActivityAccountRepository = financialActivityAccountRepository;
        this.officeRepositoryWrapper = officeRepositoryWrapper;
        this.globalConfigurationRepositoryWrapper = globalConfigurationRepositoryWrapper ;
    }

    protected JournalEntry retrieveJournalEntry(final String json, final Set<String> paramersSupported) {
        boolean notNullCheckRequired = true;
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, paramersSupported);
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("voucher");
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final Long officeId = retrieveLongNamed(VouchersApiConstants.officeId_ParamName, element, baseDataValidator, notNullCheckRequired);
        final String currencyCode = retrieveStringNamed(VouchersApiConstants.currencyCode_ParamName, element, baseDataValidator,
                notNullCheckRequired);
        final String narration = retrieveStringNamed(VouchersApiConstants.narration_ParamName, element, baseDataValidator, false);
        final LocalDate transactionDate = retrieveDateNamed(VouchersApiConstants.transactionDate_ParamName, element, baseDataValidator,
                notNullCheckRequired);
        List<JournalEntryDetail> debitAccountsJsonArray = retrieveAccounts(VouchersApiConstants.debitAccounts_ParamName, element,
                JournalEntryType.DEBIT, baseDataValidator);
        List<JournalEntryDetail> creditAccountsJsonArray = retrieveAccounts(VouchersApiConstants.creditAccounts_ParamName, element,
                JournalEntryType.CREDIT, baseDataValidator);
        validateDebitAndCreditAccounts(debitAccountsJsonArray, creditAccountsJsonArray);
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
        this.officeRepositoryWrapper.findOneWithNotFoundDetection(officeId);
        validateAccountingClosure(transactionDate, officeId);

        final Long paymentDetailId = null;
        final String transactionIdentifier = generateTransactionId(officeId);
        final boolean manualEntry = true;
        final Date valueDate = transactionDate.toDate();
        final Date effectiveDate = transactionDate.toDate();
        final Integer entityType = null;
        final Long entityId = null;
        final String referenceNumber = null;
        final Long entityTransactionId = null;
        JournalEntry entry = JournalEntry.createNew(officeId, paymentDetailId, currencyCode, transactionIdentifier, manualEntry,
                transactionDate.toDate(), valueDate, effectiveDate, narration, entityType, entityId, referenceNumber, entityTransactionId);
        entry.addAllJournalEntryDetail(debitAccountsJsonArray);
        entry.addAllJournalEntryDetail(creditAccountsJsonArray);
        return entry;
    }

    protected List<JournalEntry> retrieveFromJournalEnties(final String json, final Set<String> paramersSupported) {
        List<JournalEntry> journalEnries = new ArrayList<>();
        boolean notNullCheckRequired = true;
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, paramersSupported);
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("voucher");
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        // Currency code, transaction date and narration are common to from and
        // to journal entries
        final String currencyCode = retrieveStringNamed(VouchersApiConstants.currencyCode_ParamName, element, baseDataValidator,
                notNullCheckRequired);
        final LocalDate transactionDate = retrieveDateNamed(VouchersApiConstants.transactionDate_ParamName, element, baseDataValidator,
                notNullCheckRequired);
        final String narration = retrieveStringNamed(VouchersApiConstants.narration_ParamName, element, baseDataValidator, false);

        // From Office Id
        final Long fromOfficeId = retrieveLongNamed(VouchersApiConstants.fromOfficeId_ParamName, element, baseDataValidator,
                notNullCheckRequired);
        this.officeRepositoryWrapper.findOneWithNotFoundDetection(fromOfficeId);
        // To Office Id
        final Long toOfficeId = retrieveLongNamed(VouchersApiConstants.toOfficeId_ParamName, element, baseDataValidator,
                notNullCheckRequired);
        this.officeRepositoryWrapper.findOneWithNotFoundDetection(toOfficeId);

        // From Office Credit accounts
        List<JournalEntryDetail> creditAccountsJsonArray = retrieveAccounts(VouchersApiConstants.creditAccounts_ParamName, element,
                JournalEntryType.CREDIT, baseDataValidator);

        // To Office Debit accounts
        List<JournalEntryDetail> debitAccountsJsonArray = retrieveAccounts(VouchersApiConstants.creditAccounts_ParamName, element,
                JournalEntryType.DEBIT, baseDataValidator);

        validateDebitAndCreditAccounts(debitAccountsJsonArray, creditAccountsJsonArray);
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

        if(fromOfficeId.equals(toOfficeId)) {
            String errorMessage = "From officeId "+fromOfficeId + " toOfficeId "+ toOfficeId+" can not be same" ;
            throw new InvalidVoucherRequestParametersException("error.msg.vouchers.fromOffice.toOffice.cannot.be.same", errorMessage) ;
        }
        
        validateAccountingClosure(transactionDate, fromOfficeId);
        validateAccountingClosure(transactionDate, toOfficeId);

        final Long paymentDetailId = null;
        final boolean manualEntry = true;
        final Date valueDate = transactionDate.toDate();
        final Date effectiveDate = transactionDate.toDate();
        final Integer entityType = null;
        final Long entityId = null;
        final String referenceNumber = null;
        final Long entityTransactionId = null;

        FinancialActivityAccount financialActivityAccount = this.financialActivityAccountRepository
                .findByFinancialActivityTypeWithNotFoundDetection(FINANCIAL_ACTIVITY.INTERBRANCH_TRANSFER.getValue());

        // From Journal Entry
        final String fromTransactionIdentifier = generateTransactionId(fromOfficeId);
        JournalEntryDetail debitAccounts = new JournalEntryDetail(financialActivityAccount.getGlAccount(),
                JournalEntryType.DEBIT.getValue(), getAmount(debitAccountsJsonArray));
        List<JournalEntryDetail> fromDebitAccounts = new ArrayList<>();
        fromDebitAccounts.add(debitAccounts);
        JournalEntry fromJournalEntry = JournalEntry.createNew(fromOfficeId, paymentDetailId, currencyCode, fromTransactionIdentifier,
                manualEntry, transactionDate.toDate(), valueDate, effectiveDate, narration, entityType, entityId, referenceNumber,
                entityTransactionId);
        fromJournalEntry.addAllJournalEntryDetail(fromDebitAccounts);
        fromJournalEntry.addAllJournalEntryDetail(creditAccountsJsonArray);

        // To Journal Entry
        final String toTransactionIdentifier = generateTransactionId(toOfficeId);
        JournalEntryDetail creditEntries = new JournalEntryDetail(financialActivityAccount.getGlAccount(),
                JournalEntryType.CREDIT.getValue(), getAmount(creditAccountsJsonArray));
        List<JournalEntryDetail> toJournalDebits = new ArrayList<>();
        toJournalDebits.add(creditEntries);
        JournalEntry toJournalEntry = JournalEntry.createNew(toOfficeId, paymentDetailId, currencyCode, toTransactionIdentifier,
                manualEntry, transactionDate.toDate(), valueDate, effectiveDate, narration, entityType, entityId, referenceNumber,
                entityTransactionId);
        toJournalEntry.addAllJournalEntryDetail(debitAccountsJsonArray);
        toJournalEntry.addAllJournalEntryDetail(toJournalDebits);

        // Finally adding these two Journal Entries to the list
        journalEnries.add(fromJournalEntry);
        journalEnries.add(toJournalEntry);

        return journalEnries;
    }

    private BigDecimal getAmount(final List<JournalEntryDetail> journalEntryDetails) {
        BigDecimal amount = BigDecimal.ZERO;
        for (JournalEntryDetail detail : journalEntryDetails) {
            amount = amount.add(detail.getAmount());
        }
        return amount;
    }

    public Map<String, Object> updateVoucher(final Voucher voucher, final PaymentDetail paymentDetails, final String json) {
        boolean notNullCheckRequired = true;
        Map<String, Object> actualChanges = new HashMap<>();
        VoucherType voucherType = VoucherType.fromInt(voucher.getVoucherType());
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, paymentdetails_supportedParameters);
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource(voucherType.getCode());
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.parameterExists(VouchersApiConstants.paymentType_ParamName, element)) {
            final Long paymentTypeId = retrieveLongNamed(VouchersApiConstants.paymentType_ParamName, element, baseDataValidator,
                    notNullCheckRequired);
            final PaymentType paymentType = this.paymentTyperepositoryWrapper.findOneWithNotFoundDetection(paymentTypeId);
            if (paymentDetails.setPaymentType(paymentType)) {
                actualChanges.put(VouchersApiConstants.paymentType_ParamName, paymentTypeId);
            }
        }
        if (this.fromApiJsonHelper.parameterExists(VouchersApiConstants.instrumentionNumber_ParamName, element)) {
            final String instrumentationNumber = retrieveStringNamed(VouchersApiConstants.instrumentionNumber_ParamName, element,
                    baseDataValidator, notNullCheckRequired);
            if (paymentDetails.setCheckNumber(instrumentationNumber)) {
                actualChanges.put(VouchersApiConstants.instrumentionNumber_ParamName, instrumentationNumber);
            }

        }
        if (this.fromApiJsonHelper.parameterExists(VouchersApiConstants.paymentDate_ParamName, element)) {
            final LocalDate paymentLocalDate = retrieveDateNamed(VouchersApiConstants.paymentDate_ParamName, element, baseDataValidator,
                    notNullCheckRequired);
            if (paymentLocalDate != null && paymentDetails.setPaymentDate(paymentLocalDate.toDate())) {
                actualChanges.put(VouchersApiConstants.paymentDate_ParamName, paymentLocalDate);
            }

        }
        if (this.fromApiJsonHelper.parameterExists(VouchersApiConstants.bankName_ParamName, element)) {
            final String bankName = retrieveStringNamed(VouchersApiConstants.bankName_ParamName, element, baseDataValidator,
                    notNullCheckRequired);
            if(paymentDetails.setBankNumber(bankName)) {
                actualChanges.put(VouchersApiConstants.bankName_ParamName, bankName);    
            }
        }
        if (this.fromApiJsonHelper.parameterExists(VouchersApiConstants.branchName_paramName, element)) {
            final String branchName = retrieveStringNamed(VouchersApiConstants.branchName_paramName, element, baseDataValidator,
                    notNullCheckRequired);
            if(paymentDetails.setBranchName(branchName)) {
                actualChanges.put(VouchersApiConstants.branchName_paramName, branchName);    
            }
        }
        return actualChanges;
    }

    protected PaymentDetail retrievePaymentDetails(final String json) {
        PaymentDetail paymentDetails = null;
        boolean notNullCheckRequired = true;
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("voucher");
        final JsonObject parentObject = this.fromApiJsonHelper.parse(json).getAsJsonObject();
        final JsonElement element = parentObject.get(VouchersApiConstants.paymentDetails_paramName);
        final Long paymentTypeId = retrieveLongNamed(VouchersApiConstants.paymentType_ParamName, element, baseDataValidator,
                notNullCheckRequired);
        final PaymentType paymentType = this.paymentTyperepositoryWrapper.findOneWithNotFoundDetection(paymentTypeId);
        final String instrumentationNumber = retrieveStringNamed(VouchersApiConstants.instrumentionNumber_ParamName, element,
                baseDataValidator, notNullCheckRequired);
        final LocalDate paymentLocalDate = retrieveDateNamed(VouchersApiConstants.paymentDate_ParamName, element, baseDataValidator,
                notNullCheckRequired);
        final String bankName = retrieveStringNamed(VouchersApiConstants.bankName_ParamName, element, baseDataValidator,
                notNullCheckRequired);
        final String branchName = retrieveStringNamed(VouchersApiConstants.branchName_paramName, element, baseDataValidator,
                notNullCheckRequired);
        Date paymentDate = null;
        if (paymentLocalDate != null) paymentDate = paymentLocalDate.toDate();
        paymentDetails = PaymentDetail.instance(paymentType, instrumentationNumber, bankName, branchName, paymentDate);
        return paymentDetails;
    }

    protected Long retrieveLongNamed(final String paramName, final JsonElement element, final DataValidatorBuilder baseDataValidator,
            boolean notNullCheckRequired) {
        final Long officeId = this.fromApiJsonHelper.extractLongNamed(paramName, element);
        if (notNullCheckRequired) {
            baseDataValidator.reset().parameter(paramName).value(officeId).notNull();
        }
        return officeId;
    }

    protected String retrieveStringNamed(final String paramName, final JsonElement element, final DataValidatorBuilder baseDataValidator,
            boolean notNullCheckRequired) {
        final String currencyCode = this.fromApiJsonHelper.extractStringNamed(paramName, element);
        if (notNullCheckRequired) {
            baseDataValidator.reset().parameter(paramName).value(currencyCode).notNull();
        }
        return currencyCode;
    }

    protected LocalDate retrieveDateNamed(final String paramName, final JsonElement element, final DataValidatorBuilder baseDataValidator,
            boolean notNullCheckRequired) {
        final LocalDate date = this.fromApiJsonHelper.extractLocalDateNamed(paramName, element);
        if (notNullCheckRequired) {
            baseDataValidator.reset().parameter(paramName).value(date).notNull();
        }
        return date;
    }

    protected List<JournalEntryDetail> retrieveAccounts(final String paramName, final JsonElement element,
            final JournalEntryType entryType, final DataValidatorBuilder baseDataValidator) {
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final List<JournalEntryDetail> accounts = new ArrayList<>();
        final JsonArray accountsJsonArray = this.fromApiJsonHelper.extractJsonArrayNamed(paramName, element);
        for (final JsonElement object : accountsJsonArray) {
            final Long glAccountId = this.fromApiJsonHelper.extractLongNamed(VouchersApiConstants.glAccountId_ParamName, object);
            final GLAccount glAccount = this.glAccountRepositoryWrapper.findOneWithNotFoundDetection(glAccountId);
            final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalNamed(VouchersApiConstants.amount_ParamName,
                    object, locale);
            baseDataValidator.reset().parameter(VouchersApiConstants.amount_ParamName).value(transactionAmount).notNull().positiveAmount();
            accounts.add(JournalEntryDetail.createNew(glAccount, entryType, transactionAmount));
        }
        return accounts;
    }

    protected String generateTransactionId(final Long officeId) {
        final AppUser user = this.context.authenticatedUser();
        final Long time = System.currentTimeMillis();
        final String uniqueVal = String.valueOf(time) + user.getId() + officeId;
        final String transactionId = Long.toHexString(Long.parseLong(uniqueVal));
        return transactionId;
    }

    protected String generateVoucherNumber(final Integer voucherType, final JournalEntry entry, boolean increment) {
        final String finYear = getCurrentFinancialYear(entry.getTransactionDate()) ;
        Integer count = this.voucherRepository.retrieveVouchersCount(voucherType, finYear) + 1;
        if (increment) count = count + 1;
        return getVoucherPrefixKey() + "/" + entry.getOfficeId().toString() + "/" + finYear + "/" + count;
    }

    protected String getCurrentFinancialYear(final Date transactionDate) {
        int  financialYearStart = getFinancialYearStart() ; 
        Calendar cal = Calendar.getInstance();
        cal.setTime(transactionDate);
        int year = cal.get(Calendar.YEAR);
        Integer currentMonth = cal.get(Calendar.MONTH)+1 ; //Calendar month value for January is 0
        StringBuilder builder = new StringBuilder() ;
        if(financialYearStart == 1) {
            builder.append(year) ;
        }else {
            if(currentMonth >= financialYearStart) {
                builder.append(year) ;
                builder.append("-") ;
                builder.append(new Integer(year+1).toString().substring(2)) ;
            }else {
                builder.append(year-1) ;
                builder.append("-") ;
                builder.append(new Integer(year).toString().substring(2)) ;
            }    
        }
        return builder.toString() ;
    }
    
    protected int getFinancialYearStart() {
        int financialYearStart = 1; //By default January 
        GlobalConfigurationProperty financialYearConfiguration = this.globalConfigurationRepositoryWrapper
                .findOneByNameWithNotFoundDetection("financial-year-beginning-month");
        if (financialYearConfiguration.isEnabled()) {
            String value = financialYearConfiguration.getValue();
            try {
                financialYearStart = Integer.parseInt(value);
            } catch (NumberFormatException e) {}
        }
        return financialYearStart;
    }
    
    protected void validateAccountingClosure(final LocalDate transactionDate, final Long officeId) {
        final GLClosure latestGLClosure = this.glClosureRepository.getLatestGLClosureByBranch(officeId);
        if (latestGLClosure != null) {
            if (latestGLClosure.getClosingDate().after(transactionDate.toDate())
                    || latestGLClosure.getClosingDate().equals(transactionDate.toDate())) { throw new JournalEntryInvalidException(
                            GL_JOURNAL_ENTRY_INVALID_REASON.ACCOUNTING_CLOSED, latestGLClosure.getClosingDate(), null, null); }
        }
    }

    protected void validateDebitAndCreditAccounts(final List<JournalEntryDetail> debits, final List<JournalEntryDetail> credits) {
        if (isListEmpty(debits)
                || isListEmpty(credits)) { throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.NO_DEBITS_OR_CREDITS,
                        null, null, null); }

        BigDecimal debitAmount = BigDecimal.ZERO;
        for (JournalEntryDetail transaction : debits) {
            debitAmount = debitAmount.add(transaction.getAmount());
        }

        BigDecimal creditAmount = BigDecimal.ZERO;
        for (JournalEntryDetail transaction : credits) {
            creditAmount = creditAmount.add(transaction.getAmount());
        }

        if (!debitAmount.equals(creditAmount)) { throw new JournalEntryInvalidException(
                GL_JOURNAL_ENTRY_INVALID_REASON.DEBIT_CREDIT_SUM_MISMATCH, null, null, null); }
    }

    protected boolean isListEmpty(final List<JournalEntryDetail> list) {
        return (list == null || list.size() == 0) ? true : false;
    }

    protected BigDecimal getJournalEntryAmount(final JournalEntry entry) {
        BigDecimal amount = BigDecimal.ZERO;
        List<JournalEntryDetail> list = entry.getJournalEntryDetails();
        for (JournalEntryDetail detail : list) {
            if (detail.getType().equals(JournalEntryType.DEBIT.getValue())) {
                amount = amount.add(detail.getAmount());
            }
        }
        return amount;
    }

    public abstract List<Voucher> validateAndCreateVouchers(final String json);

    protected abstract String getVoucherPrefixKey();
}
