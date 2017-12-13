/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.documentmanagement.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.dataqueries.service.ReadReportingService;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommandValidator;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.data.DocumentTagData;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.infrastructure.documentmanagement.domain.DocumentRepositoryWrapper;
import org.apache.fineract.infrastructure.documentmanagement.domain.StorageType;
import org.apache.fineract.infrastructure.documentmanagement.exception.ContentManagementException;
import org.apache.fineract.infrastructure.documentmanagement.exception.DocumentReportMappingNotfoundException;
import org.apache.fineract.infrastructure.documentmanagement.exception.InvalidEntityTypeForDocumentManagementException;
import org.apache.fineract.infrastructure.report.provider.ReportingProcessServiceProvider;
import org.apache.fineract.infrastructure.report.service.ReportingProcessService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.common.util.FinfluxCollectionUtils;

@Service
public class DocumentWritePlatformServiceJpaRepositoryImpl implements DocumentWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(DocumentWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final DocumentRepositoryWrapper documentRepository;
    private final ContentRepositoryFactory contentRepositoryFactory;
    private final ReadReportingService readExtraDataAndReportingService;
    private final ReportingProcessServiceProvider reportingProcessServiceProvider;
    private final DocumentReadPlatformService documentReadPlatformService;
    private final DateFormat fileNameGeneratedateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
    private final BusinessEventNotifierService businessEventNotifierService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DocumentWritePlatformServiceJpaRepositoryImpl(final RoutingDataSource dataSource, final PlatformSecurityContext context,
            final DocumentRepositoryWrapper documentRepository, final ContentRepositoryFactory documentStoreFactory,
            final ReadReportingService readExtraDataAndReportingService,
            final ReportingProcessServiceProvider reportingProcessServiceProvider,
            final DocumentReadPlatformService documentReadPlatformService,
            final BusinessEventNotifierService businessEventNotifierService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.context = context;
        this.documentRepository = documentRepository;
        this.contentRepositoryFactory = documentStoreFactory;
        this.readExtraDataAndReportingService = readExtraDataAndReportingService;
        this.reportingProcessServiceProvider = reportingProcessServiceProvider;
        this.documentReadPlatformService = documentReadPlatformService;
        this.businessEventNotifierService = businessEventNotifierService;
    }

    @Transactional
    @Override
    public Long createDocument(final DocumentCommand documentCommand, final InputStream inputStream) {
        try {
            this.context.authenticatedUser();

            final DocumentCommandValidator validator = new DocumentCommandValidator(documentCommand);

            validateParentEntityType(documentCommand);

            final String parentEntityType = documentCommand.getParentEntityType();
            final Long parentEntityId = documentCommand.getParentEntityId();
            validateEntityLockedOrNot(parentEntityType, parentEntityId);

            validator.validateForCreate();

            final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();

            final String fileLocation = contentRepository.saveFile(inputStream, documentCommand);

            final Document document = Document.createNew(documentCommand.getParentEntityType(), documentCommand.getParentEntityId(),
                    documentCommand.getName(), documentCommand.getFileName(), documentCommand.getSize(), documentCommand.getType(),
                    documentCommand.getDescription(), fileLocation, contentRepository.getStorageType());
            if (documentCommand.getReportIdentifier() != null) {
                document.setReportIdentifier(documentCommand.getReportIdentifier());
            }
            if (documentCommand.getTagIdentifier() != null) {
                document.setTagIdentifier(documentCommand.getTagIdentifier());
            }
            this.documentRepository.save(document);

            return document.getId();
        } catch (final DataIntegrityViolationException dve) {
            logger.error(dve.getMessage(), dve);
            throw new PlatformDataIntegrityException("error.msg.document.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.");
        }
    }

    private void validateEntityLockedOrNot(final String entityType, final Long entityId) {
        final DOCUMENT_MANAGEMENT_ENTITY documentEntityType = DOCUMENT_MANAGEMENT_ENTITY.fromString(entityType);
        String sqlQueryForEntityLockedOrNot = null;
        if (documentEntityType.getName().equalsIgnoreCase(DOCUMENT_MANAGEMENT_ENTITY.CLIENTS.getName())) {
            sqlQueryForEntityLockedOrNot = "select c.is_locked from m_client c where c.id = ? ";
        } else if (documentEntityType.getName().equalsIgnoreCase(DOCUMENT_MANAGEMENT_ENTITY.LOANS.getName())) {
            sqlQueryForEntityLockedOrNot = "select l.is_locked from m_loan l where l.id = ? ";
        }
        if (sqlQueryForEntityLockedOrNot != null) {
            try {
                final boolean isLocked = this.jdbcTemplate.queryForObject(sqlQueryForEntityLockedOrNot, Boolean.class, entityId);
                this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.DOCUMENT_ADD,
                        FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, isLocked));
            } catch (final EmptyResultDataAccessException e) {}
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateDocument(final DocumentCommand documentCommand, final InputStream inputStream) {
        try {
            this.context.authenticatedUser();

            String oldLocation = null;
            final DocumentCommandValidator validator = new DocumentCommandValidator(documentCommand);
            validator.validateForUpdate();
            // TODO check if entity id is valid and within data scope for the
            // user
            final Document documentForUpdate = this.documentRepository.findOneWithNotFoundDetection(documentCommand.getParentEntityType(),
                    documentCommand.getParentEntityId(), documentCommand.getId());
            this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.DOCUMENT_UPDATE,
                    FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, documentForUpdate.isLocked()));
            final StorageType documentStoreType = documentForUpdate.storageType();
            oldLocation = documentForUpdate.getLocation();
            if (inputStream != null && documentCommand.isFileNameChanged()) {
                final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
                documentCommand.setLocation(contentRepository.saveFile(inputStream, documentCommand));
                documentCommand.setStorageType(contentRepository.getStorageType().getValue());
            }

            documentForUpdate.update(documentCommand);

            if (inputStream != null && documentCommand.isFileNameChanged()) {
                final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository(documentStoreType);
                contentRepository.deleteFile(documentCommand.getName(), oldLocation);
            }

            this.documentRepository.saveAndFlush(documentForUpdate);

            return new CommandProcessingResult(documentForUpdate.getId());
        } catch (final DataIntegrityViolationException dve) {
            logger.error(dve.getMessage(), dve);
            throw new PlatformDataIntegrityException("error.msg.document.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.");
        } catch (final ContentManagementException cme) {
            logger.error(cme.getMessage(), cme);
            throw new ContentManagementException(documentCommand.getName(), cme.getMessage());
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteDocument(final DocumentCommand documentCommand) {
        this.context.authenticatedUser();

        validateParentEntityType(documentCommand);
        // TODO: Check document is present under this entity Id
        final Document document = this.documentRepository.findOneWithNotFoundDetection(documentCommand.getParentEntityType(),
                documentCommand.getParentEntityId(), documentCommand.getId());
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.DOCUMENT_DELETE,
                FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, document.isLocked()));
        this.documentRepository.delete(document);

        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository(document.storageType());
        contentRepository.deleteFile(document.getName(), document.getLocation());
        return new CommandProcessingResult(document.getId());
    }

    private void validateParentEntityType(final DocumentCommand documentCommand) {
        if (!checkValidEntityType(documentCommand.getParentEntityType())) { throw new InvalidEntityTypeForDocumentManagementException(
                documentCommand.getParentEntityType()); }
    }

    private static boolean checkValidEntityType(final String entityType) {
        for (final DOCUMENT_MANAGEMENT_ENTITY entities : DOCUMENT_MANAGEMENT_ENTITY.values()) {
            if (entities.name().equalsIgnoreCase(entityType)) { return true; }
        }
        return false;
    }

    /*** Entities for document Management **/
    public static enum DOCUMENT_MANAGEMENT_ENTITY {
        CLIENTS("clients"), //
        CLIENT_IDENTIFIERS("client_identifiers"), //
        STAFF("staff"), //
        LOANS("loans"), //
        SAVINGS("savings"), //
        GROUPS("groups"), //
        BANKSTATEMENT("bankstatement"), //
        TASKS("tasks"), //
        VOUCHERS("vouchers"), //
        OFFICES("offices"), //
        DISTRICTS("districts"), //
        VILLAGES("villages"), //
        CENTERS("centers"), //
        LOANAPPLICATION("loanapplication"), //
        INVESTMENT("investment");

        private final String name;

        DOCUMENT_MANAGEMENT_ENTITY(final String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static DOCUMENT_MANAGEMENT_ENTITY fromString(final String type) {
            DOCUMENT_MANAGEMENT_ENTITY entityType = null;
            if (type != null) {
                switch (type) {
                    case "clients":
                        entityType = CLIENTS;
                    break;
                    case "client_identifiers":
                        entityType = CLIENT_IDENTIFIERS;
                    break;
                    case "staff":
                        entityType = STAFF;
                    break;
                    case "loans":
                        entityType = LOANS;
                    break;
                    case "savings":
                        entityType = SAVINGS;
                    break;
                    case "groups":
                        entityType = GROUPS;
                    break;
                    case "bankstatement":
                        entityType = BANKSTATEMENT;
                    break;
                    case "tasks":
                        entityType = TASKS;
                    break;
                    case "vouchers":
                        entityType = VOUCHERS;
                    break;
                    case "offices":
                        entityType = OFFICES;
                    break;
                    case "districts":
                        entityType = DISTRICTS;
                    break;
                    case "villages":
                        entityType = VILLAGES;
                    break;
                    case "centers":
                        entityType = CENTERS;
                    break;
                    case "loanapplication":
                        entityType = LOANAPPLICATION;
                    break;
                    case "investment":
                        entityType = INVESTMENT;
                    break;
                }
            }
            return entityType;
        }

        @Override
        public String toString() {
            return name().toString().toLowerCase();
        }
    }

    @Override
    public Long generateDocument(final String entityType, final Long entityId, final Long reportIdetifier,
            final MultivaluedMap<String, String> reportParams) {
        final DocumentTagData tagData = this.documentReadPlatformService.retrieveDocumentTagData(reportIdetifier);
        if (tagData == null) { throw new DocumentReportMappingNotfoundException(); }
        Long documentId = null;
        final String reportName = tagData.getReportName();
        final String fileName = reportName + this.fileNameGeneratedateFormat.format(DateUtils.getLocalDateTimeOfTenant().toDate()) + "."
                + tagData.getOutputType();
        final String description = reportName;
        final Long fileSize = null;
        final String type = "application/" + tagData.getOutputType();
        addRequestParams(tagData.getReportCategory(), reportParams, entityId, tagData.getOutputType());
        final DocumentCommand documentCommand = new DocumentCommand(null, null, entityType, entityId, reportName, fileName, fileSize, type,
                description, null, reportIdetifier, tagData.getTagId());
        final String reportType = this.readExtraDataAndReportingService.getReportType(reportName);
        final ReportingProcessService reportingProcessService = this.reportingProcessServiceProvider
                .findReportingProcessService(reportType);
        Response response = null;
        if (reportingProcessService != null) {
            response = reportingProcessService.processRequest(reportName, reportParams);
        }

        if (response != null) {
            final byte[] data = (byte[]) response.getEntity();
            if (data != null) {
                final ByteArrayInputStream stream = new ByteArrayInputStream(data);
                documentId = createDocument(documentCommand, stream);
            }
        }
        return documentId;
    }

    private void addRequestParams(final String reportCategory, final MultivaluedMap<String, String> reportParams, final Long entityId,
            final String outputType) {
        final List<String> list = new ArrayList<>();
        list.add(entityId.toString());
        if ("Loan".equalsIgnoreCase(reportCategory)) {
            reportParams.put("R_loanId", list);
        } else if ("Client".equalsIgnoreCase(reportCategory)) {
            reportParams.put("R_clientId", list);
        } else if ("Savings".equalsIgnoreCase(reportCategory)) {
            reportParams.put("R_savingsId", list);
        } else if ("LoanApplication".equalsIgnoreCase(reportCategory)) {
            reportParams.put("R_loanApplicationId", list);
        } else if ("Staff".equalsIgnoreCase(reportCategory)) {
            reportParams.put("R_staffId", list);
        } else if ("Group".equalsIgnoreCase(reportCategory)) {
            reportParams.put("R_groupId", list);
        } else if ("Task".equalsIgnoreCase(reportCategory)) {
            reportParams.put("R_taskId", list);
        } else if ("Office".equalsIgnoreCase(reportCategory)) {
            reportParams.put("R_officeId", list);
        } else if ("District".equalsIgnoreCase(reportCategory)) {
            reportParams.put("R_districtId", list);
        } else if ("Village".equalsIgnoreCase(reportCategory)) {
            reportParams.put("R_villageId", list);
        } else if ("Center".equalsIgnoreCase(reportCategory)) {
            reportParams.put("R_centerId", list);
        } else {
            final String globalisationMessageCode = "error.msg.document.generate.invalid.report.category";
            final String defaultUserMessage = "Invalid report category `" + reportCategory + "` for document generation";
            final List<String> categories = Arrays.asList("Loan", "Client", "Savings", "LoanApplication", "Staff", "Group", "Task",
                    "Office", "District", "Village", "Center");
            throw new GeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, categories);
        }
        final List<String> output = new ArrayList<>();
        output.add(outputType);
        reportParams.put("output-type", output);
    }

    @Override
    @Transactional
    public Long reGenerateDocument(final String entityType, final Long entityId, final Long identifier,
            final MultivaluedMap<String, String> reportParams) {
        final Document document = this.documentRepository.findOneWithNotFoundDetection(entityType, entityId, identifier);
        this.documentRepository.delete(document);
        // We don't remove the previously created document (To maintain
        // history?)
        return generateDocument(entityType, entityId, document.getReportIdentifier(), reportParams);
    }
}