package com.finflux.workflow.execution.service.impl;

import java.util.*;

import com.finflux.loanapplicationreference.data.LoanApplicationReferenceData;
import com.finflux.loanapplicationreference.domain.LoanApplicationReference;
import com.finflux.loanapplicationreference.domain.LoanApplicationReferenceRepository;
import com.finflux.loanapplicationreference.service.LoanApplicationReferenceReadPlatformService;
import com.finflux.ruleengine.configuration.service.RuleCacheService;
import com.finflux.ruleengine.execution.data.EligibilityResult;
import com.finflux.ruleengine.execution.data.EligibilityStatus;
import com.finflux.ruleengine.execution.service.DataLayerReadPlatformService;
import com.finflux.ruleengine.execution.service.RuleExecutionService;
import com.finflux.ruleengine.execution.service.impl.LoanApplicationDataLayer;
import com.finflux.ruleengine.lib.FieldUndefinedException;
import com.finflux.ruleengine.lib.InvalidExpressionException;
import com.finflux.ruleengine.lib.data.ExpressionNode;
import com.finflux.ruleengine.lib.data.RuleResult;
import com.finflux.ruleengine.lib.service.ExpressionExecutor;
import com.finflux.ruleengine.lib.service.impl.MyExpressionExecutor;
import com.finflux.workflow.configuration.domain.*;
import com.finflux.workflow.execution.domain.*;
import com.finflux.workflow.execution.exception.WorkflowStepNoActionPermissionException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.data.RoleData;
import org.apache.fineract.useradministration.service.RoleReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.finflux.workflow.execution.data.StepAction;
import com.finflux.workflow.execution.data.StepStatus;
import com.finflux.workflow.execution.data.WorkflowExecutionData;
import com.finflux.workflow.execution.data.WorkflowExecutionStepData;
import com.finflux.workflow.execution.service.WorkflowExecutionService;
import com.finflux.workflow.execution.service.WorkflowReadService;
import com.finflux.workflow.execution.service.WorkflowExecutionWriteService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by dhirendra on 22/09/16.
 */
@Service
@Scope("singleton")
public class WorkflowExecutionServiceImpl implements WorkflowExecutionService {

    private final WorkflowReadService workflowReadService;
    private final WorkflowExecutionWriteService workflowWriteService;
    private final WorkflowExecutionRepository workflowExecutionRepository;
    private final WorkflowExecutionStepRepository workflowExecutionStepRepository;
    private final WorkflowStepRepository workflowStepRepository;
    private final WorkflowStepActionRepository workflowStepActionRepository;
    private final LoanApplicationReferenceRepository loanApplicationReferenceRepository;
    private final LoanProductWorkflowRepository loanProductWorkflowRepository;
    private final LoanApplicationWorkflowExecutionRepository loanApplicationWorkflowExecutionRepository;
    private final RoleReadPlatformService roleReadPlatformService;
    private final PlatformSecurityContext context;
    private final RuleExecutionService ruleExecutionService;
    private final DataLayerReadPlatformService dataLayerReadPlatformService;
    private final RuleCacheService ruleCacheService;
    private final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService;
    private final ExpressionExecutor expressionExecutor;
    private final WorkflowStepActionRoleRepository workflowStepActionRoleRepository;

    @Autowired
    public WorkflowExecutionServiceImpl(final WorkflowReadService workflowExecutionReadService,
                                        final WorkflowExecutionWriteService workflowExecutionWriteService, final WorkflowExecutionRepository workflowExecutionRepository,
                                        final WorkflowExecutionStepRepository workflowExecutionStepRepository,
                                        final WorkflowStepRepository workflowStepRepository,
                                        final LoanApplicationReferenceRepository loanApplicationReferenceRepository,
                                        final LoanProductWorkflowRepository loanProductWorkflowRepository,
                                        final LoanApplicationWorkflowExecutionRepository loanApplicationWorkflowExecutionRepository,
                                        final WorkflowStepActionRepository workflowStepActionRepository,
                                        final RoleReadPlatformService roleReadPlatformService,
                                        final PlatformSecurityContext context,
                                        final RuleExecutionService ruleExecutionService,
                                        final DataLayerReadPlatformService dataLayerReadPlatformService,
                                        final RuleCacheService ruleCacheService,
                                        final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService,
                                        final MyExpressionExecutor expressionExecutor,
                                        final WorkflowStepActionRoleRepository workflowStepActionRoleRepository) {
        this.workflowReadService = workflowExecutionReadService;
        this.workflowWriteService = workflowExecutionWriteService;
        this.workflowExecutionRepository = workflowExecutionRepository;
        this.workflowExecutionStepRepository = workflowExecutionStepRepository;
        this.workflowStepRepository = workflowStepRepository;
        this.loanApplicationReferenceRepository = loanApplicationReferenceRepository;
        this.loanProductWorkflowRepository = loanProductWorkflowRepository;
        this.loanApplicationWorkflowExecutionRepository = loanApplicationWorkflowExecutionRepository;
        this.workflowStepActionRepository = workflowStepActionRepository;
        this.roleReadPlatformService = roleReadPlatformService;
        this.context = context;
        this.ruleCacheService = ruleCacheService;
        this.ruleExecutionService = ruleExecutionService;
        this.dataLayerReadPlatformService = dataLayerReadPlatformService;
        this.loanApplicationReferenceReadPlatformService = loanApplicationReferenceReadPlatformService;
        this.expressionExecutor = expressionExecutor;
        this.workflowStepActionRoleRepository = workflowStepActionRoleRepository;
    }

    @Override
    public Long getOrCreateWorkflowExecutionForLoanApplication(final Long loanApplicationId) {
        LoanApplicationWorkflowExecution loanApplicationWorkflowExecution = loanApplicationWorkflowExecutionRepository
                .findOneByLoanApplicationId(loanApplicationId);
        if(loanApplicationWorkflowExecution !=null){
            return loanApplicationWorkflowExecution.getWorkflowExecutionId();
        }else{
            return createWorkflowExecutionForLoanApplication(loanApplicationId);
        }
    }

    @Transactional
    private Long createWorkflowExecutionForLoanApplication(final Long loanApplicationId) {
        //create workflow
        LoanApplicationReference loanApplicationReference = loanApplicationReferenceRepository.findOne(loanApplicationId);
        Long loanProductId = loanApplicationReference.getLoanProduct().getId();
        LoanProductWorkflow loanProductWorkflow= loanProductWorkflowRepository.findOne(loanProductId);
        if(loanProductWorkflow == null){
            return null;
        }
        Long workflowId = loanProductWorkflow.getWorkflowId();
        Long workflowExecutionId = createWorkflowExecutionForWorkflow(workflowId);
        LoanApplicationWorkflowExecution loanApplicationWorkflowExecution = LoanApplicationWorkflowExecution.create(loanApplicationId,workflowExecutionId);
        loanApplicationWorkflowExecutionRepository.save(loanApplicationWorkflowExecution);
        return workflowExecutionId;
    }

    private Long createWorkflowExecutionForWorkflow(final Long workflowId) {
        final List<Long> workflowStepIds = this.workflowReadService.getWorkflowStepsIds(workflowId);
        if (!workflowStepIds.isEmpty()) {
            final WorkflowExecution workflowExecution = WorkflowExecution.create(workflowId);
            this.workflowExecutionRepository.save(workflowExecution);
            int index = 0;
            for (final Long workflowStepId : workflowStepIds) {
                StepStatus stepStatus = StepStatus.INACTIVE;
                if(index == 0){
                    stepStatus = StepStatus.INITIATED;
                }
                final WorkflowExecutionStep workflowExecutionStep = WorkflowExecutionStep.create(workflowExecution.getId(), workflowStepId,
                        stepStatus.getValue());
                if(index == 0){
                    updateAssignedTo(workflowExecutionStep,stepStatus);
                }
                this.workflowExecutionStepRepository.save(workflowExecutionStep);
                index = index + 1;
            }
            return workflowExecution.getId();
        }
        return null;
    }

    @Override
    public WorkflowExecutionData getWorkflowExecutionData(final Long workflowExecutionId) {
        return this.workflowReadService.getWorkflowExecutionData(workflowExecutionId);
    }

    @Override
    public WorkflowExecutionStepData getWorkflowExecutionStepData(final Long workflowExecutionStepId) {
        WorkflowExecutionStepData stepData =  this.workflowReadService.getWorkflowExecutionStepData(workflowExecutionStepId);
        return stepData;
    }

    @Override
    @Transactional
    public void doActionOnWorkflowExecutionStep(Long workflowExecutionStepId, StepAction stepAction) {
        WorkflowExecutionStep workflowExecutionStep =  workflowExecutionStepRepository.findOne(workflowExecutionStepId);
        StepStatus status = StepStatus.fromInt(workflowExecutionStep.getStatus());
        if(stepAction !=null && status!=null){
            if(status.getPossibleActionsEnumOption().contains(stepAction)){
                //not supported action
            }

            if(stepAction.isCheckPermission()) {
                checkUserhasActionPrivilege(workflowExecutionStep, stepAction);
            }
            if(StepAction.CRITERIACHECK.equals(stepAction)){
                runCriteriaCheckAndPopulate(workflowExecutionStep);
            }
            if(stepAction.getToStatus()!=null) {
                StepStatus newStatus = stepAction.getToStatus();
                if(status.equals(newStatus)){
                    //do-nothing
                    return;
                }
                workflowExecutionStep.setStatus(newStatus.getValue());
                // update assigned-to if current user has next action
                updateAssignedTo(workflowExecutionStep, newStatus);

                workflowExecutionStepRepository.save(workflowExecutionStep);
                notifyWorkflow(workflowExecutionStep.getWorkflowExecutionId(), workflowExecutionStepId, status, newStatus);
            }
        }
        //do Action Log
    }

    private void updateAssignedTo(WorkflowExecutionStep workflowExecutionStep, StepStatus newStatus) {
        StepAction nextPossibleAction = newStatus.getNextPositiveAction();
        if(canUserDothisAction(workflowExecutionStep, nextPossibleAction)){
            workflowExecutionStep.setAssignedTo(context.authenticatedUser().getId());
        }else{
            workflowExecutionStep.setAssignedTo(null);
        }
    }

    private void runCriteriaCheckAndPopulate(WorkflowExecutionStep workflowExecutionStep) {
        WorkflowStep workflowStep =  workflowStepRepository.findOne(workflowExecutionStep.getWorkflowStepId());
        LoanApplicationWorkflowExecution loanApplicationWorkflowExecution = loanApplicationWorkflowExecutionRepository.findOneByWorkflowExecutionId(workflowExecutionStep.getWorkflowExecutionId());
        Long loanApplicationId = loanApplicationWorkflowExecution.getLoanApplicationId();
        LoanApplicationReferenceData loanApplicationReference = loanApplicationReferenceReadPlatformService.retrieveOne(loanApplicationId);
        Long clientId = loanApplicationReference.getClientId();
        LoanApplicationDataLayer dataLayer = new LoanApplicationDataLayer(loanApplicationId,clientId,
                dataLayerReadPlatformService, ruleCacheService);
        RuleResult ruleResult = ruleExecutionService.executeCriteria(workflowStep.getCriteriaId(),dataLayer);
        EligibilityResult eligibilityResult = new EligibilityResult();
        eligibilityResult.setStatus(EligibilityStatus.TO_BE_REVIEWED);
        eligibilityResult.setCriteriaOutput(ruleResult);
        ExpressionNode approvalLogic = new Gson().fromJson(workflowStep.getApprovalLogic(),new TypeToken<ExpressionNode>() {}.getType());
        ExpressionNode rejectionLogic = new Gson().fromJson(workflowStep.getRejectionLogic(),new TypeToken<ExpressionNode>() {}.getType());
        if(ruleResult !=null && ruleResult.getOutput().getValue()!=null){
            Map<String, Object> map = new HashMap();
            map.put("criteria", ruleResult.getOutput().getValue());
            boolean rejectionResult = false;
            boolean approvalResult = false;
            try {
                rejectionResult = expressionExecutor.executeExpression(rejectionLogic,map);
            } catch (FieldUndefinedException e) {
                e.printStackTrace();
            } catch (InvalidExpressionException e) {
                e.printStackTrace();
            }

            if(rejectionResult){
                eligibilityResult.setStatus(EligibilityStatus.REJECTED);
            }else{
                try {
                    approvalResult = expressionExecutor.executeExpression(approvalLogic,map);
                } catch (FieldUndefinedException e) {
                    e.printStackTrace();
                } catch (InvalidExpressionException e) {
                    e.printStackTrace();
                }
            }

            if(approvalResult){
                eligibilityResult.setStatus(EligibilityStatus.APPROVED);
            }
        }
        StepAction nextEligibleAction = StepAction.REVIEW;
        if(EligibilityStatus.APPROVED.equals(eligibilityResult.getStatus())){
            nextEligibleAction = StepAction.APPROVE;
        }else if(EligibilityStatus.REJECTED.equals(eligibilityResult.getStatus())){
            nextEligibleAction = StepAction.REJECT;
        }
        workflowExecutionStep.setCriteriaAction(nextEligibleAction.getValue());
        workflowExecutionStep.setCriteriaResult(new Gson().toJson(eligibilityResult));
    }

    private void checkUserhasActionPrivilege(WorkflowExecutionStep workflowExecutionStep, StepAction stepAction) {
        WorkflowStepAction workflowStepAction= workflowStepActionRepository.findOneByWorkflowStepIdAndAction(
                workflowExecutionStep.getWorkflowStepId(), stepAction.getValue());
        if(workflowStepAction!=null){
            if(canUserDothisAction(workflowStepAction)){
                return;
            }else{
                throw new WorkflowStepNoActionPermissionException(stepAction);
            }
        }
    }

    private boolean canUserDothisAction(WorkflowExecutionStep workflowExecutionStep, StepAction stepAction) {
        WorkflowStepAction workflowStepAction= workflowStepActionRepository.findOneByWorkflowStepIdAndAction(
                workflowExecutionStep.getWorkflowStepId(), stepAction.getValue());
        return canUserDothisAction(workflowStepAction);
    }

    private boolean canUserDothisAction(WorkflowStepAction workflowStepAction) {
        List<WorkflowStepActionRole> actionRoles = workflowStepActionRoleRepository.findByWorkflowStepActionId(workflowStepAction.getId());
        List<Long> roles = new ArrayList<>();

        if(actionRoles == null || actionRoles.isEmpty()){
            return true;
        }
        for(WorkflowStepActionRole actionRole:actionRoles){
            roles.add(actionRole.getRoleId());
        }
        Collection<RoleData> roleDatas = roleReadPlatformService.retrieveAppUserRoles(context.authenticatedUser().getId());
        for(RoleData roleData: roleDatas){
            if(roles.contains(roleData.getId())){
                return true;
            }
        }
        return false;
    }

    private void notifyWorkflow(Long workflowExecutionId, Long workflowExecutionStepId, StepStatus status, StepStatus newStatus) {

        if(StepStatus.COMPLETED.equals(newStatus)){

            List<Long> nextExecutionStepIds = getNextExecutionStepsByOrder(workflowExecutionId,workflowExecutionStepId);
            if(nextExecutionStepIds!=null && !nextExecutionStepIds.isEmpty()){
                for(Long nextExecutionStepId: nextExecutionStepIds){
                    WorkflowExecutionStep nextExecutionStep =  workflowExecutionStepRepository.findOne(nextExecutionStepId);
                    if(StepStatus.INACTIVE.getValue().equals(nextExecutionStep.getStatus())){
                        nextExecutionStep.setStatus(StepStatus.INITIATED.getValue());
                        updateAssignedTo(nextExecutionStep,StepStatus.INITIATED);
                        workflowExecutionStepRepository.save(nextExecutionStep);
                    }
                }
            }
            //set workflow status as all step done

        }else if(StepStatus.CANCELLED.equals(newStatus)){
            //do some logging or transition steps
        }
    }

    private List<Long> getNextExecutionStepsByOrder(Long workflowExecutionId, Long workflowExecutionStepId) {
        WorkflowExecutionStep workflowExecutionStep =  workflowExecutionStepRepository.findOne(workflowExecutionStepId);
        WorkflowStep workflowStep = workflowStepRepository.findOne(workflowExecutionStep.getWorkflowStepId());
        return workflowReadService.getExecutionStepsByOrder(workflowExecutionId,workflowStep.getStepOrder()+1);
    }


    @Override
    public void addNoteToWorkflowExecution(Long workflowExecutionId) {

    }

    @Override
    public void addNoteToWorkflowExecutionStep(Long workflowExecutionStepId) {

    }

    @Override
    public List<EnumOptionData> getClickableActionsForUser(Long workflowExecutionStepId, Long userId) {
        return getPossibleActions(workflowExecutionStepId,userId,true);
    }

    private List<EnumOptionData> getPossibleActions(Long workflowExecutionStepId, Long userId, boolean onlyClickable) {
        WorkflowExecutionStep workflowExecutionStep = workflowExecutionStepRepository.findOne(workflowExecutionStepId);
        StepStatus status = StepStatus.fromInt(workflowExecutionStep.getStatus());
        List<EnumOptionData> actionEnums = new ArrayList<>();
        //skip over statuses if no actions been configured workflow_Step_action.
        // Example if Approval is not assigned to a step. After Review it will move the step from Underreview to Completed
        //instead of Under Approval
        StepStatus nextEquivalentStatus = getNextEquivalentStatus(workflowExecutionStep.getWorkflowStepId(),status);
        WorkflowStep workflowStep = workflowStepRepository.findOne(workflowExecutionStep.getWorkflowStepId());
        if(workflowStep.getCriteriaId()!=null && StepStatus.UNDERREVIEW.equals(status)){
            StepAction stepAction = StepAction.fromInt(workflowExecutionStep.getCriteriaAction());
            if(stepAction!=null){
                actionEnums.add(stepAction.getEnumOptionData());
                return actionEnums;
            }
        }
        List<StepAction> actions = nextEquivalentStatus.getPossibleActionEnums();
        for (StepAction action : actions) {
            WorkflowStepAction workflowStepAction = workflowStepActionRepository.findOneByWorkflowStepIdAndAction(
                    workflowExecutionStep.getWorkflowStepId(), action.getValue());
            if(workflowStepAction == null){
                continue;
            }
            if(onlyClickable){
                if (action.isClickable() && canUserDothisAction(workflowStepAction)) {
                    actionEnums.add(action.getEnumOptionData());
                }
            }else{
                if (canUserDothisAction(workflowStepAction)) {
                    actionEnums.add(action.getEnumOptionData());
                }
            }

        }
        return actionEnums;
    }

    private StepStatus getNextEquivalentStatus(Long workflowStepId, StepStatus status) {
        while(status.getNextPositiveAction()!=null){
            StepAction nextAction = status.getNextPositiveAction();
            WorkflowStepAction workflowStepAction = workflowStepActionRepository.findOneByWorkflowStepIdAndAction(
                    workflowStepId, nextAction.getValue());
            if(workflowStepAction==null){
                status = nextAction.getToStatus();
            }else{
                return status;
            }
        }
        return status;
    }
}
