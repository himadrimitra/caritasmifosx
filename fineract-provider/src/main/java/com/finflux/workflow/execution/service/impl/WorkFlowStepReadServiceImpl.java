package com.finflux.workflow.execution.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.workflow.execution.data.LoanProductWorkFlowSummaryData;
import com.finflux.workflow.execution.data.StepStatus;
import com.finflux.workflow.execution.data.StepSummaryData;
import com.finflux.workflow.execution.data.WorkFlowSummaryData;
import com.finflux.workflow.execution.service.WorkFlowStepReadService;

@Service
public class WorkFlowStepReadServiceImpl implements WorkFlowStepReadService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public WorkFlowStepReadServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @SuppressWarnings({ "unused", "null" })
    @Override
    public List<LoanProductData> retrieveLoanProductWorkFlowSummary(final Long loanProductId, final Long officeId) {
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("SELECT lp.id AS loanProductId,lp.name AS loanProductName,wf.id AS workFlowId, wf.name AS workFlowName ");
        sqlBuilder.append(",co.id AS officeId, co.name AS officeName, wfes.status AS stepStatus ");
        sqlBuilder.append(",wfs.id AS stepId, wfs.name AS stepName,wfs.short_name AS stepShortName ");
        sqlBuilder.append(",SUM(IF(wfes.status BETWEEN 2 AND 6,1,0)) AS noOfCount ");
        sqlBuilder.append("FROM f_workflow wf ");
        sqlBuilder.append("JOIN f_loan_product_workflow lpw ON lpw.workflow_id = wf.id ");
        sqlBuilder.append("JOIN m_product_loan lp ON ");
        if (loanProductId != null) {
            sqlBuilder.append("lp.id = ").append(loanProductId).append(" AND ");
        }
        sqlBuilder.append("lp.id = lpw.loan_product_id ");
        sqlBuilder.append("JOIN f_workflow_execution wfe ON wfe.workflow_id = wf.id ");
        sqlBuilder.append("JOIN f_loan_application_workflow_execution lawe ON lawe.workflow_execution_id = wfe.id ");
        sqlBuilder.append("JOIN f_loan_application_reference lar ON lar.id = lawe.loan_application_id AND lp.id = lar.loan_product_id ");
        sqlBuilder.append("JOIN m_client c ON c.id = lar.client_id ");
        sqlBuilder.append("JOIN m_office o ");
        sqlBuilder.append("JOIN m_office co ON co.hierarchy LIKE CONCAT(o.hierarchy, '%') AND co.id = c.office_id ");
        sqlBuilder.append("JOIN f_workflow_execution_step wfes ON wfes.workflow_execution_id = wfe.id ");
        sqlBuilder.append("JOIN f_workflow_step wfs ON wfs.id = wfes.workflow_step_id ");
        if (officeId != null) {
            sqlBuilder.append("AND o.id = ").append(officeId).append(" ");
        }
        sqlBuilder.append("GROUP BY lp.id,wf.id,co.id,wfes.status,wfs.name ");
        sqlBuilder.append("ORDER BY lp.name,wf.name,co.id,wfs.step_order,wfes.status ");
        final List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sqlBuilder.toString());
        final List<LoanProductData> loanProducts = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            LoanProductData loanProduct = null;
            LoanProductWorkFlowSummaryData loanProductWorkFlowSummary = null;
            List<OfficeData> offices = null;
            OfficeData office = null;
            List<WorkFlowSummaryData> workFlowSummaries = null;
            WorkFlowSummaryData workFlowSummary = null;
            List<StepSummaryData> stepSummaries = null;
            StepSummaryData stepSummary = null;

            for (final Map<String, Object> l : list) {
                final Integer stepStatusId = Integer.parseInt(l.get("stepStatus").toString());
                if (!(StepStatus.fromInt(stepStatusId).getValue() > 6)) {
                    final Long lpId = (Long) l.get("loanProductId");
                    final String loanProductName = (String) l.get("loanProductName");
                    final Long oId = (Long) l.get("officeId");
                    final String officeName = (String) l.get("officeName");
                    final Long workFlowId = (Long) l.get("workFlowId");
                    final String workFlowName = (String) l.get("workFlowName");
                    final Long stepId = (Long) l.get("stepId");
                    final String stepName = (String) l.get("stepName");
                    final String stepShortName = (String) l.get("stepShortName");
                    final Long noOfCount = Long.parseLong(l.get("noOfCount").toString());
                    final String stepStatus = StepStatus.fromInt(stepStatusId).toString();

                    /**
                     * Product
                     */
                    Boolean isLoanProductData = false;
                    for (final LoanProductData lp : loanProducts) {
                        if (lp.getId() == lpId) {
                            isLoanProductData = true;
                            break;
                        }
                    }
                    if (!isLoanProductData) {
                        loanProductWorkFlowSummary = new LoanProductWorkFlowSummaryData();
                        loanProduct = LoanProductData.lookup(lpId, loanProductName);
                        loanProduct.setLoanProductWorkFlowSummary(loanProductWorkFlowSummary);
                        loanProducts.add(loanProduct);
                        offices = new ArrayList<OfficeData>();
                        loanProductWorkFlowSummary.setOfficeSummaries(offices);
                    }

                    /**
                     * Office
                     */
                    Boolean isOfficeData = false;
                    for (final OfficeData o : offices) {
                        if (o.getId() == oId) {
                            isOfficeData = true;
                            break;
                        }
                    }
                    if (!isOfficeData) {
                        workFlowSummaries = new ArrayList<WorkFlowSummaryData>();
                        office = OfficeData.lookup(oId, officeName);
                        office.setWorkFlowSummaries(workFlowSummaries);
                        offices.add(office);
                    }

                    /**
                     * work Flow Summary
                     */
                    Boolean isWorkFlowData = false;
                    for (final WorkFlowSummaryData ws : workFlowSummaries) {
                        if (ws.getStepName().equalsIgnoreCase(stepName)) {
                            isWorkFlowData = true;
                            ws.setNoOfCount(ws.getNoOfCount() + noOfCount);
                            break;
                        }
                    }
                    if (!isWorkFlowData) {
                        stepSummaries = new ArrayList<StepSummaryData>();
                        workFlowSummary = new WorkFlowSummaryData(stepName, stepShortName, noOfCount);
                        workFlowSummary.setStepSummaries(stepSummaries);
                        workFlowSummaries.add(workFlowSummary);
                    }
                    stepSummary = new StepSummaryData(stepStatus, noOfCount);
                    stepSummaries.add(stepSummary);
                }
            }
        }
        return loanProducts;
    }
}