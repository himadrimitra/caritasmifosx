package com.finflux.loanapplicationreference.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.api.JsonQuery;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.data.DisbursementData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidLoanStateTransitionException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanApplicationDateException;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModel;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModelPeriod;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleCalculationPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanApplicationWritePlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanWritePlatformService;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductBusinessRuleValidator;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.apache.fineract.portfolio.loanproduct.exception.LoanProductNotFoundException;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.loanapplicationreference.api.LoanApplicationReferenceApiConstants;
import com.finflux.loanapplicationreference.data.LoanApplicationReferenceDataValidator;
import com.finflux.loanapplicationreference.data.LoanApplicationReferenceStatus;
import com.finflux.loanapplicationreference.domain.LoanApplicationReference;
import com.finflux.loanapplicationreference.domain.LoanApplicationReferenceRepositoryWrapper;
import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.domain.TaskConfigEntityTypeMapping;
import com.finflux.task.domain.TaskConfigEntityTypeMappingRepository;
import com.finflux.task.service.TaskPlatformWriteService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class LoanApplicationReferenceWritePlatformServiceImpl implements LoanApplicationReferenceWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(LoanApplicationReferenceWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final LoanApplicationReferenceDataValidator validator;
    private final LoanApplicationReferenceDataAssembler assembler;
    private final LoanApplicationReferenceRepositoryWrapper repository;
    private final LoanApplicationWritePlatformService loanApplicationWritePlatformService;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanWritePlatformService loanWritePlatformService;
    private final LoanRepositoryWrapper loanRepository;
    private final FromJsonHelper fromJsonHelper;
    private final LoanScheduleCalculationPlatformService calculationPlatformService;
    private final LoanProductRepository loanProductRepository;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final LoanProductBusinessRuleValidator loanProductBusinessRuleValidator;
    private final TaskPlatformWriteService taskPlatformWriteService;
    private final ConfigurationDomainService configurationDomainService;
    private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
    private final PaymentTypeRepositoryWrapper paymentTypeRepository;

    private final String resourceNameForPermissionsForDisburseLoan = "DISBURSE_LOAN";

    @Autowired
    public LoanApplicationReferenceWritePlatformServiceImpl(final PlatformSecurityContext context, final FromJsonHelper fromApiJsonHelper,
            final LoanApplicationReferenceDataValidator validator, final LoanApplicationReferenceDataAssembler assembler,
            final LoanApplicationReferenceRepositoryWrapper repository,
            final LoanApplicationWritePlatformService loanApplicationWritePlatformService,
            final LoanReadPlatformService loanReadPlatformService, final LoanWritePlatformService loanWritePlatformService,
            final LoanRepositoryWrapper loanRepository, final FromJsonHelper fromJsonHelper,
            final LoanScheduleCalculationPlatformService calculationPlatformService, final LoanProductRepository loanProductRepository,
            final LoanProductReadPlatformService loanProductReadPlatformService,
            final LoanProductBusinessRuleValidator loanProductBusinessRuleValidator,
            final TaskPlatformWriteService taskPlatformWriteService, final ConfigurationDomainService configurationDomainService,
            final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
            final PaymentTypeRepositoryWrapper paymentTypeRepository) {
        this.context = context;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.validator = validator;
        this.assembler = assembler;
        this.repository = repository;
        this.loanApplicationWritePlatformService = loanApplicationWritePlatformService;
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanWritePlatformService = loanWritePlatformService;
        this.loanRepository = loanRepository;
        this.fromJsonHelper = fromJsonHelper;
        this.calculationPlatformService = calculationPlatformService;
        this.loanProductRepository = loanProductRepository;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.loanProductBusinessRuleValidator = loanProductBusinessRuleValidator;
        this.taskPlatformWriteService = taskPlatformWriteService;
        this.configurationDomainService = configurationDomainService;
        this.taskConfigEntityTypeMappingRepository = taskConfigEntityTypeMappingRepository;
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Override
    public CommandProcessingResult create(final JsonCommand command) {
        try {
            final Long loanProductId = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanProductIdParamName);
            final Boolean isPenalty = false;
            final List<Map<String, Object>> chargeIdList = this.loanProductReadPlatformService.getLoanProductMandatoryCharges(
                    loanProductId, isPenalty);
            this.loanProductBusinessRuleValidator.validateLoanProductMandatoryCharges(chargeIdList, command.parsedJson());
            this.validator.validateForCreate(command.json());

            final LoanApplicationReference loanApplicationReference = this.assembler.assembleCreateForm(command);

            final LoanProduct loanProduct = this.loanProductRepository.findOne(loanProductId);
            if (loanProduct == null) { throw new LoanProductNotFoundException(loanProductId); }
            this.validator.validateLoanAmountRequestedMinMaxConstraint(command.json(), loanProduct);

            this.repository.save(loanApplicationReference);

            /**
             * Check work flow configuration enabled or not
             */
            Boolean isProductMappedToWorkFlow = false;
            if (this.configurationDomainService.isWorkFlowEnabled()) {
                /**
                 * Checking is loan product mapped with task configuration
                 * entity type LOAN_PRODUCT
                 */
                final TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
                        .findOneByEntityTypeAndEntityId(TaskConfigEntityType.LOAN_PRODUCT.getValue(), loanProductId);
                if (taskConfigEntityTypeMapping != null) {
                    isProductMappedToWorkFlow = true;
                    final Long loanApplicationId = loanApplicationReference.getId();
                    final Long clientId = loanApplicationReference.getClient().getId();
                    Client client = loanApplicationReference.getClient();
                    final Map<TaskConfigKey, String> map = new HashMap<>();
                    map.put(TaskConfigKey.CLIENT_ID, String.valueOf(clientId));
                    map.put(TaskConfigKey.LOANAPPLICATION_ID, String.valueOf(loanApplicationId));
                    String description = constructDescription(loanProduct.getProductName(), loanApplicationReference.getLoanApplicationReferenceNo(), client);
                    this.taskPlatformWriteService.createTaskFromConfig(taskConfigEntityTypeMapping.getTaskConfigId(),
                            TaskEntityType.LOAN_APPLICATION, loanApplicationId, loanApplicationReference.getClient(),
                            loanApplicationReference.getClient().getOffice(), map, description);
                }
            }

            final Map<String, Object> changes = new LinkedHashMap<>(5);
            changes.put("isProductMappedToWorkFlow", isProductMappedToWorkFlow);

            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(loanApplicationReference.getId())//
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(dve);
            return CommandProcessingResult.empty();
        }
    }

    private String constructDescription(String productName, String loanApplicationReferenceNo, Client client) {
        String description = productName + " Application #" + loanApplicationReferenceNo + " for "
                + client.getDisplayName().toUpperCase() + " in Office (" + client.getOfficeName() + ")";
        return description;
    }

    @Override
    public CommandProcessingResult update(final Long loanApplicationReferenceId, JsonCommand command) {
        try {
            final LoanApplicationReference loanApplicationReference = this.repository
                    .findOneWithNotFoundDetection(loanApplicationReferenceId);
            final Long loanProductId = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanProductIdParamName);
            final Boolean isPenalty = false;
            final List<Map<String, Object>> chargeIdList = this.loanProductReadPlatformService.getLoanProductMandatoryCharges(
                    loanProductId, isPenalty);
            this.loanProductBusinessRuleValidator.validateLoanProductMandatoryCharges(chargeIdList, command.parsedJson());
            this.validator.validateForUpdate(command.json());
            final LoanProduct loanProduct = this.loanProductRepository.findOne(loanProductId);
            if (loanProduct == null) { throw new LoanProductNotFoundException(loanProductId); }
            this.validator.validateLoanAmountRequestedMinMaxConstraint(command.json(), loanProduct);

            final Map<String, Object> changes = this.assembler.assembleUpdateForm(loanApplicationReference, command);

            if (!changes.isEmpty()) {
                this.repository.saveAndFlush(loanApplicationReference);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(loanApplicationReferenceId) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult requestForApproval(final Long loanApplicationReferenceId, final JsonCommand command) {
        try {
            final LoanApplicationReference loanApplicationReference = this.repository
                    .findOneWithNotFoundDetection(loanApplicationReferenceId);
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            final Integer statusEnum = LoanApplicationReferenceStatus.APPLICATION_IN_APPROVE_STAGE.getValue();
            changes.put("statusEnum", statusEnum);
            loanApplicationReference.updateStatusEnum(statusEnum);
            this.repository.saveAndFlush(loanApplicationReference);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(loanApplicationReferenceId) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult reject(Long loanApplicationReferenceId, JsonCommand command) {
        try {
            final LoanApplicationReference loanApplicationReference = this.repository
                    .findOneWithNotFoundDetection(loanApplicationReferenceId);
            final Map<String, Object> changes = new LinkedHashMap<>(1);
            final Integer statusEnum = LoanApplicationReferenceStatus.APPLICATION_REJECTED.getValue();
            changes.put("statusEnum", statusEnum);
            loanApplicationReference.updateStatusEnum(statusEnum);
            this.repository.saveAndFlush(loanApplicationReference);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(loanApplicationReferenceId) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult approve(final Long loanApplicationReferenceId, final JsonCommand command) {
        try {
            final LoanApplicationReference loanApplicationReference = this.repository
                    .findOneWithNotFoundDetection(loanApplicationReferenceId);

            final JsonElement element = this.fromApiJsonHelper.parse(command.json());
            final JsonObject jsonObject = element.getAsJsonObject();
            final JsonObject validationJsonObject = jsonObject.getAsJsonObject("formValidationData");
            final JsonElement validateJsonElement = this.fromApiJsonHelper.parse(validationJsonObject.toString());
            final JsonCommand validateCommand = JsonCommand.fromExistingCommand(command, validateJsonElement);
            final JsonQuery query = JsonQuery.from(validationJsonObject.toString(),
                    this.fromApiJsonHelper.parse(validationJsonObject.toString()), this.fromJsonHelper);
            final boolean considerAllDisbursmentsInSchedule = true;
            final LoanScheduleModel loanScheduleModel = this.calculationPlatformService.calculateLoanSchedule(query, true,
                    considerAllDisbursmentsInSchedule);

            final JsonObject approveJsonObject = jsonObject.getAsJsonObject("formRequestData");
            final JsonElement approveJsonElement = this.fromApiJsonHelper.parse(approveJsonObject.toString());
            final JsonCommand approveCommand = JsonCommand.fromExistingCommand(command, approveJsonElement);

            final LocalDate submittedOnDate = validateCommand
                    .localDateValueOfParameterNamed(LoanApplicationReferenceApiConstants.submittedOnDateParamName);

            final Long loanProductId = validateCommand.longValueOfParameterNamed("productId");
            final Boolean isPenalty = false;
            final List<Map<String, Object>> chargeIdList = this.loanProductReadPlatformService.getLoanProductMandatoryCharges(
                    loanProductId, isPenalty);
            this.loanProductBusinessRuleValidator.validateLoanProductMandatoryCharges(chargeIdList, approveCommand.parsedJson());
            this.validator.validateForApprove(approveCommand.json(), submittedOnDate);

            if (approveCommand.parameterExists(LoanApplicationReferenceApiConstants.fixedEmiAmountParamName)) {
                final LoanProduct loanProduct = loanApplicationReference.getLoanProduct();
                final Integer minimumNoOfRepayments = loanProduct.getMinNumberOfRepayments();
                final Integer maximumNoOfRepayments = loanProduct.getMaxNumberOfRepayments();
                final Collection<LoanScheduleModelPeriod> loanScheduleModelPeriods = loanScheduleModel.getPeriods();
                Integer actualNumberOfRepayments = 0;
                for (final LoanScheduleModelPeriod loanScheduleModelPeriod : loanScheduleModelPeriods) {
                    if (loanScheduleModelPeriod != null && loanScheduleModelPeriod.periodNumber() != null) {
                        actualNumberOfRepayments++;
                    }
                }

                this.validator.validateLoanTermAndRepaidEveryValues(minimumNoOfRepayments, maximumNoOfRepayments, actualNumberOfRepayments);
            }

            Map<String, Object> changes = this.assembler.assembleApproveForm(loanApplicationReference, approveCommand);

            final BigDecimal loanAmountApproved = approveCommand
                    .bigDecimalValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanAmountApprovedParamName);
            if (loanAmountApproved.compareTo(loanApplicationReference.getLoanAmountRequested()) == 1) {
                final String errorMessage = "Loan approved amount can't be greater than loan amount demanded.";
                throw new InvalidLoanStateTransitionException("approval", "amount.can't.be.greater.than.loan.amount.demanded",
                        errorMessage, loanApplicationReference.getLoanAmountRequested(), loanAmountApproved);
            }

            this.repository.save(loanApplicationReference);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(loanApplicationReferenceId) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult undoApprove(final Long loanApplicationReferenceId, final JsonCommand command) {

        final LoanApplicationReference loanApplicationReference = this.repository.findOneWithNotFoundDetection(loanApplicationReferenceId);
        final Map<String, Object> changes = new LinkedHashMap<>(1);
        final Integer statusEnum = LoanApplicationReferenceStatus.APPLICATION_IN_APPROVE_STAGE.getValue();
        changes.put("statusEnum", statusEnum);
        loanApplicationReference.updateStatusEnum(statusEnum);
        this.repository.saveAndFlush(loanApplicationReference);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanApplicationReferenceId) //
                .with(changes) //
                .build();
    }

    @Override
    public CommandProcessingResult disburse(final Long loanApplicationReferenceId, final JsonCommand command) {

        this.context.authenticatedUser().validateHasThesePermission(this.resourceNameForPermissionsForDisburseLoan);

        final LoanApplicationReference loanApplicationReference = this.repository.findOneWithNotFoundDetection(loanApplicationReferenceId);

        this.validator.validateForDisburse(command.json());

        if (loanApplicationReference.getStatusEnum().intValue() == LoanApplicationReferenceStatus.APPLICATION_APPROVED.getValue()
                .intValue()) {

            final JsonElement element = this.fromApiJsonHelper.parse(command.json());
            final JsonObject jsonObject = element.getAsJsonObject();
            final JsonElement elementSubmit = this.fromApiJsonHelper.parse(jsonObject.get("submitApplication").toString());
            final JsonCommand submitCommand = JsonCommand.fromExistingCommand(command, elementSubmit);

            CommandProcessingResult result = submitLoan(submitCommand);
            final Long loanId = result.resourceId();
            final Loan loan = this.loanRepository.findOneWithNotFoundDetection(loanId);
            loan.updateProposedPrincipal(loanApplicationReference.getLoanAmountRequested());
            loan.setExpectedDisbursalPaymentType(loanApplicationReference.getExpectedDisbursalPaymentType());
            loan.setExpectedRepaymentPaymentType(loanApplicationReference.getExpectedRepaymentPaymentType());
            this.loanRepository.save(loan);

            result = approveLoan(loanApplicationReference, loanId, submitCommand);
            loan.updateApprovedPrincipal(loanApplicationReference.getLoanApplicationSanction().getLoanAmountApproved());
            this.loanRepository.save(loan);

            result = disburseLoan(loanId, command);
            loanApplicationReference.updateLoan(loan);
            this.loanRepository.save(loan);
        } else {
            /**
             * Through error : Application is not in disbursement stage.
             */
            final String defaultUserMessage = "Loan application not in disbursement stage.";
            throw new LoanApplicationDateException("loan.application.not.in.disbursement.stage", defaultUserMessage);
        }

        final Map<String, Object> changes = new LinkedHashMap<>(1);
        final Integer statusEnum = LoanApplicationReferenceStatus.APPLICATION_ACTIVE.getValue();
        changes.put("statusEnum", statusEnum);
        changes.put("loanId", loanApplicationReference.getLoan().getId());
        loanApplicationReference.updateStatusEnum(statusEnum);
        this.repository.saveAndFlush(loanApplicationReference);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanApplicationReferenceId) //
                .with(changes) //
                .build();
    }

    private CommandProcessingResult submitLoan(final JsonCommand command) {
        return this.loanApplicationWritePlatformService.submitApplication(command);
    }

    private CommandProcessingResult approveLoan(final LoanApplicationReference loanApplicationReference, final Long loanId,
            final JsonCommand command) {

        final JsonObject jsonObject = new JsonObject();
        final String pattern = command.dateFormat().toString();
        jsonObject.addProperty("approvedOnDate", loanApplicationReference.getLoanApplicationSanction().approvedOnDateParamNameLocalDate()
                .toString(pattern));
        jsonObject.addProperty("approvedLoanAmount", loanApplicationReference.getLoanApplicationSanction().getLoanAmountApproved());
        jsonObject.addProperty("expectedDisbursementDate", loanApplicationReference.getLoanApplicationSanction()
                .expectedDisbursementDateParamNameLocalDate().toString(pattern));
        if (command.hasParameter("disbursementData")) {
            final JsonArray disbursementDataArray = new JsonArray();
            Collection<DisbursementData> disbursementData = this.loanReadPlatformService.retrieveLoanDisbursementDetails(loanId);
            for (final DisbursementData data : disbursementData) {
                final JsonObject object = new JsonObject();
                object.addProperty("id", data.getId());
                object.addProperty("principal", data.getPrincipal());
                object.addProperty("expectedDisbursementDate", data.getExpectedDisbursementDate().toString(pattern));
                object.addProperty("loanChargeId", data.getLoanChargeId());
                final JsonElement element = this.fromApiJsonHelper.parse(object.toString());
                disbursementDataArray.add(element);
            }
            jsonObject.add("disbursementData", disbursementDataArray);
        }
        jsonObject.addProperty("locale", command.locale().toString());
        jsonObject.addProperty("dateFormat", command.dateFormat().toString());
        final JsonElement element = this.fromApiJsonHelper.parse(jsonObject.toString());
        final JsonCommand approveCommand = JsonCommand.fromExistingCommand(command, element);
        return this.loanApplicationWritePlatformService.approveApplication(loanId, approveCommand);
    }

    private CommandProcessingResult disburseLoan(final Long loanId, final JsonCommand command) {
        final JsonElement element = this.fromApiJsonHelper.parse(command.json());
        final JsonObject jsonObject = element.getAsJsonObject();
        final JsonElement elementDisburse = this.fromApiJsonHelper.parse(jsonObject.get("disburse").toString());
        final JsonCommand disburseCommand = JsonCommand.fromExistingCommand(command, elementDisburse);
        final boolean isAccountTransfer = false;
        return this.loanWritePlatformService.disburseLoan(loanId, disburseCommand, isAccountTransfer);
    }

    private void handleDataIntegrityIssues(final DataIntegrityViolationException dve) {
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue", "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }

}
