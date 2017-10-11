package com.finflux.pdcm.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonQuery;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanSchedulePeriodData;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.pdcm.constants.ChequeStatus;
import com.finflux.pdcm.constants.ChequeType;
import com.finflux.pdcm.data.PostDatedChequeDetailData;
import com.finflux.pdcm.data.PostDatedChequeDetailMappingData;
import com.finflux.pdcm.data.PostDatedChequeDetailSearchTemplateData;
import com.finflux.pdcm.data.PostDatedChequeDetailService;
import com.finflux.pdcm.data.PostDatedChequeDetailTemplateData;

@Service
public class PostDatedChequeDetailReadPlatformServiceImpl implements PostDatedChequeDetailReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    private final PostDatedChequeDetailDataMapper dataMapper;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final PostDatedChequeDetailService service;
    private final LoanReadPlatformService loanReadPlatformService;

    @Autowired
    public PostDatedChequeDetailReadPlatformServiceImpl(final RoutingDataSource dataSource,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService, final OfficeReadPlatformService officeReadPlatformService,
            final PostDatedChequeDetailService service, final LoanReadPlatformService loanReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
        this.dataMapper = new PostDatedChequeDetailDataMapper();
        this.officeReadPlatformService = officeReadPlatformService;
        this.service = service;
        this.loanReadPlatformService = loanReadPlatformService;
    }

    @Override
    public PostDatedChequeDetailTemplateData template(final Integer entityTypeId, final Long entityId) {
        final Collection<EnumOptionData> pdcTypeOptions = ChequeType.options();
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        final EntityType entityType = EntityType.fromInt(entityTypeId);
        Collection<LoanSchedulePeriodData> loanSchedulePeriods = null;
        if (entityType.isLoan()) {
            final boolean excludeLoanScheduleMappedToPDC = true;
            loanSchedulePeriods = this.loanReadPlatformService.lookUpLoanSchedulePeriodsByPeriodNumberAndDueDateAndDueAmounts(entityId,
                    excludeLoanScheduleMappedToPDC);
        }
        return PostDatedChequeDetailTemplateData.template(pdcTypeOptions, paymentOptions, loanSchedulePeriods);
    }

    @Override
    public Collection<PostDatedChequeDetailData> retrieveAll(final Integer entityTypeId, final Long entityId) {
        final String sql = "select " + this.dataMapper.schema() + " where pdcm.entity_type = ? and pdcm.entity_id = ? ";
        return this.jdbcTemplate.query(sql, this.dataMapper, new Object[] { entityTypeId, entityId });
    }

    private static final class PostDatedChequeDetailDataMapper implements RowMapper<PostDatedChequeDetailData> {

        private final String schema;

        public PostDatedChequeDetailDataMapper() {
            final StringBuilder sb = new StringBuilder(500);
            sb.append("pdc.id as id,pdc.bank_name as bankName,pdc.branch_name as branchName,pdc.account_number as accountNumber ");
            sb.append(",pdc.ifsc_code as ifscCode,pdc.cheque_amount as chequeAmount,pdc.cheque_type as chequeType ");
            sb.append(",pdc.cheque_number as chequeNumber,pdc.cheque_date as chequeDate,pdc.present_status as presentStatus ");
            sb.append(",pdc.previous_status as previousStatus,pdc.presented_date as presentedDate,pdc.bounced_date as bouncedDate ");
            sb.append(",pdc.cleared_date as clearedDate,pdc.cancelled_date as cancelledDate,pdc.returned_date as returnedDate ");
            sb.append(",pdcm.id as pdcMappingId,pdcm.payment_type as paymentType,pdcm.entity_type as entityTypeId,pdcm.entity_id as entityId,pdcm.due_amount as dueAmount,pdcm.due_date as dueDate,pdcm.paid_status as paidStatus ");
            sb.append(",l.id as loanId, l.account_no as loanAccountNumber ,l.loan_status_id as loanStatusId ");
            sb.append("from f_pdc_cheque_detail pdc ");
            sb.append("join f_pdc_cheque_detail_mapping pdcm on pdcm.pdc_cheque_detail_id = pdc.id and pdcm.is_deleted = 0 ");
            sb.append("join m_loan l on l.id = pdcm.entity_id ");
            this.schema = sb.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public PostDatedChequeDetailData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String bankName = rs.getString("bankName");
            final String branchName = rs.getString("branchName");
            final String accountNumber = rs.getString("accountNumber");
            final String ifscCode = rs.getString("ifscCode");
            final BigDecimal chequeAmount = rs.getBigDecimal("chequeAmount");
            final Integer chequeTypeId = JdbcSupport.getIntegeActualValue(rs, "chequeType");
            final EnumOptionData chequeType = ChequeType.type(chequeTypeId);
            final String chequeNumber = rs.getString("chequeNumber");
            final LocalDate chequeDate = JdbcSupport.getLocalDate(rs, "chequeDate");
            final Integer presentStatusId = JdbcSupport.getIntegeActualValue(rs, "presentStatus");
            final EnumOptionData presentStatus = ChequeStatus.type(presentStatusId);
            final Integer previousStatusId = JdbcSupport.getIntegeActualValue(rs, "previousStatus");
            EnumOptionData previousStatus = null;
            if (previousStatusId != null) {
                previousStatus = ChequeStatus.type(previousStatusId);
            }
            final LocalDate presentedDate = JdbcSupport.getLocalDate(rs, "presentedDate");
            final LocalDate bouncedDate = JdbcSupport.getLocalDate(rs, "bouncedDate");
            final LocalDate clearedDate = JdbcSupport.getLocalDate(rs, "clearedDate");
            final LocalDate cancelledDate = JdbcSupport.getLocalDate(rs, "cancelledDate");
            final LocalDate returnedDate = JdbcSupport.getLocalDate(rs, "returnedDate");

            final Long pdcMappingId = rs.getLong("pdcMappingId");
            final Integer paymentType = JdbcSupport.getIntegeActualValue(rs, "paymentType");
            final Integer entityTypeId = JdbcSupport.getIntegeActualValue(rs, "entityTypeId");
            final EnumOptionData entityType = EntityType.type(entityTypeId);
            final Long entityId = rs.getLong("entityId");
            final BigDecimal dueAmount = rs.getBigDecimal("dueAmount");
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
            final boolean paidStatus = rs.getBoolean("paidStatus");
            
            final String loanProductName = null;
            final Long loanId = rs.getLong("loanId");
            final String loanAccountNumber = rs.getString("loanAccountNumber");
            final Integer loanStatusId = JdbcSupport.getIntegeActualValue(rs, "loanStatusId");
            final LoanStatusEnumData status = LoanEnumerations.status(loanStatusId);
            final LoanAccountData loanAccountData = LoanAccountData.loanDetailsForPDCLookUp(loanId, loanAccountNumber, status,
                    loanProductName);

            final PostDatedChequeDetailData postDatedChequeDetailData = PostDatedChequeDetailData.instance(id, bankName, branchName,
                    accountNumber, ifscCode, chequeAmount, chequeType, chequeNumber, chequeDate, presentStatus, previousStatus,
                    presentedDate, bouncedDate, clearedDate, cancelledDate, returnedDate, loanAccountData);
            
            final PostDatedChequeDetailMappingData postDatedChequeDetailMappingData = PostDatedChequeDetailMappingData.instance(
                    pdcMappingId, paymentType, entityType, entityId, dueAmount, dueDate, paidStatus);
            postDatedChequeDetailData.setMappingData(postDatedChequeDetailMappingData);

            return postDatedChequeDetailData;
        }
    }

    @Override
    public PostDatedChequeDetailData retrieveOne(final Long pdcId) {
        try {
            final String sql = "select " + this.dataMapper.schema() + " where pdc.id = ? ";
            return this.jdbcTemplate.queryForObject(sql, this.dataMapper, new Object[] { pdcId });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public PostDatedChequeDetailSearchTemplateData searchTemplate() {
        final Collection<OfficeData> officeOptions = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
        final Collection<EnumOptionData> pdcTypeOptions = ChequeType.options();
        final Collection<EnumOptionData> chequeStatusOptions = ChequeStatus.options();
        return PostDatedChequeDetailSearchTemplateData.template(officeOptions, pdcTypeOptions, chequeStatusOptions);
    }

    @Override
    public Collection<PostDatedChequeDetailData> searchPDC(final JsonQuery query) {
        final PDCSearchParameters searchParameters = this.service.validateAndBuildPDCSearchParameters(query.json());
        final ChequeStatus chequeStatus = ChequeStatus.fromInt(searchParameters.getChequeStatus());
        final PostDatedChequeDetailSearchDataMapper searchDataMapper = new PostDatedChequeDetailSearchDataMapper();
        String sql = "select " + searchDataMapper.schema(chequeStatus);
        final List<Object> params = new ArrayList<>();
        final String whereClauseConditions = buildSQLwhereCondition(searchParameters, params);
        if (StringUtils.isNotBlank(whereClauseConditions)) {
            sql = sql + " where " + whereClauseConditions;
        }
        final String groupByOrOrderByConditions = buildSQLGroupOrOrderByCondition(chequeStatus);
        if (StringUtils.isNotBlank(groupByOrOrderByConditions)) {
            sql = sql + groupByOrOrderByConditions;
        }
        return this.jdbcTemplate.query(sql, params.toArray(), searchDataMapper);
    }

    private String buildSQLGroupOrOrderByCondition(final ChequeStatus chequeStatus) {
        final StringBuilder buff = new StringBuilder();
        buff.append(" order by ");
        if (chequeStatus.isPresented() || chequeStatus.isBounced()) {
            buff.append(" l.id desc, lt.transaction_date desc, lt.created_date desc, pdcm.due_date desc, pdc.cheque_date desc ");
        } else {
            buff.append(" l.id, pdcm.due_date, pdc.cheque_date ");
        }
        return buff.toString();
    }

    private String buildSQLwhereCondition(final PDCSearchParameters searchParameters, final List<Object> params) {
        final StringBuilder buff = new StringBuilder();
        final Date fromDate = searchParameters.getFromDate();
        final Date toDate = searchParameters.getToDate();
        if (searchParameters.getOfficeId() != null) {
            buff.append("o.id = ? ");
            params.add(searchParameters.getOfficeId());
        }
        if (searchParameters.getChequeType() != null) {
            if (params.size() > 0) buff.append("and ");
            buff.append("pdc.cheque_type = ? ");
            params.add(searchParameters.getChequeType());
        }
        if (searchParameters.getChequeStatus() != null) {
            if (params.size() > 0) buff.append("and ");
            buff.append("pdc.present_status = ? ");
            params.add(searchParameters.getChequeStatus());
            final ChequeStatus chequeStatus = ChequeStatus.fromInt(searchParameters.getChequeStatus());
            if (chequeStatus.isPending()) {
                if (fromDate != null) {
                    if (params.size() > 0) buff.append("and ");
                    buff.append("pdc.cheque_date >= ? ");
                    params.add(fromDate);
                }
                if (toDate != null) {
                    if (params.size() > 0) buff.append("and ");
                    buff.append("pdc.cheque_date <= ? ");
                    params.add(toDate);
                }
            } else if (chequeStatus.isPresented()) {
                if (fromDate != null) {
                    if (params.size() > 0) buff.append("and ");
                    buff.append("pdc.presented_date >= ? ");
                    params.add(fromDate);
                }
                if (toDate != null) {
                    if (params.size() > 0) buff.append("and ");
                    buff.append("pdc.presented_date <= ? ");
                    params.add(toDate);
                }
            } else if (chequeStatus.isBounced()) {
                if (fromDate != null) {
                    if (params.size() > 0) buff.append("and ");
                    buff.append("pdc.bounced_date >= ? ");
                    params.add(fromDate);
                }
                if (toDate != null) {
                    if (params.size() > 0) buff.append("and ");
                    buff.append("pdc.bounced_date <= ? ");
                    params.add(toDate);
                }
            } else if (chequeStatus.isCleared()) {
                if (fromDate != null) {
                    if (params.size() > 0) buff.append("and ");
                    buff.append("pdc.cleared_date >= ? ");
                    params.add(fromDate);
                }
                if (toDate != null) {
                    if (params.size() > 0) buff.append("and ");
                    buff.append("pdc.cleared_date <= ? ");
                    params.add(toDate);
                }
            } else if (chequeStatus.isReturned()) {
                if (fromDate != null) {
                    if (params.size() > 0) buff.append("and ");
                    buff.append("pdc.returned_date >= ? ");
                    params.add(fromDate);
                }
                if (toDate != null) {
                    if (params.size() > 0) buff.append("and ");
                    buff.append("pdc.returned_date <= ? ");
                    params.add(toDate);
                }
            }
        }
        return buff.toString();
    }

    private static final class PostDatedChequeDetailSearchDataMapper implements RowMapper<PostDatedChequeDetailData> {

        private final String schema;

        public PostDatedChequeDetailSearchDataMapper() {
            final StringBuilder sb = new StringBuilder(500);
            sb.append("o.name as officeName,c.display_name as clientName,lp.name as loanProductName ");
            sb.append(",l.id as loanId, l.account_no as loanAccountNumber ,l.loan_status_id as loanStatusId ");
            sb.append(",pdc.id as id,pdc.bank_name as bankName,pdc.branch_name as branchName,pdc.account_number as accountNumber ");
            sb.append(",pdc.ifsc_code as ifscCode,pdc.cheque_amount as chequeAmount,pdc.cheque_type as chequeType ");
            sb.append(",pdc.cheque_number as chequeNumber,pdc.cheque_date as chequeDate,pdc.present_status as presentStatus ");
            sb.append(",pdc.previous_status as previousStatus,pdc.presented_date as presentedDate,pdc.bounced_date as bouncedDate ");
            sb.append(",pdc.cleared_date as clearedDate,pdc.cancelled_date as cancelledDate,pdc.returned_date as returnedDate ");
            sb.append(",pdcm.id as pdcMappingId,pdcm.payment_type as paymentType,pdcm.entity_type as entityTypeId,pdcm.entity_id as entityId,pdcm.due_amount as dueAmount,pdcm.due_date as dueDate,pdcm.paid_status as paidStatus ");
            sb.append("from f_pdc_cheque_detail pdc ");
            sb.append("join f_pdc_cheque_detail_mapping pdcm on pdcm.pdc_cheque_detail_id = pdc.id and pdcm.is_deleted = 0 ");
            sb.append("and pdcm.entity_type = ").append(EntityType.LOAN.getValue());
            sb.append(" join m_loan l on l.id = pdcm.entity_id and l.loan_status_id >= ").append(LoanStatus.ACTIVE.getValue());
            sb.append(" join m_product_loan lp on lp.id = l.product_id ");
            sb.append("join m_client c on c.id = l.client_id ");
            sb.append("join m_office o on o.id = c.office_id ");
            this.schema = sb.toString();
        }

        public String schema(final ChequeStatus chequeStatus) {
            if (chequeStatus.isPresented() || chequeStatus.isBounced()) {
                final StringBuilder sb = new StringBuilder(this.schema.length() + 100);
                sb.append(this.schema);
                sb.append(" left join m_loan_transaction lt on lt.loan_id = l.id and pdcm.transaction_id = lt.id ");
                return sb.toString();
            }
            return this.schema;
        }

        @Override
        public PostDatedChequeDetailData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String bankName = rs.getString("bankName");
            final String branchName = rs.getString("branchName");
            final String accountNumber = rs.getString("accountNumber");
            final String ifscCode = rs.getString("ifscCode");
            final BigDecimal chequeAmount = rs.getBigDecimal("chequeAmount");
            final Integer chequeTypeId = JdbcSupport.getIntegeActualValue(rs, "chequeType");
            final EnumOptionData chequeType = ChequeType.type(chequeTypeId);
            final String chequeNumber = rs.getString("chequeNumber");
            final LocalDate chequeDate = JdbcSupport.getLocalDate(rs, "chequeDate");
            final Integer presentStatusId = JdbcSupport.getIntegeActualValue(rs, "presentStatus");
            final EnumOptionData presentStatus = ChequeStatus.type(presentStatusId);
            final Integer previousStatusId = JdbcSupport.getIntegeActualValue(rs, "previousStatus");
            EnumOptionData previousStatus = null;
            if (previousStatusId != null) {
                previousStatus = ChequeStatus.type(previousStatusId);
            }
            final LocalDate presentedDate = JdbcSupport.getLocalDate(rs, "presentedDate");
            final LocalDate bouncedDate = JdbcSupport.getLocalDate(rs, "bouncedDate");
            final LocalDate clearedDate = JdbcSupport.getLocalDate(rs, "clearedDate");
            final LocalDate cancelledDate = JdbcSupport.getLocalDate(rs, "cancelledDate");
            final LocalDate returnedDate = JdbcSupport.getLocalDate(rs, "returnedDate");

            final Long pdcMappingId = rs.getLong("pdcMappingId");
            final Integer paymentType = JdbcSupport.getIntegeActualValue(rs, "paymentType");
            final Integer entityTypeId = JdbcSupport.getIntegeActualValue(rs, "entityTypeId");
            final EnumOptionData entityType = EntityType.type(entityTypeId);
            final Long entityId = rs.getLong("entityId");

            final BigDecimal dueAmount = rs.getBigDecimal("dueAmount");
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
            final boolean paidStatus = rs.getBoolean("paidStatus");
            final String officeName = rs.getString("officeName");
            final String clientName = rs.getString("clientName");
            final String loanProductName = rs.getString("loanProductName");
            final Long loanId = rs.getLong("loanId");
            final String loanAccountNumber = rs.getString("loanAccountNumber");
            final Integer loanStatusId = JdbcSupport.getIntegeActualValue(rs, "loanStatusId");
            final LoanStatusEnumData status = LoanEnumerations.status(loanStatusId);
            final LoanAccountData loanAccountData = LoanAccountData.loanDetailsForPDCLookUp(loanId, loanAccountNumber, status,
                    loanProductName);

            final PostDatedChequeDetailData postDatedChequeDetailData = PostDatedChequeDetailData.searchDataInstance(id, bankName,
                    branchName, accountNumber, ifscCode, chequeAmount, chequeType, chequeNumber, chequeDate, presentStatus, previousStatus,
                    presentedDate, bouncedDate, clearedDate, cancelledDate, returnedDate, officeName, clientName, loanAccountData);
            final PostDatedChequeDetailMappingData postDatedChequeDetailMappingData = PostDatedChequeDetailMappingData.instance(
                    pdcMappingId, paymentType, entityType, entityId, dueAmount, dueDate, paidStatus);
            postDatedChequeDetailData.setMappingData(postDatedChequeDetailMappingData);

            return postDatedChequeDetailData;
        }
    }

}