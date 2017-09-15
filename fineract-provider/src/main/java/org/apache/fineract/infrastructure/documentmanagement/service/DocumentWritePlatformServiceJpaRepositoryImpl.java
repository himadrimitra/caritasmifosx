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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentWritePlatformServiceJpaRepositoryImpl implements DocumentWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(DocumentWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final DocumentRepositoryWrapper documentRepository;
    private final ContentRepositoryFactory contentRepositoryFactory;
    private final ReadReportingService readExtraDataAndReportingService;
    private final ReportingProcessServiceProvider reportingProcessServiceProvider;
    private final DocumentReadPlatformService documentReadPlatformService ;
    private final DateFormat fileNameGeneratedateFormat = new SimpleDateFormat("ddMMyyyyHHmmss") ; 
    
    @Autowired
    public DocumentWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final DocumentRepositoryWrapper documentRepository, final ContentRepositoryFactory documentStoreFactory,
            final ReadReportingService readExtraDataAndReportingService,
            final ReportingProcessServiceProvider reportingProcessServiceProvider,
            final DocumentReadPlatformService documentReadPlatformService) {
        this.context = context;
        this.documentRepository = documentRepository;
        this.contentRepositoryFactory = documentStoreFactory;
        this.readExtraDataAndReportingService = readExtraDataAndReportingService ;
        this.reportingProcessServiceProvider = reportingProcessServiceProvider ;
        this.documentReadPlatformService = documentReadPlatformService ;
    }

    @Transactional
    @Override
    public Long createDocument(final DocumentCommand documentCommand, final InputStream inputStream) {
        try {
            this.context.authenticatedUser();

            final DocumentCommandValidator validator = new DocumentCommandValidator(documentCommand);

            validateParentEntityType(documentCommand);

            validator.validateForCreate();

            final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();

            final String fileLocation = contentRepository.saveFile(inputStream, documentCommand);

            final Document document = Document.createNew(documentCommand.getParentEntityType(), documentCommand.getParentEntityId(),
                    documentCommand.getName(), documentCommand.getFileName(), documentCommand.getSize(), documentCommand.getType(),
                    documentCommand.getDescription(), fileLocation, contentRepository.getStorageType());
            if(documentCommand.getReportIdentifier() != null) {
                document.setReportIdentifier(documentCommand.getReportIdentifier()); 
            }
            if(documentCommand.getTagIdentifier() != null) {
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
        CLIENTS, CLIENT_IDENTIFIERS, STAFF, LOANS, SAVINGS, GROUPS, BANKSTATEMENT, TASKS, VOUCHERS, OFFICES, DISTRICTS, VILLAGES, CENTERS, LOANAPPLICATION;

        @Override
        public String toString() {
            return name().toString().toLowerCase();
        }
    }

    @Override
    public Long generateDocument(String entityType, Long entityId, Long reportIdetifier,
            final MultivaluedMap<String, String> reportParams) {
        final DocumentTagData tagData = documentReadPlatformService.retrieveDocumentTagData(reportIdetifier) ;
        if(tagData == null) throw new DocumentReportMappingNotfoundException() ;
        Long documentId = null ;
        String reportName = tagData.getReportName();
        final String fileName = reportName +fileNameGeneratedateFormat.format(DateUtils.getLocalDateTimeOfTenant().toDate()) +"."+tagData.getOutputType();
        final String description = reportName;
        final Long fileSize = null;
        final String type = "application/"+tagData.getOutputType();
        addRequestParams(tagData.getReportCategory(), reportParams, entityId, tagData.getOutputType()); 
        final DocumentCommand documentCommand = new DocumentCommand(null, null, entityType, entityId, reportName, fileName, fileSize, type,
                description, null, reportIdetifier, tagData.getTagId());
        String reportType = this.readExtraDataAndReportingService.getReportType(reportName);
        ReportingProcessService reportingProcessService = this.reportingProcessServiceProvider.findReportingProcessService(reportType);
        Response response = null ;
        if (reportingProcessService != null) {
            response = reportingProcessService.processRequest(reportName, reportParams);
        }
        
        if(response != null) {
            byte[] data = (byte[])response.getEntity() ; 
            if(data != null) {
                ByteArrayInputStream stream = new ByteArrayInputStream(data) ;
                documentId = this.createDocument(documentCommand, stream) ;
            }
        }
        return documentId ;
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
        List<String> output = new ArrayList<>();
        output.add(outputType);
        reportParams.put("output-type", output);
    }

    @Override
    @Transactional
    public Long reGenerateDocument(String entityType, Long entityId, Long identifier, MultivaluedMap<String, String> reportParams) {
        final Document document = this.documentRepository.findOneWithNotFoundDetection(entityType, entityId, identifier) ; 
        this.documentRepository.delete(document); 
        //We don't remove the previously created document (To maintain history?)  
        return this.generateDocument(entityType, entityId, document.getReportIdentifier(), reportParams) ;
    }
}