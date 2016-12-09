package org.apache.fineract.portfolio.cgt.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.cgt.api.CgtApiConstants;
import org.apache.fineract.portfolio.cgt.api.CgtDayApiConstants;
import org.apache.fineract.portfolio.cgt.domain.Cgt;
import org.apache.fineract.portfolio.cgt.domain.CgtDay;
import org.apache.fineract.portfolio.cgt.domain.CgtDayClient;
import org.apache.fineract.portfolio.cgt.domain.CgtDayClientAttendanceStatusType;
import org.apache.fineract.portfolio.cgt.domain.CgtDayRepository;
import org.apache.fineract.portfolio.cgt.domain.CgtRepository;
import org.apache.fineract.portfolio.cgt.domain.CgtStatusType;
import org.apache.fineract.portfolio.cgt.exception.CgtDaysCannotBeGreaterThanMaxCgtException;
import org.apache.fineract.portfolio.cgt.exception.CgtGlobalConfigurationNotEnabledException;
import org.apache.fineract.portfolio.cgt.exception.CgtHasNoClientsException;
import org.apache.fineract.portfolio.cgt.serialization.CgtDayDataValidator;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CgtDayWritePlatformServiceImpl implements CgtDayWritePlatformService {

    private final CgtDayDataValidator cgtDayDataValidator;
    private final CgtRepository cgtRepository;
    private final CgtDayRepository cgtDayRepository;
    private final ConfigurationDomainService configurationDomainService;
    private final StaffRepositoryWrapper staffRepositoryWrapper;
    private final NoteRepository noteRepository;

    @Autowired
    private CgtDayWritePlatformServiceImpl(final CgtDayDataValidator cgtDayDataValidator, final CgtRepository cgtRepository,
            final ConfigurationDomainService configurationDomainService, final CgtDayRepository cgtDayRepository,
            final StaffRepositoryWrapper staffRepositoryWrapper, final NoteRepository noteRepository) {

        this.cgtDayDataValidator = cgtDayDataValidator;
        this.cgtRepository = cgtRepository;
        this.configurationDomainService = configurationDomainService;
        this.cgtDayRepository = cgtDayRepository;
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.noteRepository = noteRepository;

    }

    @Override
    @Transactional
    public CommandProcessingResult createCgtDay(JsonCommand command) {

        this.cgtDayDataValidator.validateForCreateCgtDay(command);
        Long minCgtDays = 1l;
        String cgtDayCreationType = command.stringValueOfParameterNamed(CgtDayApiConstants.cgtDayCreationTypeParamName);
        if (!this.configurationDomainService.isCgtEnabled()) { throw new CgtGlobalConfigurationNotEnabledException(); }
        if (this.configurationDomainService.isMinCgtDaysEnabled() && cgtDayCreationType.equals("startCgtDay")) {
            minCgtDays = this.configurationDomainService.getMinCgtDays();
        }
        Long maxCgtDays = this.configurationDomainService.getMaxCgtDays();
        final Cgt cgt = this.cgtRepository.findOne(command.subentityId());
        final String location = cgt.getLocation();
        final Staff loanOfficer = cgt.getLoanOfficer();
        LocalDate scheduledDate = new LocalDate(cgt.getExpectedStartDate());
        Date completedDate = null;
        int cgtDayCount = this.cgtDayRepository.findByCgtId(command.subentityId()).size();
        if (cgtDayCount >= 1) {
            scheduledDate = scheduledDate.plusDays(cgtDayCount);
        }
        
        if (cgtDayCount >= maxCgtDays) {
            String errorMessage = "CGT Days cannot be created more than " + maxCgtDays + "";
            throw new CgtDaysCannotBeGreaterThanMaxCgtException(maxCgtDays, errorMessage);
        }

        List<CgtDay> cgtDays = new ArrayList<>(minCgtDays.intValue());

        final Set<Client> associatedClientMembers = new HashSet<>(cgt.getClientMembers().size());
        associatedClientMembers.addAll(cgt.getClientMembers());

        List<Note> notes = null;
        CgtDay cgtDay = null;

        for (int i = 0; i < minCgtDays; i++) {
            final String cgtDayName = "CGT_DAY_" + (cgtDayCount + (i + 1));
            cgtDay = CgtDay.newCgtDay(cgt, scheduledDate.toDate(), completedDate, cgtDayName, loanOfficer, location,
                    CgtStatusType.NEW.getValue(), notes);
            scheduledDate = scheduledDate.plusDays(1);
            final Set<CgtDayClient> cgtDayClients = assembleSetOfClientsAttendance(associatedClientMembers, cgtDay,
                    CgtDayClientAttendanceStatusType.PRESENT.getValue());
            cgtDay.updateCgtDayClient(cgtDayClients);
            cgtDays.add(cgtDay);
        }

        final List<CgtDay> cgtDayList = this.cgtDayRepository.save(cgtDays);
        cgt.updateCgtStatus(CgtStatusType.IN_PROGRESS.getValue());
        this.cgtRepository.save(cgt);

        Map<String, Object> responseMap = extractResponseMap(minCgtDays, cgtDayList);

        return new CommandProcessingResultBuilder() //
                .with(responseMap).build();

    }

    @Override
    @Transactional
    public CommandProcessingResult updateCgtDay(JsonCommand command) {

        this.cgtDayDataValidator.validateForUpdateCgtDay(command);
        CgtDay cgtDayEntity = this.cgtDayRepository.findOne(command.entityId());

        final Map<String, Object> changes = cgtDayEntity.updateCgtDay(command);

        if (changes.containsKey(CgtApiConstants.loanOfficerIdParamName)) {
            final Long loanOfficerId = command.longValueOfParameterNamed(CgtApiConstants.loanOfficerIdParamName);
            final Staff newValue = this.staffRepositoryWrapper.findOneWithNotFoundDetection(loanOfficerId);
            cgtDayEntity.updateLoanOfficer(newValue);
        }

        this.cgtDayRepository.save(cgtDayEntity);

        return new CommandProcessingResultBuilder() //
                .withResourceIdAsString(cgtDayEntity.getId().toString()) //
                .with(changes) //
                .build();
    }

    @Override
    @Transactional
    public CommandProcessingResult completeCgtDay(JsonCommand command) {

        this.cgtDayDataValidator.validateForCompleteCgtDay(command);
        CgtDay cgtDayEntity = this.cgtDayRepository.findOne(command.entityId());
        final Map<String, Object> actualChanges = cgtDayEntity.completeCgtDay(command);

        String noteText = null;
        if (command.hasParameter("note")) {
            noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.cgtDayNote(cgtDayEntity, noteText);
                this.noteRepository.save(note);
            }
        }

        this.cgtDayRepository.save(cgtDayEntity);

        return new CommandProcessingResultBuilder() //
                .withResourceIdAsString(cgtDayEntity.getId().toString()) //
                .with(actualChanges) //
                .build();
    }

    private Map<String, Object> extractResponseMap(final Long minCgtDays, final List<CgtDay> cgtDayList) {
        int[] resourceIds = new int[cgtDayList.size()];
        for (int i = 0; i < resourceIds.length; i++) {
            resourceIds[i] = cgtDayList.get(i).getId().intValue();
        }
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("numberOfEntitiesCreated", minCgtDays);
        responseMap.put("resourceIds", resourceIds);
        return responseMap;
    }

    private Set<CgtDayClient> assembleSetOfClientsAttendance(final Set<Client> clients, final CgtDay cgtDay, final Integer attendanceType) {

        final Set<CgtDayClient> clientMembers = new HashSet<>();

        if (!clients.isEmpty()) {
            for (final Client client : clients) {
                if (client.isActive()) {
                    clientMembers.add(CgtDayClient.assembleWithAttendance(client, cgtDay, attendanceType));
                }
            }
        } else {
            String defaultUserMessage = "clients not found for CGT " + cgtDay.getCgt().getUniqueId() + " ";
            throw new CgtHasNoClientsException(defaultUserMessage, cgtDay.getCgt().getUniqueId());
        }

        return clientMembers;
    }

}
