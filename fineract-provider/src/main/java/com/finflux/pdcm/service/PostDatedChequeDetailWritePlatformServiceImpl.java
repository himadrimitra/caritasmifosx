package com.finflux.pdcm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.ExceptionHelper;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.pdcm.constants.PostDatedChequeDetailApiConstants;
import com.finflux.pdcm.data.PostDatedChequeDetailService;
import com.finflux.pdcm.domain.PostDatedChequeDetail;
import com.finflux.pdcm.domain.PostDatedChequeDetailsRepositoryWrapper;

@Service
public class PostDatedChequeDetailWritePlatformServiceImpl implements PostDatedChequeDetailWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(PostDatedChequeDetailWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final PostDatedChequeDetailService service;
    private final PostDatedChequeDetailsRepositoryWrapper repository;

    @Autowired
    public PostDatedChequeDetailWritePlatformServiceImpl(final PlatformSecurityContext context, final PostDatedChequeDetailService service,
            final PostDatedChequeDetailsRepositoryWrapper repository) {
        this.context = context;
        this.service = service;
        this.repository = repository;
    }

    @Override
    public CommandProcessingResult create(final Integer entityType, final Long entityId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();

            final List<PostDatedChequeDetail> postDatedChequeDetails = this.service.validateAndCreate(entityType, entityId, command.json());

            this.repository.save(postDatedChequeDetails);

            final Map<String, Object> changes = new HashMap<>();
            final List<Long> postDatedChequeDetailIds = new ArrayList<>();
            for (final PostDatedChequeDetail postDatedChequeDetail : postDatedChequeDetails) {
                postDatedChequeDetailIds.add(postDatedChequeDetail.getId());
            }
            changes.put(PostDatedChequeDetailApiConstants.resourceIds, postDatedChequeDetailIds);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .with(changes).build();
        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult update(final Long pdcId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();

            final PostDatedChequeDetail postDatedChequeDetail = this.repository.findOneWithNotFoundDetection(pdcId);

            final Map<String, Object> changes = this.service.validateAndUpdate(postDatedChequeDetail, command);

            this.repository.save(postDatedChequeDetail);

            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(pdcId)//
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    /**
     * Soft delete
     */
    @Override
    public CommandProcessingResult delete(final Long pdcId, final JsonCommand command) {
        final PostDatedChequeDetail postDatedChequeDetail = this.repository.findOneWithNotFoundDetection(pdcId);
        this.service.validateAndDelete(postDatedChequeDetail);
        this.repository.save(postDatedChequeDetail);
        return new CommandProcessingResultBuilder()//
                .withCommandId(command.commandId())//
                .withEntityId(pdcId)//
                .build();
    }

    @Override
    public CommandProcessingResult presentPDC(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.service.validateBulkOperationsOnPDC(command);
            final List<PostDatedChequeDetail> postDatedChequeDetails = processForPresentPDC(command);
            final Map<String, Object> changes = new HashMap<>();
            final List<Long> postDatedChequeDetailIds = new ArrayList<>();
            final List<Long> transactionIds = new ArrayList<>();
            final List<Integer> statusIds = new ArrayList<>();
            for (final PostDatedChequeDetail postDatedChequeDetail : postDatedChequeDetails) {
                postDatedChequeDetailIds.add(postDatedChequeDetail.getId());
                transactionIds.add(postDatedChequeDetail.getPostDatedChequeDetailMapping().getTransactionId());
                statusIds.add(postDatedChequeDetail.getPresentStatus());
            }
            changes.put(PostDatedChequeDetailApiConstants.resourceIds, postDatedChequeDetailIds);
            changes.put(PostDatedChequeDetailApiConstants.transactionIds, transactionIds);
            changes.put(PostDatedChequeDetailApiConstants.statusIds, statusIds);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .with(changes).build();
        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }

    @SuppressWarnings("null")
    public List<PostDatedChequeDetail> processForPresentPDC(final JsonCommand command) {
        final List<PostDatedChequeDetail> postDatedChequeDetails = new ArrayList<>();
        final LocalDate presentedDate = command.localDateValueOfParameterNamed(PostDatedChequeDetailApiConstants.dateParamName);
        final String presentedDescription = command.stringValueOfParameterNamed(PostDatedChequeDetailApiConstants.descriptionParamName);
        final String[] pdcChequeDetails = command.arrayValueOfParameterNamed(PostDatedChequeDetailApiConstants.pdcChequeDetailsParamName);
        final long[] pdcChequeDetailsArray = Arrays.stream(pdcChequeDetails).mapToLong(Long::parseLong).toArray();
        final String transactionDate = command.stringValueOfParameterNamed(PostDatedChequeDetailApiConstants.dateParamName);
        final String validationErrorCode = "error.msg.present.pdc.failed.for.cheque.number";
        String defaultEnglishMessage = "Present PDC failed for cheque number ";
        final List<ApiParameterError> validationErrors = new ArrayList<>();
        for (final Long pdcId : pdcChequeDetailsArray) {
            PostDatedChequeDetail postDatedChequeDetail = null;
            try {
                postDatedChequeDetail = this.repository.findOneWithNotFoundDetection(pdcId);
                this.service.processForPresentPDC(command, postDatedChequeDetail, presentedDate, presentedDescription, transactionDate);
                postDatedChequeDetails.add(postDatedChequeDetail);
            } catch (Exception e) {
                defaultEnglishMessage += postDatedChequeDetail.getChequeNumber();
                constructPDCActionProcessErrorObjects(e, validationErrors, validationErrorCode, defaultEnglishMessage,
                        postDatedChequeDetail);
            }
        }
        this.service.throwExceptionIfDomainValidationWarningsExist(validationErrors);
        return postDatedChequeDetails;
    }

    private void constructPDCActionProcessErrorObjects(final Exception e, final List<ApiParameterError> validationErrors,
            final String validationErrorCode, final String defaultEnglishMessage, final PostDatedChequeDetail postDatedChequeDetail) {
        final StringBuilder sb = new StringBuilder();
        ExceptionHelper.handleExceptions(e, sb, validationErrorCode, postDatedChequeDetail.getId(), logger);
        final ApiParameterError error = ApiParameterError.parameterError(validationErrorCode, defaultEnglishMessage,
                PostDatedChequeDetailApiConstants.chequeNumberParamName, postDatedChequeDetail.getChequeNumber());
        validationErrors.add(error);
    }

    @Override
    public CommandProcessingResult bouncedPDC(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.service.validateBulkOperationsOnPDC(command);
            final List<PostDatedChequeDetail> postDatedChequeDetails = processForBouncedPDC(command);
            final Map<String, Object> changes = new HashMap<>();
            final List<Integer> statusIds = new ArrayList<>();
            for (final PostDatedChequeDetail postDatedChequeDetail : postDatedChequeDetails) {
                statusIds.add(postDatedChequeDetail.getPresentStatus());
            }
            changes.put(PostDatedChequeDetailApiConstants.statusIds, statusIds);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .with(changes).build();
        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }

    @SuppressWarnings("null")
    public List<PostDatedChequeDetail> processForBouncedPDC(final JsonCommand command) {
        final List<PostDatedChequeDetail> postDatedChequeDetails = new ArrayList<>();
        final LocalDate bouncedDate = command.localDateValueOfParameterNamed(PostDatedChequeDetailApiConstants.dateParamName);
        final String bouncedDescription = command.stringValueOfParameterNamed(PostDatedChequeDetailApiConstants.descriptionParamName);
        final String[] pdcChequeDetails = command.arrayValueOfParameterNamed(PostDatedChequeDetailApiConstants.pdcChequeDetailsParamName);
        final String transactionDate = command.stringValueOfParameterNamed(PostDatedChequeDetailApiConstants.dateParamName);
        final long[] pdcChequeDetailsArray = Arrays.stream(pdcChequeDetails).mapToLong(Long::parseLong).toArray();
        final String validationErrorCode = "error.msg.bounced.pdc.failed.for.cheque.number";
        String defaultEnglishMessage = "Bounced PDC failed for cheque number ";
        final List<ApiParameterError> validationErrors = new ArrayList<>();
        for (final Long pdcId : pdcChequeDetailsArray) {
            PostDatedChequeDetail postDatedChequeDetail = null;
            try {
                postDatedChequeDetail = this.repository.findOneWithNotFoundDetection(pdcId);
                this.service.processForBouncedPDC(command, postDatedChequeDetail, bouncedDate, bouncedDescription, transactionDate);
                postDatedChequeDetails.add(postDatedChequeDetail);
            } catch (Exception e) {
                defaultEnglishMessage += postDatedChequeDetail.getChequeNumber();
                constructPDCActionProcessErrorObjects(e, validationErrors, validationErrorCode, defaultEnglishMessage,
                        postDatedChequeDetail);
            }
        }
        this.service.throwExceptionIfDomainValidationWarningsExist(validationErrors);
        return postDatedChequeDetails;
    }

    @Override
    public CommandProcessingResult clearPDC(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.service.validateBulkOperationsOnPDC(command);
            final List<PostDatedChequeDetail> postDatedChequeDetails = processForClearPDC(command);
            final Map<String, Object> changes = new HashMap<>();
            final List<Integer> statusIds = new ArrayList<>();
            for (final PostDatedChequeDetail postDatedChequeDetail : postDatedChequeDetails) {
                statusIds.add(postDatedChequeDetail.getPresentStatus());
            }
            changes.put(PostDatedChequeDetailApiConstants.statusIds, statusIds);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .with(changes).build();
        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }

    @SuppressWarnings("null")
    public List<PostDatedChequeDetail> processForClearPDC(final JsonCommand command) {
        final List<PostDatedChequeDetail> postDatedChequeDetails = new ArrayList<>();
        final LocalDate clearedDate = command.localDateValueOfParameterNamed(PostDatedChequeDetailApiConstants.dateParamName);
        final String clearedDescription = command.stringValueOfParameterNamed(PostDatedChequeDetailApiConstants.descriptionParamName);
        final String[] pdcChequeDetails = command.arrayValueOfParameterNamed(PostDatedChequeDetailApiConstants.pdcChequeDetailsParamName);
        final long[] pdcChequeDetailsArray = Arrays.stream(pdcChequeDetails).mapToLong(Long::parseLong).toArray();
        final String validationErrorCode = "error.msg.clear.pdc.failed.for.cheque.number";
        String defaultEnglishMessage = "Clear PDC failed for cheque number ";
        final List<ApiParameterError> validationErrors = new ArrayList<>();
        for (final Long pdcId : pdcChequeDetailsArray) {
            PostDatedChequeDetail postDatedChequeDetail = null;
            try {
                postDatedChequeDetail = this.repository.findOneWithNotFoundDetection(pdcId);
                this.service.processForClearPDC(postDatedChequeDetail, clearedDate, clearedDescription);
                postDatedChequeDetails.add(postDatedChequeDetail);
            } catch (Exception e) {
                defaultEnglishMessage += postDatedChequeDetail.getChequeNumber();
                constructPDCActionProcessErrorObjects(e, validationErrors, validationErrorCode, defaultEnglishMessage,
                        postDatedChequeDetail);
            }
        }
        this.service.throwExceptionIfDomainValidationWarningsExist(validationErrors);
        return postDatedChequeDetails;
    }

    @Override
    public CommandProcessingResult cancelPDC(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.service.validateBulkOperationsOnPDC(command);
            final List<PostDatedChequeDetail> postDatedChequeDetails = processForCancelPDC(command);
            final Map<String, Object> changes = new HashMap<>();
            final List<Integer> statusIds = new ArrayList<>();
            for (final PostDatedChequeDetail postDatedChequeDetail : postDatedChequeDetails) {
                statusIds.add(postDatedChequeDetail.getPresentStatus());
            }
            changes.put(PostDatedChequeDetailApiConstants.statusIds, statusIds);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .with(changes).build();
        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }

    @SuppressWarnings("null")
    public List<PostDatedChequeDetail> processForCancelPDC(final JsonCommand command) {
        final List<PostDatedChequeDetail> postDatedChequeDetails = new ArrayList<>();
        final LocalDate cancelledDate = command.localDateValueOfParameterNamed(PostDatedChequeDetailApiConstants.dateParamName);
        final String cancelledDescription = command.stringValueOfParameterNamed(PostDatedChequeDetailApiConstants.descriptionParamName);
        final String[] pdcChequeDetails = command.arrayValueOfParameterNamed(PostDatedChequeDetailApiConstants.pdcChequeDetailsParamName);
        final long[] pdcChequeDetailsArray = Arrays.stream(pdcChequeDetails).mapToLong(Long::parseLong).toArray();
        final String validationErrorCode = "error.msg.cancel.pdc.failed.for.cheque.number";
        String defaultEnglishMessage = "Cancel PDC failed for cheque number ";
        final List<ApiParameterError> validationErrors = new ArrayList<>();
        for (final Long pdcId : pdcChequeDetailsArray) {
            PostDatedChequeDetail postDatedChequeDetail = null;
            try {
                postDatedChequeDetail = this.repository.findOneWithNotFoundDetection(pdcId);
                this.service.processForCancelPDC(postDatedChequeDetail, cancelledDate, cancelledDescription);
                postDatedChequeDetails.add(postDatedChequeDetail);
            } catch (Exception e) {
                defaultEnglishMessage += postDatedChequeDetail.getChequeNumber();
                constructPDCActionProcessErrorObjects(e, validationErrors, validationErrorCode, defaultEnglishMessage,
                        postDatedChequeDetail);
            }
        }
        this.service.throwExceptionIfDomainValidationWarningsExist(validationErrors);
        return postDatedChequeDetails;
    }

    @Override
    public CommandProcessingResult returnPDC(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.service.validateBulkOperationsOnPDC(command);
            final List<PostDatedChequeDetail> postDatedChequeDetails = processForReturnPDC(command);
            final Map<String, Object> changes = new HashMap<>();
            final List<Integer> statusIds = new ArrayList<>();
            for (final PostDatedChequeDetail postDatedChequeDetail : postDatedChequeDetails) {
                statusIds.add(postDatedChequeDetail.getPresentStatus());
            }
            changes.put(PostDatedChequeDetailApiConstants.statusIds, statusIds);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .with(changes).build();
        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }

    @SuppressWarnings("null")
    public List<PostDatedChequeDetail> processForReturnPDC(final JsonCommand command) {
        final List<PostDatedChequeDetail> postDatedChequeDetails = new ArrayList<>();
        final LocalDate returnedDate = command.localDateValueOfParameterNamed(PostDatedChequeDetailApiConstants.dateParamName);
        final String returnedDescription = command.stringValueOfParameterNamed(PostDatedChequeDetailApiConstants.descriptionParamName);
        final String[] pdcChequeDetails = command.arrayValueOfParameterNamed(PostDatedChequeDetailApiConstants.pdcChequeDetailsParamName);
        final long[] pdcChequeDetailsArray = Arrays.stream(pdcChequeDetails).mapToLong(Long::parseLong).toArray();
        final String validationErrorCode = "error.msg.return.pdc.failed.for.cheque.number";
        String defaultEnglishMessage = "Return PDC failed for cheque number ";
        final List<ApiParameterError> validationErrors = new ArrayList<>();
        for (final Long pdcId : pdcChequeDetailsArray) {
            PostDatedChequeDetail postDatedChequeDetail = null;
            try {
                postDatedChequeDetail = this.repository.findOneWithNotFoundDetection(pdcId);
                this.service.processForReturnPDC(postDatedChequeDetail, returnedDate, returnedDescription);
                postDatedChequeDetails.add(postDatedChequeDetail);
            } catch (Exception e) {
                defaultEnglishMessage += postDatedChequeDetail.getChequeNumber();
                constructPDCActionProcessErrorObjects(e, validationErrors, validationErrorCode, defaultEnglishMessage,
                        postDatedChequeDetail);
            }
        }
        this.service.throwExceptionIfDomainValidationWarningsExist(validationErrors);
        return postDatedChequeDetails;
    }

    @Override
    public CommandProcessingResult undoPDC(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.service.validateUndoPDC(command);
            final List<PostDatedChequeDetail> postDatedChequeDetails = processForUndoPDC(command);
            this.repository.save(postDatedChequeDetails);
            final Map<String, Object> changes = new HashMap<>();
            final List<Integer> statusIds = new ArrayList<>();
            for (final PostDatedChequeDetail postDatedChequeDetail : postDatedChequeDetails) {
                statusIds.add(postDatedChequeDetail.getPresentStatus());
            }
            changes.put(PostDatedChequeDetailApiConstants.statusIds, statusIds);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .with(changes).build();
        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }

    @SuppressWarnings("null")
    public List<PostDatedChequeDetail> processForUndoPDC(final JsonCommand command) {
        final List<PostDatedChequeDetail> postDatedChequeDetails = new ArrayList<>();
        final String[] pdcChequeDetails = command.arrayValueOfParameterNamed(PostDatedChequeDetailApiConstants.pdcChequeDetailsParamName);
        final long[] pdcChequeDetailsArray = Arrays.stream(pdcChequeDetails).mapToLong(Long::parseLong).toArray();
        final String validationErrorCode = "error.msg.undo.pdc.failed.for.cheque.number";
        String defaultEnglishMessage = "Undo PDC failed for cheque number ";
        final List<ApiParameterError> validationErrors = new ArrayList<>();
        for (final Long pdcId : pdcChequeDetailsArray) {
            PostDatedChequeDetail postDatedChequeDetail = null;
            try {
                postDatedChequeDetail = this.repository.findOneWithNotFoundDetection(pdcId);
                this.service.processForUndoPDC(command, postDatedChequeDetail);
                postDatedChequeDetails.add(postDatedChequeDetail);
            } catch (Exception e) {
                defaultEnglishMessage += postDatedChequeDetail.getChequeNumber();
                constructPDCActionProcessErrorObjects(e, validationErrors, validationErrorCode, defaultEnglishMessage,
                        postDatedChequeDetail);
            }
        }
        this.service.throwExceptionIfDomainValidationWarningsExist(validationErrors);
        return postDatedChequeDetails;
    }

    @SuppressWarnings("unused")
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.loan.purpose.groupping.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}
