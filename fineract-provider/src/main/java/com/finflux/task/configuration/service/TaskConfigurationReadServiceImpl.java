package com.finflux.task.configuration.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.common.data.EntityData;
import com.finflux.ruleengine.configuration.data.RuleData;
import com.finflux.ruleengine.configuration.service.RiskConfigReadPlatformService;
import com.finflux.task.configuration.data.LoanProdcutTasksConfigTemplateData;
import com.finflux.task.configuration.data.TaskConfigEntityMappingData;
import com.finflux.task.configuration.data.TaskMappingTemplateData;
import com.finflux.task.data.TaskActivityData;
import com.finflux.task.data.TaskConfigData;
import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskType;
import com.finflux.task.service.TaskPlatformReadService;

@Service
public  class TaskConfigurationReadServiceImpl implements TaskConfigurationReadService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final TaskPlatformReadService taskPlatformReadService;
    private final RiskConfigReadPlatformService riskConfigReadPlatformService;

    @Autowired
    public TaskConfigurationReadServiceImpl(final RoutingDataSource dataSource, final PlatformSecurityContext context,
            final TaskPlatformReadService taskPlatformReadService, final RiskConfigReadPlatformService riskConfigReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.context = context;
        this.taskPlatformReadService = taskPlatformReadService;
        this.riskConfigReadPlatformService = riskConfigReadPlatformService;
    }

    @Override
    public LoanProdcutTasksConfigTemplateData retrieveLoanProdcutTasksConfigTemplateData() {
        final Collection<TaskActivityData> taskActivityDatas = this.taskPlatformReadService.retrieveAllTaskActivityData();
        final Collection<RuleData> criteriaOptions = this.riskConfigReadPlatformService.getAllCriterias();
        return LoanProdcutTasksConfigTemplateData.template(taskActivityDatas, criteriaOptions);
    }

    @Override
    public TaskMappingTemplateData retrieveTaskMappingTemplateData() {
        this.context.authenticatedUser();
        TaskConfigParentMapper taskConfigParentMapper = new TaskConfigParentMapper();
        String sql = "SELECT " + taskConfigParentMapper.schema() + " and ftc.task_type = ?";
        Integer taskType = TaskType.WORKFLOW.getValue();
        Collection<TaskConfigData> taskConfigList = this.jdbcTemplate.query(sql, taskConfigParentMapper, taskType);
        Collection<EnumOptionData> taskEntityTypeOptions = TaskConfigEntityType.entityTypeOptions();
        TaskMappingTemplateData taskMappingTemplateData = new TaskMappingTemplateData(taskConfigList, taskEntityTypeOptions);
        return taskMappingTemplateData;
    }

    @Override
    public Collection<TaskConfigEntityMappingData> retrieveAllTaskConfigEntityMappings() {
        this.context.authenticatedUser();
        TaskConfigEntityMappingExtractor entityMappingExtractor = new TaskConfigEntityMappingExtractor();
        String sql = "SELECT" + entityMappingExtractor.schema() + " where ftm.is_active = true order by ftm.task_config_id,ftm.entity_type";
        return this.jdbcTemplate.query(sql, entityMappingExtractor);
    }

    @Override
    public TaskConfigEntityMappingData retrieveTaskConfigEntityMapping(final Long taskConfigId, final Integer taskConfigEntityTypeVaule) {
        this.context.authenticatedUser();
        TaskConfigEntityMappingExtractor entityMappingExtractor = new TaskConfigEntityMappingExtractor();
        String sql = "SELECT" + entityMappingExtractor.schema();
        sql = sql + " where ftm.is_active = true and ftm.task_config_id = " + taskConfigId + " and ftm.entity_type = "
                + taskConfigEntityTypeVaule;
        return this.jdbcTemplate.query(sql, entityMappingExtractor).get(0);
    }

    private static final class TaskConfigParentMapper implements RowMapper<TaskConfigData> {

        public String schema() {
            final StringBuilder sb = new StringBuilder(400);
            sb.append(" ftc.id AS id, ftc.name AS name");
            sb.append(" FROM f_task_config ftc");
            sb.append(" WHERE ftc.parent_id is null and ftc.id not in (select distinct ftem.task_config_id from f_task_config_entity_type_mapping ftem)");
            return sb.toString();
        }

        @Override
        public TaskConfigData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "id");
            if (id == null) { return null; }
            final String name = rs.getString("name");
            return TaskConfigData.lookup(id, null, name, null);
        }
    }

    private static final class TaskConfigEntityMappingExtractor implements ResultSetExtractor<List<TaskConfigEntityMappingData>> {

        private String schemaSql;

        public String schema() {
            return this.schemaSql;
        }

        private TaskConfigEntityMappingExtractor() {
            final StringBuilder sb = new StringBuilder(400);
            sb.append(" ftm.task_config_id, ftc.name, ftc.short_name as taskConfigShortName, ftm.entity_type,");
            sb.append(" ftm.entity_id, pl.name as productName, pl.short_name as productShortname, ftm.is_active ");
            sb.append(" FROM f_task_config_entity_type_mapping ftm");
            sb.append(" join f_task_config ftc on ftm.task_config_id = ftc.id");
            sb.append(" left join m_product_loan pl on pl.id = ftm.entity_id and ftm.entity_type = 1"); // TaskConfigEntityType.LOANPRODUCT
                                                                                                        // =
                                                                                                        // 1
            this.schemaSql = sb.toString();
        }

        @Override
        public List<TaskConfigEntityMappingData> extractData(ResultSet rs) throws SQLException, DataAccessException {

            List<TaskConfigEntityMappingData> entityMappingDataList = new ArrayList<>();

            TaskConfigEntityMappingData taskConfigEntityMappingData = null;
            Long taskConfigId = null;
            Integer entityTypeValue = null;

            while (rs.next()) {
                final Long tempTaskConfigId = rs.getLong("task_config_id");
                final Integer tempEntityTypeValue = rs.getInt("entity_type");
                if (taskConfigEntityMappingData == null
                        || (taskConfigId != null && !taskConfigId.equals(tempTaskConfigId) && entityTypeValue != null && !entityTypeValue
                                .equals(tempEntityTypeValue))) {
                    taskConfigId = tempTaskConfigId;
                    entityTypeValue = tempEntityTypeValue;
                    TaskConfigData taskConfig = TaskConfigData.lookup(taskConfigId, null, rs.getString("name"), null);
                    EnumOptionData taskConfigEntityType = TaskConfigEntityType.fromInt(entityTypeValue).getEnumOptionData();
                    Collection<EntityData> entityDetails = new ArrayList<>();
                    if (TaskConfigEntityType.LOANPRODUCT.getValue().compareTo(entityTypeValue) == 0) {
                        EntityData entityData = new EntityData(rs.getLong("entity_id"), rs.getString("productName"),
                                rs.getString("productShortname"));
                        entityDetails.add(entityData);
                    }
                    Boolean isActiveMapping = rs.getBoolean("is_active");
                    taskConfigEntityMappingData = new TaskConfigEntityMappingData(taskConfig, taskConfigEntityType, entityDetails,
                            isActiveMapping);
                    entityMappingDataList.add(taskConfigEntityMappingData);
                } else {
                    if (TaskConfigEntityType.LOANPRODUCT.getValue().compareTo(entityTypeValue) == 0) {
                        EntityData entityData = new EntityData(rs.getLong("entity_id"), rs.getString("productName"),
                                rs.getString("productShortname"));
                        taskConfigEntityMappingData.getEntityDetails().add(entityData);
                    }
                }

            }
            return entityMappingDataList;
        }
    }

}
