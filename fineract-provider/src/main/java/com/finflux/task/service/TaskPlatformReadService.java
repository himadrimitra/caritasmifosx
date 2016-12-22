package com.finflux.task.service;

import com.finflux.task.data.*;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;

import java.util.List;

public interface TaskPlatformReadService {

    TaskTemplateData retrieveTemplate(Long officeId, boolean staffInSelectedOfficeOnly);

    List<Long> getChildTaskConfigIds(Long taskConfigId);

    TaskExecutionData getTaskDetailsByEntityTypeAndEntityId(final TaskEntityType taskEntityType, final Long entityId);

    TaskExecutionData getTaskDetails(final Long taskId);

    List<TaskExecutionData> getTaskChildren(Long parentTaskId);

    List<Long> getChildTasksByOrder(Long parentTaskId, int orderId);

    List<LoanProductData> retrieveLoanProductTaskSummary(final Long loanProductId, final Long officeId);

    List<TaskInfoData> retrieveTaskInformations(final String filterBy);

    List<TaskNoteData> getTaskNotes(Long taskId);

    List<TaskActionLogData> getActionLogs(Long taskId);
}