package com.finflux.task.execution.service;

import java.util.List;

import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;

import com.finflux.task.execution.data.TaskData;
import com.finflux.task.execution.data.TaskEntityType;
import com.finflux.task.execution.data.WorkFlowStepActionData;

public interface TaskReadService {

    List<Long> getChildTaskConfigIds(final Long taskConfigId);

    List<TaskData> getTaskDetailsByEntityTypeAndEntityId(final TaskEntityType taskEntityType, final Long entityId);

    List<LoanProductData> retrieveLoanProductWorkFlowSummary(final Long loanProductId, final Long officeId);

    List<WorkFlowStepActionData> retrieveWorkFlowStepActions(final String filterBy);

}