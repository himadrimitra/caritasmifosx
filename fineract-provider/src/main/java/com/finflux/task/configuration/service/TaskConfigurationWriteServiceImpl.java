package com.finflux.task.configuration.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loan.purpose.api.LoanPurposeGroupApiConstants;
import com.finflux.portfolio.loan.purpose.exception.LoanPurposeNotFoundException;
import com.finflux.task.configuration.api.TaskConfigurationApiConstants;
import com.finflux.task.configuration.data.TaskConfigurationDataValidator;
import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.domain.TaskConfig;
import com.finflux.task.domain.TaskConfigEntityTypeMapping;
import com.finflux.task.domain.TaskConfigEntityTypeMappingRepository;
import com.finflux.task.domain.TaskConfigRepositoryWrapper;

@Service
@Scope("singleton")
public class TaskConfigurationWriteServiceImpl implements TaskConfigurationWriteService {

    private final static Logger logger = LoggerFactory.getLogger(TaskConfigurationWriteServiceImpl.class);

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final TaskConfigurationDataValidator validator;
    private final TaskConfigurationDataAssembler assembler;
    private final TaskConfigRepositoryWrapper taskConfigRepository;
    private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;

    @Autowired
    public TaskConfigurationWriteServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final TaskConfigurationDataValidator validator, final TaskConfigurationDataAssembler assembler,
            final TaskConfigRepositoryWrapper taskConfigRepository,
            final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.validator = validator;
        this.assembler = assembler;
        this.taskConfigRepository = taskConfigRepository;
        this.taskConfigEntityTypeMappingRepository = taskConfigEntityTypeMappingRepository;
    }

    @Override
    public CommandProcessingResult createTaskConfig(final Long entityId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            List<TaskConfig> taskConfigs = null;
            final String entityType = command.stringValueOfParameterNamed(TaskConfigurationApiConstants.entityTypeParamName);
            if (entityType.equalsIgnoreCase(TaskConfigurationApiConstants.LOANPRODUCT)) {
                taskConfigs = createLoanProductWorkflowTasks(entityId, command);
                this.taskConfigRepository.save(taskConfigs);
            }
            if (taskConfigs != null) {
                for (final TaskConfig t : taskConfigs) {
                    if (t.getParent() == null) {
                        final TaskConfigEntityTypeMapping mapping = new TaskConfigEntityTypeMapping();
                        mapping.setTaskConfig(t);
                        mapping.setEntityType(TaskConfigEntityType.LOANPRODUCT.getValue());
                        mapping.setEntityId(entityId);
                        this.taskConfigEntityTypeMappingRepository.save(mapping);
                        break;
                    }
                }
            }
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    private List<TaskConfig> createLoanProductWorkflowTasks(final Long entityId, final JsonCommand command) {
        this.validator.validateCreateLoanProductWorkflowTasks(command.json());
        return this.assembler.assembleCreateLoanProductWorkflowTasksForm(entityId, command);
    }

    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.task.config.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }

    @Override
    public CommandProcessingResult createTaskConfigEntityMapping(Long taskConfigId, JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.validator.validateCreateTaskConfigEntityMapping(command.json());
            TaskConfig taskConfig = this.taskConfigRepository.findOneWithNotFoundDetection(taskConfigId);
            final Boolean isActive = command.booleanObjectValueOfParameterNamed(TaskConfigurationApiConstants.isActiveParamName);
            final Integer entityType = command.integerValueOfParameterNamed(TaskConfigurationApiConstants.entityTypeParamName);
            String[] entityIds = command.arrayValueOfParameterNamed(TaskConfigurationApiConstants.entityIdsParamName);
            List<TaskConfigEntityTypeMapping> mappingList = new ArrayList<>();
            for (String entityId : entityIds) {
                TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = new TaskConfigEntityTypeMapping(taskConfig, entityType,
                        Long.valueOf(entityId), isActive);
                mappingList.add(taskConfigEntityTypeMapping);
            }
            this.taskConfigEntityTypeMappingRepository.save(mappingList);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult inActivateTaskConfigEntityMapping(JsonCommand command) {
        try {
            this.context.authenticatedUser();
            List<TaskConfigEntityTypeMapping> entityMappingList = this.taskConfigEntityTypeMappingRepository
                    .findByTaskConfigIdAndEntityType(command.entityId(), command.getEntityTypeId());
            final Map<String, Object> changes = new LinkedHashMap<>(entityMappingList.size());
            for (TaskConfigEntityTypeMapping entityTypeMapping : entityMappingList) {
                // if (!entityTypeMapping.getIsActive()) { throw new
                // LoanPurposeNotFoundException(loanPurposeId, "inactivated"); }
                changes.put(TaskConfigurationApiConstants.isActiveParamName, false);
                entityTypeMapping.setIsActive(false);
                this.taskConfigEntityTypeMappingRepository.save(entityTypeMapping);
            }

            return new CommandProcessingResultBuilder()//
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }
}
