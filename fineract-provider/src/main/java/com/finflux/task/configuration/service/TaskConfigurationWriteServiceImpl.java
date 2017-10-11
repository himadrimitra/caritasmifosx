package com.finflux.task.configuration.service;

import java.util.List;

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
    private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMapping;

    @Autowired
    public TaskConfigurationWriteServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final TaskConfigurationDataValidator validator, final TaskConfigurationDataAssembler assembler,
            final TaskConfigRepositoryWrapper taskConfigRepository, final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMapping) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.validator = validator;
        this.assembler = assembler;
        this.taskConfigRepository = taskConfigRepository;
        this.taskConfigEntityTypeMapping = taskConfigEntityTypeMapping;
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
                        this.taskConfigEntityTypeMapping.save(mapping);
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
}
