package org.apache.fineract.portfolio.cgt.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.cgt.api.CgtApiConstants;
import org.apache.fineract.portfolio.cgt.domain.Cgt;
import org.apache.fineract.portfolio.cgt.domain.CgtRepository;
import org.apache.fineract.portfolio.cgt.domain.CgtStatusType;
import org.apache.fineract.portfolio.cgt.exception.CgtCannotBeCreatedException;
import org.apache.fineract.portfolio.cgt.exception.CgtHasNoClientsException;
import org.apache.fineract.portfolio.cgt.serialization.CgtDataValidator;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
public class CgtWritePlatformServiceImpl implements CgtWritePlatformService {

    private final CgtDataValidator cgtDataValidator;
    private final PlatformSecurityContext context;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final CgtRepository cgtRepository;
    private final NoteRepository noteRepository;
    private final StaffRepositoryWrapper staffRepositoryWrapper;

    @Autowired
    private CgtWritePlatformServiceImpl(final CgtDataValidator cgtDataValidator, final PlatformSecurityContext context,
            final ClientRepositoryWrapper clientRepositoryWrapper, final CgtRepository cgtRepository, final NoteRepository noteRepository,
            final StaffRepositoryWrapper staffRepositoryWrapper) {

        this.cgtDataValidator = cgtDataValidator;
        this.context = context;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.cgtRepository = cgtRepository;
        this.noteRepository = noteRepository;
        this.staffRepositoryWrapper = staffRepositoryWrapper;
    }

    @Override
    @Transactional
    public CommandProcessingResult createCgt(final JsonCommand command) {

    	this.cgtDataValidator.validateForCreateCgt(command);
    	
        final Integer entityId = command.integerValueOfParameterNamed(CgtApiConstants.entityIdParamName);
        final Integer entityType = command.integerValueOfParameterNamed(CgtApiConstants.entityTypeParamName);

        final List<Cgt> activeOrInprogresscgts = this.cgtRepository.findActiveOrInProgressCgts(CgtStatusType.NEW.getValue(),
                CgtStatusType.IN_PROGRESS.getValue(), entityId, entityType);
        if (!activeOrInprogresscgts.isEmpty()) { throw new CgtCannotBeCreatedException(
                "CGT cannot be created until all existing CGT are in completed state"); }

        final String location = command.stringValueOfParameterNamed(CgtApiConstants.locationParamName);
        final Long loanOfficerId = command.longValueOfParameterNamed(CgtApiConstants.loanOfficerIdParamName);
        final LocalDate expectedStartDate = command.localDateValueOfParameterNamed(CgtApiConstants.expectedStartDateParamName);
        final LocalDate expectedEndDate = command.localDateValueOfParameterNamed(CgtApiConstants.expectedEndDateParamName);

        final Set<Client> activeClients = this.assembleSetOfClients(command, entityId.toString());
        final String uniqueId = MathUtility.randomNameGenerator("CGT_", 9);
        //final Date actualStartDate = DateUtils.getLocalDateTimeOfTenant().toDate();
        final Date actualStartDate = null;
        final Staff staff = this.staffRepositoryWrapper.findOneWithNotFoundDetection(loanOfficerId);
        Set<Note> notes = new HashSet<>(1);
        Cgt cgtEntity = Cgt.newCgt(uniqueId, expectedStartDate.toDate(), expectedEndDate.toDate(), actualStartDate, activeClients,
                entityType, entityId, CgtStatusType.NEW.getValue(), location, staff, notes);

        String noteText = null;
        if (command.hasParameter("note")) {
            noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.cgtNote(cgtEntity, noteText);
                notes.add(note);
            }
        }

        this.cgtRepository.save(cgtEntity);
        this.noteRepository.save(notes);

        return new CommandProcessingResultBuilder() //
                .withResourceIdAsString(cgtEntity.getId().toString()).build();

    }

    @Override
    @Transactional
    public CommandProcessingResult updateCgt(final JsonCommand command) {

        this.cgtDataValidator.validateForUpdateCgt(command);
        Cgt cgtEntity = this.cgtRepository.findOne(command.entityId());

        final Map<String, Object> changes = cgtEntity.updateCgt(command);

        if (changes.containsKey(CgtApiConstants.loanOfficerIdParamName)) {
            final Long loanOfficerId = command.longValueOfParameterNamed(CgtApiConstants.loanOfficerIdParamName);
            final Staff newValue = this.staffRepositoryWrapper.findOneWithNotFoundDetection(loanOfficerId);
            cgtEntity.updateLoanOfficer(newValue);
        }

        if (changes.containsKey(CgtApiConstants.clientIdsParamName)) {
            final Set<Client> activeClients = this.assembleSetOfClients(command, command.entityId().toString());
            cgtEntity.updateAssociatedClients(activeClients);
        }

        String noteText = null;
        if (command.hasParameter("note")) {
            noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.cgtNote(cgtEntity, noteText);
                cgtEntity.getNotes().add(note);
            }
        }

        this.cgtRepository.save(cgtEntity);

        return new CommandProcessingResultBuilder() //
                .withResourceIdAsString(cgtEntity.getId().toString()) //
                .with(changes) //
                .build();
    }

    @Override
    @Transactional
    public CommandProcessingResult rejectCgt(final JsonCommand command) {

        this.cgtDataValidator.validateForRejectCgt(command);
        Cgt cgtEntity = this.cgtRepository.findOne(command.entityId());
        final Map<String, Object> actualChanges = cgtEntity.rejectCgt(command);

        String noteText = null;
        if (command.hasParameter("note")) {
            noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.cgtNote(cgtEntity, noteText);
                cgtEntity.updateNotes(note);
            }
        }
        cgtEntity.updateCgtStatus(CgtStatusType.REJECT.getValue());
        this.cgtRepository.save(cgtEntity);

        return new CommandProcessingResultBuilder() //
                .withResourceIdAsString(cgtEntity.getId().toString()) //
                .with(actualChanges) //
                .build();
    }

    @Override
    @Transactional
    public CommandProcessingResult completeCgt(final JsonCommand command) {

        this.cgtDataValidator.validateForCompleteCgt(command);
        Cgt cgtEntity = this.cgtRepository.findOne(command.entityId());
        final Map<String, Object> actualChanges = cgtEntity.completeCgt(command);

        String noteText = null;
        if (command.hasParameter("note")) {
            noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.cgtNote(cgtEntity, noteText);
                cgtEntity.updateNotes(note);
            }
        }
        cgtEntity.updateCgtStatus(CgtStatusType.COMPLETE.getValue());
        this.cgtRepository.save(cgtEntity);

        return new CommandProcessingResultBuilder() //
                .withResourceIdAsString(cgtEntity.getId().toString()) //
                .with(actualChanges) //
                .build();
    }

    private Set<Client> assembleSetOfClients(final JsonCommand command, String entityId) {

        final Set<Client> clientMembers = new HashSet<>();
        final String[] clientMembersArray = command.arrayValueOfParameterNamed(CgtApiConstants.clientIdsParamName);

        if (!ObjectUtils.isEmpty(clientMembersArray)) {
            for (final String clientId : clientMembersArray) {
                final Long id = Long.valueOf(clientId);
                final Client client = this.clientRepositoryWrapper.findOneWithNotFoundDetection(id);
                if (client.isActive()) {
                    clientMembers.add(client);
                }
                clientMembers.add(client);
            }
        } else {
            String defaultUserMessage = "clients not found for CGT  " + entityId + " ";
            throw new CgtHasNoClientsException(defaultUserMessage, entityId);
        }

        return clientMembers;
    }

}
