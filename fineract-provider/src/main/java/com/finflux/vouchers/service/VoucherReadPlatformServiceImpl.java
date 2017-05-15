package com.finflux.vouchers.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.common.AccountingEnumerations;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.journalentry.data.JournalEntryData;
import org.apache.fineract.accounting.journalentry.data.JournalEntryDetailData;
import org.apache.fineract.accounting.journalentry.data.TransactionDetailData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.service.CurrencyReadPlatformService;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.vouchers.constants.VoucherType;
import com.finflux.vouchers.data.VoucherData;
import com.finflux.vouchers.data.VoucherTemplateData;
import com.finflux.vouchers.exception.InvalidVoucherTypeException;

@Service
public class VoucherReadPlatformServiceImpl implements VoucherReadPlatformService {

    private final OfficeReadPlatformService officeReadPlatformService;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final PaginationHelper<VoucherData> paginationHelper = new PaginationHelper<>();
    private final JdbcTemplate jdbcTemplate;
    private final VoucherServiceFactory vouherServiceFactory ;
    
    @Autowired
    public VoucherReadPlatformServiceImpl(final OfficeReadPlatformService officeReadPlatformService, final RoutingDataSource dataSource,
            final CurrencyReadPlatformService currencyReadPlatformService,
            final VoucherServiceFactory vouherServiceFactory) {
        this.officeReadPlatformService = officeReadPlatformService;
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.vouherServiceFactory = vouherServiceFactory ;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public VoucherData retrieveVoucheTemplate(final String voucherTypeCode) {
        Collection<OfficeData> officeOptions = null;
        Collection<CurrencyData> currencyOptions = null;
        List<GLAccountData> debitAccountingOptions = null;
        List<GLAccountData> creditAccountingOptions = null;
        Collection<EnumOptionData> voucherTypeOptions = null;
        Collection<PaymentTypeData> paymentOptions = null;
        if (voucherTypeCode != null) {
            officeOptions = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
            currencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrencies();
            VoucherService service = this.vouherServiceFactory.findVoucherService(voucherTypeCode) ;
            if(service == null) throw new InvalidVoucherTypeException(voucherTypeCode) ;
            VoucherTemplateData template = service.getTemplate() ;
            debitAccountingOptions = template.getDebitAccountingOptions() ;
            creditAccountingOptions = template.getCreditAccountingOptions() ;
            paymentOptions = template.getPaymentOptions() ;
        } else {
            voucherTypeOptions = VoucherType.voucherTypeOptions();
        }
        VoucherTemplateData templateData = VoucherTemplateData.template(officeOptions, currencyOptions, debitAccountingOptions,
                creditAccountingOptions, voucherTypeOptions, paymentOptions);
        return VoucherData.template(templateData);
    }

    @SuppressWarnings("unused")
    @Override
    public VoucherData retrieveOne(String voucherType, Long voucherId) {
        VoucherMapper mapper = new VoucherMapper();
        String sqlString = "select " + mapper.schema() + " where voucher.id = ?";
        VoucherData voucherData = this.jdbcTemplate.queryForObject(sqlString, mapper, new Object[] { voucherId });
        JournalEntryDetailsMapper detailsMapper = new JournalEntryDetailsMapper();
        String detailsQuery = "select " + detailsMapper.schema() + " where journalEntryDetail.journal_entry_id = ?";
        List<JournalEntryDetailData> detailedEntries = new ArrayList<>() ;
        detailedEntries.addAll(this.jdbcTemplate.query(detailsQuery, detailsMapper, new Object[] { voucherData
                .getJournalEntryData().getId() }));
        if(voucherData.isReversed()) {
            detailsQuery = "select " + detailsMapper.schema() + " where journalEntryDetail.journal_entry_id = ?";
            detailedEntries.addAll(this.jdbcTemplate.query(detailsQuery, detailsMapper, new Object[] { voucherData
                    .getJournalEntryData().getReversedJournalEntryId()})) ;
        }
        voucherData.setJournalEntryDetails(detailedEntries);
        return voucherData;
    }

    class JournalEntryDetailsMapper implements RowMapper<JournalEntryDetailData> {

        private StringBuilder buff = new StringBuilder();

        public JournalEntryDetailsMapper() {
            buff.append(
                    " journalEntryDetail.type_enum as entryType,journalEntryDetail.amount as amount, journalEntryDetail.account_id as glAccountId, ")
                    .append(" account.gl_code as glAccountCode, account.name as glAccountName, account.classification_enum as classification ")
                    .append("from f_journal_entry_detail journalEntryDetail ")
                    .append("LEFT JOIN acc_gl_account account ON account.id = journalEntryDetail.account_id ");
        }

        public String schema() {
            return buff.toString();
        }

        @SuppressWarnings("unused")
        @Override
        public JournalEntryDetailData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final String glCode = rs.getString("glAccountCode");
            final String glAccountName = rs.getString("glAccountName");
            final Long glAccountId = rs.getLong("glAccountId");
            final int accountTypeId = JdbcSupport.getInteger(rs, "classification");
            final EnumOptionData accountType = AccountingEnumerations.gLAccountType(accountTypeId);
            GLAccountData glAccountData = GLAccountData.createFrom(glAccountId, glAccountName, glCode, accountType);
            final BigDecimal amount = rs.getBigDecimal("amount");
            final int entryTypeId = JdbcSupport.getInteger(rs, "entryType");
            final EnumOptionData entryType = AccountingEnumerations.journalEntryType(entryTypeId);
            return new JournalEntryDetailData(glAccountData, amount, entryType);
        }

    }

    class VoucherMapper implements RowMapper<VoucherData> {

        private StringBuilder buff = new StringBuilder();

        VoucherMapper() {
            this.buff
                    .append("voucher.id as voucherId, voucher.voucher_number as voucherNumber, voucher.voucher_type as voucherType, voucher.jentry_id as journalEntryId,")
                    .append("journalEntry.office_id as officeId, office.name as officeName, journalEntry.entry_date as entryDate, journalEntry.value_date as valueDate, ")
                    .append(" journalEntry.effective_date as effectiveDate, journalEntry.reversal_id as reversalId, journalEntry.reversed as isReversed,")
                    .append("journalEntry.manual_entry as isManualEntry, journalEntry.transaction_identifier as transactionIdentifier, journalEntry.payment_details_id as paymentDetailsId, ")
                    .append("journalEntry.description as narration, paymentDetails.payment_type_id as paymentTypeId, paymentDetails.check_number as instrumentationNo, paymentDetails.payment_date as paymentDate, ")
                    .append("paymentDetails.bank_number as bankNo, paymentDetails.branch_name as branchName, paymentType.value as paymentType, currency.code as currencyCode, ")
                    .append("currency.decimal_places as decimalPlaces, currency.currency_multiplesof currencyMultiplesOf, currency.name as currencyName, ")
                    .append("journalEntry.createdby_id as createdbyId,  createdUser.username as createdUserName, journalEntry.created_date as createdDate, ")
                    .append("journalEntry.lastmodifiedby_id as modifiedbyId,  modifiedUser.username as modifiedUserName, journalEntry.lastmodified_date as lastModifiedDate, ")
                    .append("currency.internationalized_name_code as currencyNameCode, currency.display_symbol as currencyDisplaySymbol, ")
                    .append("voucher.amount as voucherAmount ").append("from f_voucher_details voucher ")
                    .append("LEFT JOIN f_journal_entry journalEntry ON journalEntry.id = voucher.jentry_id ")
                    .append("LEFT JOIN m_payment_detail paymentDetails ON paymentDetails.id = journalEntry.payment_details_id ")
                    .append("LEFT JOIN m_payment_type paymentType ON paymentType.id = paymentDetails.payment_type_id ")
                    .append("LEFT JOIn m_currency currency ON currency.code = journalEntry.currency_code ")
                    .append("LEFT JOIN m_office office ON office.id = journalEntry.office_id ")
                    .append("LEFT JOIN m_appuser createdUser ON createdUser.id = journalEntry.createdby_id ")
                    .append("LEFT JOIN m_appuser modifiedUser ON modifiedUser.id = journalEntry.lastmodifiedby_id ");
        }

        public String schema() {
            return this.buff.toString();
        }

        @Override
        public VoucherData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long voucherId = rs.getLong("voucherId");
            final String voucherNumber = rs.getString("voucherNumber");
            final Integer voucherType = rs.getInt("voucherType");
            final BigDecimal voucherAmount = rs.getBigDecimal("voucherAmount");
            final Long journalEntryId = rs.getLong("journalEntryId");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final LocalDate journalEntryDate = JdbcSupport.getLocalDate(rs, "entryDate");
            final LocalDate journalValueDate = JdbcSupport.getLocalDate(rs, "valueDate");
            final LocalDate journalEffectiveDate = JdbcSupport.getLocalDate(rs, "effectiveDate");
            final Long reversalId = rs.getLong("reversalId");
            final Boolean isReversed = rs.getBoolean("isReversed");
            final Boolean isManualEntry = rs.getBoolean("isManualEntry");
            final String transactionIdentifier = rs.getString("transactionIdentifier");
            final String narration = rs.getString("narration");
            final Long paymentDetailsId = rs.getLong("paymentDetailsId");
            final Long paymentTypeId = rs.getLong("paymentTypeId");
            final String paymentTypeValue = rs.getString("paymentType");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "decimalPlaces");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "currencyMultiplesOf");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);
            PaymentDetailData paymentDetailData = null;
            if (paymentDetailsId != null && paymentDetailsId > 0) {
                final PaymentTypeData paymentType = PaymentTypeData.instance(paymentTypeId, paymentTypeValue);
                final String instrumentationNo = rs.getString("instrumentationNo");
                final String bankNo = rs.getString("bankNo");
                final String branchName = rs.getString("branchName");
                final Date paymentDate = rs.getDate("paymentDate");
                final String routingCode = null;
                final String receiptNumber = null;
                final String accountNumber = null;
                paymentDetailData = new PaymentDetailData(paymentDetailsId, paymentType, accountNumber, instrumentationNo, routingCode,
                        receiptNumber, bankNo, branchName, paymentDate);
            }
            final Long createdByUserId = rs.getLong("createdbyId");
            final String createdByUserName = rs.getString("createdUserName");
            final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");
            EnumOptionData entityType = null;
            final Long entityId = null;
            final Long entityTransactionId = null;
            final String referenceNumber = null;
            Collection<JournalEntryDetailData> journalEntryDetails = new ArrayList<>();
            TransactionDetailData transactionDetailData = new TransactionDetailData(null, paymentDetailData, null, null);
            JournalEntryData journalEntryData = new JournalEntryData(journalEntryId, officeId, officeName, journalEntryDate,
                    journalValueDate, journalEffectiveDate, transactionIdentifier, isManualEntry, entityType, entityId,
                    entityTransactionId, createdByUserId, createdDate, createdByUserName, narration, isReversed, referenceNumber,
                    transactionDetailData, currency, journalEntryDetails, reversalId);
            return VoucherData.instance(voucherId, voucherNumber, VoucherType.voucherType(voucherType), journalEntryData, voucherAmount);
        }

    }

    @Override
    public Page<VoucherData> retrieveVouchers(SearchParameters searchParams) {
        VoucherMapper mapper = new VoucherMapper();
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(mapper.schema());
        List<Object> params = new ArrayList<>();
        String whereconditions = buildSQLwhereCondition(searchParams, params);
        if (StringUtils.isNotBlank(whereconditions)) {
            sqlBuilder.append(" where (").append(whereconditions).append(")");
        }
        if (searchParams.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParams.getLimit());
            if (searchParams.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParams.getOffset());
            }
        }
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        Object[] params1 = new Object[params.size()];
        params.toArray(params1);
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), params1, mapper);
    }

    private String buildSQLwhereCondition(final SearchParameters searchParams, final List<Object> params) {
        final Long officeId = searchParams.getOfficeId();
        final String voucherType = searchParams.getVoucherType();
        final String voucherNumber = searchParams.getVoucherNumber();
        final Date fromDate = searchParams.getStartDate();
        final Date endDate = searchParams.getEndDate();
        StringBuilder buff = new StringBuilder();
        if (officeId != null) {
            buff.append("journalEntry.office_id = ? ");
            params.add(officeId);
        }

        if (voucherType != null) {
            if (params.size() > 0) buff.append("and ");
            buff.append("voucher.voucher_type = ? ");
            params.add(VoucherType.fromCode(voucherType).getValue());
        }

        if (voucherNumber != null) {
            if (params.size() > 0) buff.append("and ");
            buff.append("voucher.voucher_number = ? ");
            params.add(voucherNumber);
        }

        if (fromDate != null) {
            if (params.size() > 0) buff.append("and ");
            buff.append("journalEntry.entry_date >= ? ");
            params.add(fromDate);
        }

        if (endDate != null) {
            if (params.size() > 0) buff.append("and ");
            buff.append("journalEntry.entry_date <= ? ");
            params.add(endDate);
        }
        return buff.toString();
    }
}