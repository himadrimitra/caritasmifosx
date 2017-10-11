package com.finflux.familydetail.service;

import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.finflux.familydetail.data.FamilyDetailsSummaryDataValidator;
import com.finflux.familydetail.domain.FamilyDetailsSummary;
import com.finflux.familydetail.domain.FamilyDetailsSummaryRepositoryWrapers;

@Service
public class FamilyDetailsSummaryWritePlatformServiceImpl implements FamilyDetailsSummaryWritePlatromService {

    private final static Logger logger = LoggerFactory.getLogger(FamilyDetailsSummaryWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final FamilyDetailsSummaryRepositoryWrapers familyDetailsSummaryRepository;
    private final FamilyDetailsSummaryDataValidator validator;
    private final ClientRepositoryWrapper clientRepository;
    private final FamilyDetailsSummaryDataAssembler assembler;

    @Autowired
    public FamilyDetailsSummaryWritePlatformServiceImpl(final PlatformSecurityContext context,
            final FamilyDetailsSummaryRepositoryWrapers familyDetailsSummaryRepository, final FamilyDetailsSummaryDataValidator validator,
            final ClientRepositoryWrapper clientRepository, final FamilyDetailsSummaryDataAssembler assembler) {
        this.context = context;
        this.familyDetailsSummaryRepository = familyDetailsSummaryRepository;
        this.validator = validator;
        this.clientRepository = clientRepository;
        this.assembler = assembler;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final Long clientId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
            this.validator.validateForCreate(command.json());
            final FamilyDetailsSummary familyDetailsSummary = this.assembler.assembleCreateForm(client, command);
            this.familyDetailsSummaryRepository.save(familyDetailsSummary);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withClientId(client.getId()) //
                    .withEntityId(familyDetailsSummary.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult update(final Long clientId, final Long familyDetailsSummaryId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.clientRepository.findOneWithNotFoundDetection(clientId);
            final FamilyDetailsSummary familyDetailsSummary = this.familyDetailsSummaryRepository
                    .findOneWithNotFoundDetection(familyDetailsSummaryId);
            this.validator.validateForUpdate(command.json());
            final Map<String, Object> changes = familyDetailsSummary.update(command);
            if (!CollectionUtils.isEmpty(changes)) {
                this.familyDetailsSummaryRepository.save(familyDetailsSummary);
            }
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(familyDetailsSummary.getId()) //
                    .withClientId(command.getClientId()) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }

    }

    @Transactional
    @Override
    public CommandProcessingResult delete(final Long clientId, final Long familyDetailsSummaryId) {
        try {
            this.clientRepository.findOneWithNotFoundDetection(clientId);
            final FamilyDetailsSummary familyDetailDelete = this.familyDetailsSummaryRepository
                    .findOneWithNotFoundDetection(familyDetailsSummaryId);
            this.familyDetailsSummaryRepository.delete(familyDetailDelete);
            return new CommandProcessingResultBuilder() //
                    .withEntityId(familyDetailsSummaryId) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }

    @SuppressWarnings("unused")
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("UQ_f_family_details_summary_client_id")) { throw new PlatformDataIntegrityException(
                "error.msg.family.details.summary.already.exist.for.the.client", "Family details summary already exist for the client",
                "clientId"); }
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.family.details.summary.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}