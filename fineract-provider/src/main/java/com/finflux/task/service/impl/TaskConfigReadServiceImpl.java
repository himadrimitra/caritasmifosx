package com.finflux.task.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.task.data.TaskConfigData;
import com.finflux.task.data.TaskConfigTemplateData;
import com.finflux.task.data.TaskType;
import com.finflux.task.service.TaskConfigReadService;

@Service
public class TaskConfigReadServiceImpl implements TaskConfigReadService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final OfficeReadPlatformService officeReadPlatformService;

    @Autowired
    public TaskConfigReadServiceImpl(final RoutingDataSource dataSource, final PlatformSecurityContext context,
            final OfficeReadPlatformService officeReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.context = context;
        this.officeReadPlatformService = officeReadPlatformService;
    }

    @Override
    public TaskConfigTemplateData retrieveForLookUp(final Long parentConfigId) {
        this.context.authenticatedUser();
        TaskConfigMapper taskConfigMapper = new TaskConfigMapper();
        Collection<OfficeData> offices = new ArrayList<>();
        Collection<TaskConfigData> taskConfigs = new ArrayList<>();
        String sql = "SELECT " + taskConfigMapper.schema();
        if (null == parentConfigId) {
            offices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
            sql += " WHERE ftc.`parent_id` IS NULL AND ftc.`task_type` = 1 ";
            taskConfigs = this.jdbcTemplate.query(sql, taskConfigMapper);
        } else {
            sql += " WHERE ftc.`parent_id` IS NOT NULL AND ftc.`task_type` = 2 AND ftc.`parent_id` = ? ";
            taskConfigs = this.jdbcTemplate.query(sql, taskConfigMapper, parentConfigId);
        }
        final List<EnumOptionData> loanAccountTypeOptions = Arrays.asList(
                AccountType.loanAccountType(AccountType.INDIVIDUAL.getValue(), AccountType.INDIVIDUAL.getCode()),
                AccountType.loanAccountType(AccountType.JLG.getValue(), AccountType.JLG.getCode()));
        return TaskConfigTemplateData.template(offices, taskConfigs, loanAccountTypeOptions);
    }

    @Override
    public TaskConfigTemplateData retrieveTemplate() {
        this.context.authenticatedUser();
        TaskConfigWithChidrenMapper taskConfigWithChidrenMapper = new TaskConfigWithChidrenMapper();
        Collection<OfficeData> offices = new ArrayList<>();
        Collection<TaskConfigData> taskConfigs = new ArrayList<>();
        String sql = "SELECT " + taskConfigWithChidrenMapper.schema();
        taskConfigs = this.jdbcTemplate.query(sql, taskConfigWithChidrenMapper);
        final List<EnumOptionData> loanAccountTypeOptions = Arrays.asList(
                AccountType.loanAccountType(AccountType.INDIVIDUAL.getValue(), AccountType.INDIVIDUAL.getCode()),
                AccountType.loanAccountType(AccountType.JLG.getValue(), AccountType.JLG.getCode()));
        return TaskConfigTemplateData.template(offices, taskConfigs, loanAccountTypeOptions);
    }

    @Override
    public TaskConfigData retrieveOne(Long configId) {
        try {
            this.context.authenticatedUser();
            TaskConfigWithChidrenMapper taskConfigWithChidrenMapper = new TaskConfigWithChidrenMapper();
            String sql = "SELECT " + taskConfigWithChidrenMapper.schema();
            sql += " WHERE ftc.id = ? ";
            return this.jdbcTemplate.queryForObject(sql, taskConfigWithChidrenMapper, configId);
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    private static final class TaskConfigMapper implements RowMapper<TaskConfigData> {

        public String schema() {
            final StringBuilder sb = new StringBuilder(400);
            sb.append(" ftc.`id` AS configId, ftc.`parent_id` AS parentId, ftc.`name` AS configName, ftc.`task_type` AS taskType ");
            sb.append(" FROM f_task_config ftc ");
            sb.append(" LEFT JOIN f_task_config ptc ON ptc.`id` = ftc.`parent_id` ");
            return sb.toString();
        }

        @Override
        public TaskConfigData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long configId = JdbcSupport.getLongDefaultToNullIfZero(rs, "configId");
            if (configId == null) { return null; }
            final Long parentId = JdbcSupport.getLongDefaultToNullIfZero(rs, "parentId");
            final String configName = rs.getString("configName");
            final Integer taskTypeId = JdbcSupport.getInteger(rs, "taskType");
            EnumOptionData taskTypeEnum = null;
            if (taskTypeId != null && taskTypeId >= 0) {
                taskTypeEnum = TaskType.fromInt(taskTypeId).getEnumOptionData();
            }
            return TaskConfigData.lookup(configId, parentId, configName, taskTypeEnum);
        }
    }

    private static final class TaskConfigWithChidrenMapper implements RowMapper<TaskConfigData> {

        public String schema() {
            final StringBuilder sb = new StringBuilder(400);
            sb.append(" ftc.id AS id, ftc.parent_id AS parentId, ftc.name AS name, ftc.short_name AS shortName, ");
            sb.append(" ftc.task_type AS tastTypeId, ftc.task_config_order AS configOrder, ftc.criteria_id AS criteriaId, ");
            sb.append(" ftc.approval_logic AS approvalLogic, ftc.rejection_logic AS rejectionLogic, ftc.action_group_id AS actionGroupId ");
            sb.append(" FROM f_task_config ftc ");
            sb.append(" LEFT JOIN f_task_config ptc ON ptc.id = ftc.parent_id ");
            return sb.toString();
        }

        @Override
        public TaskConfigData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "id");
            if (id == null) { return null; }
            final Long parentId = JdbcSupport.getLongDefaultToNullIfZero(rs, "parentId");
            final String name = rs.getString("name");
            final String shortName = rs.getString("shortName");
            final Integer taskTypeId = JdbcSupport.getInteger(rs, "tastTypeId");
            EnumOptionData taskTypeEnum = null;
            if (taskTypeId != null && taskTypeId >= 0) {
                taskTypeEnum = TaskType.fromInt(taskTypeId).getEnumOptionData();
            }
            final Integer configOrder = JdbcSupport.getInteger(rs, "configOrder");
            final String approvalLogic = rs.getString("approvalLogic");
            final String rejectionLogic = rs.getString("rejectionLogic");
            final Long criteriaId = JdbcSupport.getLongDefaultToNullIfZero(rs, "criteriaId");
            final Long actionGroupId = JdbcSupport.getLongDefaultToNullIfZero(rs, "actionGroupId");
            return TaskConfigData.instance(id, parentId, name, shortName, taskTypeEnum, null, configOrder, criteriaId, approvalLogic,
                    rejectionLogic, null, actionGroupId);
        }
    }
}
