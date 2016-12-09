package com.finflux.task.execution.service;

import java.util.List;

import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;

import com.finflux.task.execution.data.TaskExecutionData;
import com.finflux.task.execution.data.TaskEntityType;
import com.finflux.task.execution.data.TaskInfoData;

public interface TaskReadService {

    List<Long> getChildTaskConfigIds(final Long taskConfigId);

	TaskExecutionData getTaskDetailsByEntityTypeAndEntityId(final TaskEntityType taskEntityType, final Long entityId);

	TaskExecutionData getTaskDetails(final Long taskId);

	List<TaskExecutionData> getTaskChildren(Long parentTaskId);

	List<Long> getChildTasksByOrder(Long parentTaskId, int orderId);

	List<LoanProductData> retrieveLoanProductWorkFlowSummary(final Long loanProductId, final Long officeId);

    List<TaskInfoData> retrieveWorkFlowStepActions(final String filterBy);

}