package com.finflux.risk.existingloans.service;

import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.risk.existingloans.api.ExistingLoanApiConstants;
import com.finflux.risk.existingloans.data.ExistingLoanDataValidator;
import com.finflux.risk.existingloans.domain.ExistingLoan;
import com.finflux.risk.existingloans.domain.ExistingLoanRepositoryWrapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ExistingLoanWritePlatformServiceImp implements ExistingLoanWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ExistingLoanWritePlatformServiceImp.class);

    private final PlatformSecurityContext context;
    private final ExistingLoanAssembler existingLoanAssembler;
    private final ExistingLoanDataValidator existingLoanDataValidator;
    private final ExistingLoanRepositoryWrapper existingLoanRepository;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ExistingLoanWritePlatformServiceImp(final PlatformSecurityContext context, final ExistingLoanAssembler existingLoanAssembler,
            final ExistingLoanDataValidator existingLoanDataValidator, final ExistingLoanRepositoryWrapper existingLoanRepository,
            final CodeValueRepositoryWrapper codeValueRepository, final ClientRepositoryWrapper clientRepository,
            final FromJsonHelper fromApiJsonHelper) {
        this.context = context;
        this.existingLoanAssembler = existingLoanAssembler;
        this.existingLoanDataValidator = existingLoanDataValidator;
        this.existingLoanRepository = existingLoanRepository;
        this.codeValueRepository = codeValueRepository;
        this.clientRepository = clientRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    @Override
    public CommandProcessingResult saveExistingLoan(final Long clientId, final JsonCommand command) {

        this.context.authenticatedUser();

        final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);

        this.existingLoanDataValidator.validateForCreate(command.json());

        final List<ExistingLoan> existingLoans = this.existingLoanAssembler.assembleForSave(client, command);

        this.existingLoanRepository.save(existingLoans);

        return new CommandProcessingResultBuilder() //
                .withEntityId(existingLoans.get(0).getId()) //
                .withCommandId(command.commandId()).build();
    }

    @Transactional
    @Override
    public CommandProcessingResult updateExistingLoan(final Long clientId, final Long existingLoanId, final JsonCommand command) {

        try {

            this.clientRepository.findOneWithNotFoundDetection(clientId);

            final ExistingLoan existingLoanForUpdate = this.existingLoanRepository.findOneWithNotFoundDetection(existingLoanId);

            this.existingLoanDataValidator.validateForUpdate(command.json());

            final Map<String, Object> changes = existingLoanForUpdate.update(command);

            if (changes.containsKey(ExistingLoanApiConstants.sourceCvIdParamName)) {

                final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.sourceCvIdParamName);
                CodeValue sourceCvId = null;
                if (newValue != null) {
                    sourceCvId = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                            ExistingLoanApiConstants.Source_Cv_Option, newValue);
                }
                existingLoanForUpdate.updatesourceCvId(sourceCvId);
            }
            if (changes.containsKey(ExistingLoanApiConstants.loanEnquiryIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.Bureau_Cv_Option);
                CodeValue bureauCvId = null;
                if (newValue != null) {
                    bureauCvId = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                            ExistingLoanApiConstants.Bureau_Cv_Option, newValue);
                }
                // existingLoanForUpdate.updatebureauCvId(bureauCvId);
            }

            if (changes.containsKey(ExistingLoanApiConstants.externalLoanPurposeCvIdParamName)) {
                CodeValue externalLoanPurposeCvId = null;
                final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.ExternalLoan_Purpose_Option);
                if (newValue != null) {
                    externalLoanPurposeCvId = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                            ExistingLoanApiConstants.ExternalLoan_Purpose_Option, newValue);
                }
                existingLoanForUpdate.updateExternalLoanPurpose(externalLoanPurposeCvId);
            }
            if (changes.containsKey(ExistingLoanApiConstants.loanTypeCvIdParamName)) {
                CodeValue loanType = null;
                final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.LoanType_Cv_Option);
                if (newValue != null) {
                    loanType = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                            ExistingLoanApiConstants.LoanType_Cv_Option, newValue);
                }
                existingLoanForUpdate.updateloanType(loanType);
            }

            if (!changes.isEmpty()) {
                this.existingLoanRepository.saveAndFlush(existingLoanForUpdate);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withClientId(existingLoanForUpdate.getClientId()) //
                    .withEntityId(existingLoanForUpdate.getId()) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @SuppressWarnings("unused")
    @Transactional
    @Override
    public CommandProcessingResult deleteExistingLoan(final Long clientId, final Long existingLoanId) {

        final ExistingLoan existingLoan = this.existingLoanRepository.findOneWithNotFoundDetection(existingLoanId);

        this.existingLoanRepository.delete(existingLoan);

        return new CommandProcessingResultBuilder() //
                .withEntityId(existingLoan.getId()) //
                .build();
    }

    @Override
    @SuppressWarnings("null")
    public void createOrUpdateExistingLoans(final Long clientId, final JsonCommand command) {
        this.clientRepository.findOneWithNotFoundDetection(clientId);
        final JsonElement elements = this.fromApiJsonHelper.parse(command.jsonFragment(ExistingLoanApiConstants.existingLoansParamName));
        final JsonArray array = elements.getAsJsonArray();
        if (array != null && array.size() > 0) {
            final JsonObject createJsonObject = new JsonObject();
            final JsonArray createJsonArray = new JsonArray();
            for (int i = 0; i < array.size(); i++) {
                final JsonObject jsonObject = array.get(i).getAsJsonObject();
                if (jsonObject.has(ExistingLoanApiConstants.idParamName)) {
                    /**
                     * Call Update Service
                     */
                    final JsonElement element = this.fromApiJsonHelper.parse(jsonObject.toString());
                    final JsonCommand newCommand = JsonCommand.fromExistingCommand(command, element);
                    final Long id = this.fromApiJsonHelper.extractLongNamed(ExistingLoanApiConstants.idParamName, element);
                    updateExistingLoan(clientId, id, newCommand);
                } else {
                    final JsonElement element = this.fromApiJsonHelper.parse(jsonObject.toString());
                    createJsonArray.add(element);
                }
            }
            if (createJsonArray != null && createJsonArray.size() > 0) {
                createJsonObject.add(ExistingLoanApiConstants.existingLoansParamName, createJsonArray);
                final JsonElement element = this.fromApiJsonHelper.parse(createJsonObject.toString());
                final JsonCommand newCommand = JsonCommand.fromExistingCommand(command, element);
                /**
                 * Call Create Service
                 */
                saveExistingLoan(clientId, newCommand);
            }
        }
    }

    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}
