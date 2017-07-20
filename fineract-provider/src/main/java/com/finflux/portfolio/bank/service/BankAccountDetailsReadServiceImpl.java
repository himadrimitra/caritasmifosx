package com.finflux.portfolio.bank.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.TaskExecutionData;
import com.finflux.task.data.WorkflowDTO;
import com.finflux.task.service.CreateWorkflowTaskFactory;
import com.finflux.task.service.TaskExecutionService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.portfolio.bank.domain.BankAccountDetailEntityType;
import com.finflux.portfolio.bank.domain.BankAccountDetailStatus;
import com.finflux.portfolio.bank.domain.BankAccountType;

@Service
public class BankAccountDetailsReadServiceImpl implements BankAccountDetailsReadService {

    private final JdbcTemplate jdbcTemplate;
    private final ConfigurationDomainService configurationDomainService;
    private final CreateWorkflowTaskFactory createWorkflowTaskFactory;
    private final ClientRepositoryWrapper clientRepository;
    private final TaskExecutionService taskExecutionService;

    @Autowired
    public BankAccountDetailsReadServiceImpl(final RoutingDataSource dataSource,
            ConfigurationDomainService configurationDomainService, CreateWorkflowTaskFactory createWorkflowTaskFactory,
            ClientRepositoryWrapper clientRepository, TaskExecutionService taskExecutionService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.configurationDomainService = configurationDomainService;
        this.createWorkflowTaskFactory = createWorkflowTaskFactory;
        this.clientRepository = clientRepository;
        this.taskExecutionService = taskExecutionService;
    }

    @Override
    public BankAccountDetailData retrieveOne(final Long id) {

        BankAccountDetailMapper mapper = new BankAccountDetailMapper();
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(mapper.schema());
        sb.append(" where bad.id=?");

        return this.jdbcTemplate.queryForObject(sb.toString(), mapper, id);
    }
    
    @Override
    public BankAccountDetailData retrieveOneBy(final BankAccountDetailEntityType entityType, final Long entityId) {

        try {
            BankAccountDetailMapper mapper = new BankAccountDetailMapper();
            StringBuilder sb = new StringBuilder();
            sb.append("select ");
            sb.append(mapper.schema());
            sb.append(" left join f_bank_account_detail_associations bada on bada.bank_account_detail_id = bad.id ");
            sb.append(" where bada.entity_type_enum=? and bada.entity_id = ?");

            return this.jdbcTemplate.queryForObject(sb.toString(), mapper, entityType.getValue(), entityId);
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }

    }
    

    private class BankAccountDetailMapper implements RowMapper<BankAccountDetailData> {

        public String schema() {
            StringBuilder sb = new StringBuilder();
            sb.append(" bad.id as id, bad.name as name, bad.account_number as accountNumber, bad.ifsc_code as ifscCode, ");
            sb.append(" bad.mobile_number as mobileNumber, bad.email as email, bad.status_id as status, ");
            sb.append(" bad.bank_name as bankName, bad.bank_city as bankCity, bad.account_type_enum as accountType, ");
            sb.append(
                    " bad.last_transaction_date as lastTransactionDate, bad.micr_code as micrCode, bad.branch_name as branchName, ");
            sb.append(" bad.document_id as documentId, bad.checker_info as checkerInfo ");
            sb.append(" from f_bank_account_details bad ");
            return sb.toString();
        }

        @Override
        public BankAccountDetailData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String accountNumber = rs.getString("accountNumber");
            final String ifscCode = rs.getString("ifscCode");
            final String email = rs.getString("email");
            final String mobileNumber = rs.getString("mobileNumber");
            final String bankName = rs.getString("bankName");
            final String bankCity = rs.getString("bankCity");
            final Integer type = JdbcSupport.getInteger(rs, "status");
            final EnumOptionData status = BankAccountDetailStatus.bankAccountDetailStatusEnumDate(type);
            final Integer accountTypeVal  = JdbcSupport.getInteger(rs, "accountType");
            final EnumOptionData accountType = BankAccountType.bankAccountType(accountTypeVal);
            final Date lastTransactionDate = rs.getDate("lastTransactionDate");
            final String micrCode = rs.getString("micrCode") ;
            final String branchName = rs.getString("branchName") ;
            final Long documentId = rs.getLong("documentId");
            final String checkerInfo = rs.getString("checkerInfo");
            return new BankAccountDetailData(id, name, accountNumber, ifscCode, mobileNumber, email,bankName,bankCity,
                    status, accountType, lastTransactionDate, micrCode, branchName, documentId, checkerInfo);
        }

    }
    
    @Override
    public Collection<EnumOptionData> bankAccountTypeOptions() {
        final List<EnumOptionData> bankAccountTypeOptions = Arrays.asList(
                BankAccountType.bankAccountType(BankAccountType.SAVINGSACCOUNT),BankAccountType.bankAccountType(BankAccountType.CURRENTACCOUNT));
        return bankAccountTypeOptions;
    }

    @Override
    public TaskExecutionData createOrFetchBankAccountWorkflow(BankAccountDetailEntityType bankEntityType,
            Long entityId) {
        Boolean isClientBankAccountLinkedToWorkflow = false;
        if (this.configurationDomainService.isWorkFlowEnabled()
                && BankAccountDetailEntityType.CLIENTS.getValue().equals(bankEntityType.getValue())) {
            Long clientId = entityId;
            final TaskExecutionData taskExecutionData = this.taskExecutionService
                    .getTaskIdByEntity(TaskEntityType.CLIENT_BANKACCOUNT, clientId);
            if (taskExecutionData != null) {
                BankAccountDetailData data = retrieveOneBy(bankEntityType, entityId);
                if (data != null || taskExecutionData.getStatus().getId() < 7) {
                    return taskExecutionData;
                }
            }
            WorkflowDTO workflowDTO = new WorkflowDTO(clientRepository.findOneWithNotFoundDetection(clientId));
            if (this.createWorkflowTaskFactory.create(TaskConfigEntityType.CLIENTBANKACCOUNT)
                    .createWorkFlow(workflowDTO)) {
                return this.taskExecutionService.getTaskIdByEntity(TaskEntityType.CLIENT_BANKACCOUNT, clientId);
            }
        }
        return null;
    }

}
