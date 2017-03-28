package com.finflux.mandates.service;

import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.domain.MandateProcessStatusEnum;
import com.finflux.mandates.domain.MandateProcessTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Service
public class MandatesProcessingReadPlatformServiceImpl implements MandatesProcessingReadPlatformService{

        private final JdbcTemplate jdbcTemplate;
        private final PlatformSecurityContext context;
        private final OfficeReadPlatformService officeReadPlatformService;
        private final TransactionsProcessingReadPlatformService transactionsProcessingReadPlatformService;
        final SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy-MM-dd");

        @Autowired
        public MandatesProcessingReadPlatformServiceImpl(final PlatformSecurityContext context,
                final RoutingDataSource dataSource,
                final OfficeReadPlatformService officeReadPlatformService,
                final TransactionsProcessingReadPlatformService transactionsProcessingReadPlatformService){

                this.context = context;
                this.jdbcTemplate = new JdbcTemplate(dataSource);
                this.officeReadPlatformService = officeReadPlatformService;
                this.transactionsProcessingReadPlatformService = transactionsProcessingReadPlatformService;
        }

        @Override
        public MandatesProcessData retrieveMandateDownloadTemplate() {
                return MandatesProcessData.mandateDownloadTemplate(this.officeReadPlatformService.retrieveAllOfficesForDropdown());
        }

        @Override
        public MandatesProcessData retrieveTransactionsDownloadTemplate() {
                return MandatesProcessData.transactionsDownloadTemplate(this.officeReadPlatformService.retrieveAllOfficesForDropdown(),
                        this.transactionsProcessingReadPlatformService.retrieveRecentFailedTransactions());
        }

        @Override public MandatesProcessData retrieveMandateProcessData(final Long id) {
                try{
                        MandatesProcessDataMapper mapper = new MandatesProcessDataMapper();
                        String sql = "select " + mapper.schema();
                        sql += " where m.id = ? ";
                        return  this.jdbcTemplate.queryForObject(sql, new Object[] {id}, mapper);

                } catch (EmptyResultDataAccessException e){
                        return null;
                }
        }

        @Override
        public Collection<MandatesProcessData> retrieveMandates(final MandateProcessTypeEnum type,
                final Date requestDate, final Long officeId) {
                try{
                        final AppUser user = this.context.authenticatedUser();
                        MandatesProcessDataMapper mapper = new MandatesProcessDataMapper();
                        List<Object> params = new ArrayList<>();

                        String sql = "select " + mapper.schema();
                        final String hierarchy = user.getOffice().getHierarchy();
                        final String hierarchySearchString = hierarchy + "%";
                        sql += " where o.hierarchy like ? ";
                        params.add(hierarchySearchString);

                        if(!type.hasStateOf(MandateProcessTypeEnum.ALL) && !type.hasStateOf(MandateProcessTypeEnum.INVALID)){
                                sql += " and m.process_type = ? ";
                                params.add(type.getValue());
                        }

                        if(null != requestDate){
                                sql += " and m.request_date = ? ";
                                params.add(yyyyMMddFormat.format(requestDate));
                        }

                        if(null != officeId){
                                sql += " and m.office_id = ? ";
                                params.add(officeId);
                        }

                        sql += " order by m.id desc limit 100 ";

                        return this.jdbcTemplate.query(sql, params.toArray(), mapper);
                } catch (EmptyResultDataAccessException e){
                        return new ArrayList<>();
                }
        }

        @Override
        public Collection<MandatesProcessData> retrieveMandatesWithStatus(Integer[] status) {
                try{
                        MandatesProcessDataMapper mapper = new MandatesProcessDataMapper();
                        String sql = "select " + mapper.schema();
                        sql += " where m.process_status in (?)";
                        return  this.jdbcTemplate.query(sql, new Object[] {StringUtils.join(status,',')}, mapper);

                } catch (EmptyResultDataAccessException e){
                        return new ArrayList<>();
                }
        }

        @Override
        public boolean pendingMandateDownloadProcessExists(final Long officeId) {
                StringBuilder sql = new StringBuilder(" select count(*) > 0 ")
                        .append(" from f_mandates_process as m ")
                        .append(" where m.process_type = ? ")
                        .append(" and m.process_status in (?,?) ")
                        .append(" and m.office_id = ? ");
                return this.jdbcTemplate.queryForObject(sql.toString(),
                        new Object[]{MandateProcessTypeEnum.MANDATES_DOWNLOAD.getValue(),
                                MandateProcessStatusEnum.REQUESTED.getValue(),
                                MandateProcessStatusEnum.INPROCESS.getValue(),
                                officeId}, Boolean.class);
        }

        @Override
        public boolean pendingMandateRequestsExists(Long officeId, boolean includeChildOffices) {
                final AppUser user = this.context.authenticatedUser();
                final String hierarchy = user.getOffice().getHierarchy();
                final String hierarchySearchString = hierarchy + "%";

                List<Object> params = new ArrayList<>();
                StringBuilder sql = new StringBuilder(" select count(*) > 0 ")
                        .append(" from f_loan_mandates as m ")
                        .append(" left join m_loan as l on m.loan_id = l.id ")
                        .append(" left join m_client as c on l.client_id = c.id ")
                        .append(" left join m_office as o on c.office_id = o.id ")
                        .append(" where m.mandate_status_enum in (100, 200, 300) ")
                        .append(" and o.hierarchy like ? ");
                params.add(hierarchySearchString);
                if(includeChildOffices) {
                        sql.append(" and o.hierarchy like concat((select off.hierarchy from m_office as off where off.id = ?),'%') ");
                        params.add(officeId);
                }else{
                        sql.append(" and o.id = ? ");
                        params.add(officeId);
                }
                return this.jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Boolean.class);
        }

        @Override
        public boolean pendingTransactionsDownloadProcessExists(Long officeId) {
                StringBuilder sql = new StringBuilder(" select count(*) > 0 ")
                        .append(" from f_mandates_process as m ")
                        .append(" where m.process_type = ? ")
                        .append(" and m.process_status in (?,?) ")
                        .append(" and m.office_id = ? ");
                return this.jdbcTemplate.queryForObject(sql.toString(),
                        new Object[]{MandateProcessTypeEnum.TRANSACTIONS_DOWNLOAD.getValue(),
                                MandateProcessStatusEnum.REQUESTED.getValue(),
                                MandateProcessStatusEnum.INPROCESS.getValue(),
                                officeId}, Boolean.class);
        }

        private static class MandatesProcessDataMapper implements RowMapper<MandatesProcessData> {
                public String schema(){
                        StringBuilder sql = new StringBuilder(" m.*, o.name as office_name, d.name as document_name ")
                                .append(" from f_mandates_process as m ")
                                .append(" left join m_office as o on m.office_id = o.id ")
                                .append(" left join m_document as d on m.document_id = d.id ");
                        return sql.toString();
                }

                @Override
                public MandatesProcessData mapRow(ResultSet rs, int rowNum) throws SQLException {
                        final Long id = JdbcSupport.getLong(rs, "id");
                        final Date requestDate = rs.getDate("request_date");
                        final Integer mandateProcessTypeEnum = rs.getInt("process_type");
                        final String mandateProcessType = MandateProcessTypeEnum.fromInt(mandateProcessTypeEnum).getType();
                        final Integer mandateProcessStatusEnum = rs.getInt("process_status");
                        final String mandateProcessStatus = MandateProcessStatusEnum.fromInt(mandateProcessStatusEnum).getStatus();
                        final Long officeId = JdbcSupport.getLong(rs, "office_id");
                        final String officeName = rs.getString("office_name");
                        final Boolean includeChildOffices = rs.getBoolean("include_child_offices");
                        final Boolean includeMandateScans = rs.getBoolean("include_mandate_scans");
                        final Date paymentDueStartDate = rs.getDate("payment_due_start_date");
                        final Date paymentDueEndDate = rs.getDate("payment_due_end_date");
                        final String includeFailedTransactions = rs.getString("include_failed_transactions");
                        final Long documentId = JdbcSupport.getLong(rs, "document_id");
                        final String documentName = rs.getString("document_name");
                        final String failureReasonCode = rs.getString("failed_reason_code");
                        final String failureReasonDesc = rs.getString("failed_reason_desc");
                        final Integer totalRecords = JdbcSupport.getInteger(rs, "total_records");
                        final Integer successRecords = JdbcSupport.getInteger(rs, "success_records");
                        final Integer failedRecords = JdbcSupport.getInteger(rs, "failed_records");
                        final Integer unprocessedRecords = JdbcSupport.getInteger(rs, "unprocessed_records");

                        return MandatesProcessData.from(id, requestDate, mandateProcessType, mandateProcessStatus, officeId, officeName,
                                includeChildOffices, includeMandateScans, paymentDueStartDate, paymentDueEndDate, includeFailedTransactions,
                                documentId, documentName, failureReasonCode, failureReasonDesc, totalRecords, successRecords, failedRecords,
                                unprocessedRecords);
                }
        }
}
