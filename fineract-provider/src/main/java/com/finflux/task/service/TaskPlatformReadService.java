package com.finflux.task.service;

import java.util.List;

import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.useradministration.data.RoleData;

import com.finflux.task.data.*;

public interface TaskPlatformReadService {

    TaskTemplateData retrieveTemplate(Long officeId, boolean staffInSelectedOfficeOnly);

    List<Long> getChildTaskConfigIds(Long taskConfigId);

    TaskExecutionData getTaskDetailsByEntityTypeAndEntityId(final TaskEntityType taskEntityType, final Long entityId);

    TaskExecutionData getTaskDetails(final Long taskId);

    List<TaskExecutionData> getTaskChildren(Long parentTaskId);

    List<Long> getChildTasksByOrder(Long parentTaskId, int orderId);

    List<LoanProductData> retrieveLoanProductTaskSummary(final Long loanProductId, final Long officeId);

    Page<TaskInfoData> retrieveTaskInformations(final String filterBy, SearchParameters searchParameters, final Long parentConfigId,
            final Long childConfigId);

    List<TaskNoteData> getTaskNotes(Long taskId);

    List<TaskActionLogData> getActionLogs(Long taskId);

    TaskMakerCheckerData getMakerCheckerData(Long taskId);

	List<RoleData> getRolesForAnAction(Long actionGroupId, Long actionId);
}