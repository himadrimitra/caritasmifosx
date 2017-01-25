package com.finflux.task.configuration.service;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.ruleengine.configuration.data.RuleData;
import com.finflux.ruleengine.configuration.service.RiskConfigReadPlatformService;
import com.finflux.task.configuration.data.LoanProdcutTasksConfigTemplateData;
import com.finflux.task.data.TaskActivityData;
import com.finflux.task.service.TaskPlatformReadService;

@Service
public class TaskConfigurationReadServiceImpl implements TaskConfigurationReadService {

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

}
