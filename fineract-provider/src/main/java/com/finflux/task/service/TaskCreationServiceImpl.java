package com.finflux.task.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.ruleengine.configuration.service.RiskConfigWritePlatformService;
import com.finflux.task.form.AdhocTaskForm;
import com.finflux.workflow.configuration.domain.WorkflowEntityTypeMapping;
import com.finflux.workflow.configuration.domain.WorkflowEntityTypeMappingRepository;
import com.finflux.workflow.execution.data.WorkFlowEntityType;
import com.finflux.workflow.execution.data.WorkFlowExecutionEntityType;
import com.finflux.workflow.execution.data.WorkflowConfigKey;
import com.finflux.workflow.execution.service.WorkflowExecutionService;

@Service
public class TaskCreationServiceImpl implements TaskCreationService {

	private final static Logger logger = LoggerFactory
			.getLogger(RiskConfigWritePlatformService.class);

	private final PlatformSecurityContext context;
	private final FromJsonHelper fromApiJsonHelper;
	private final WorkflowExecutionService workflowExecutionService;
	private final WorkflowEntityTypeMappingRepository workflowEntityTypeMappingRepository;

	@Autowired
	public TaskCreationServiceImpl(
			final PlatformSecurityContext context,
			final FromJsonHelper fromApiJsonHelper,
			final WorkflowExecutionService workflowExecutionService,
			final WorkflowEntityTypeMappingRepository workflowEntityTypeMappingRepository) {
		this.context = context;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.workflowExecutionService = workflowExecutionService;
		this.workflowEntityTypeMappingRepository = workflowEntityTypeMappingRepository;
	}

	@Override
	public CommandProcessingResult createAdhocTask(JsonCommand command) {
		this.context.authenticatedUser();
		AdhocTaskForm form = fromApiJsonHelper.fromJson(command.json(),
				AdhocTaskForm.class);
		WorkflowEntityTypeMapping workflowEntityTypeMapping = this.workflowEntityTypeMappingRepository
				.findOneByEntityTypeAndEntityId(
						WorkFlowEntityType.ADHOC.getValue(), null);
		if (workflowEntityTypeMapping != null) {
			Map<WorkflowConfigKey, String> map = new HashMap<>();
			final Long officeId = form.getOfficeId();

			map.put(WorkflowConfigKey.TITLE, form.getTitle());
			map.put(WorkflowConfigKey.BODY, form.getBody());
			Long workflowId = workflowExecutionService
					.createWorkflowExecutionForWorkflow(
							workflowEntityTypeMapping.getWorkflowId(),
							WorkFlowExecutionEntityType.ADHOC, null, null,
							officeId, map);
			return new CommandProcessingResultBuilder()//
					.withCommandId(command.commandId())//
					.withEntityId(workflowId)//
					.build();
		}

		return null;
	}
}