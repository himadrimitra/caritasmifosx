package com.finflux.mandates.service;

import com.finflux.mandates.api.MandatesProcessingApiConstants;
import com.finflux.mandates.data.MandateProcessCounts;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.data.MandatesProcessDataValidator;
import com.finflux.mandates.domain.MandateProcessStatusEnum;
import com.finflux.mandates.domain.MandateProcessTypeEnum;
import com.finflux.mandates.exception.MandateAlreadyUnderProcessingException;
import com.finflux.mandates.exception.NoMandatesToBeProcessedException;
import com.finflux.mandates.exception.UnableToReadFileException;
import com.finflux.mandates.processor.MandatesAsyncProcessingHelper;
import com.finflux.portfolio.loan.mandate.data.MandateData;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.infrastructure.documentmanagement.domain.DocumentRepository;
import org.apache.fineract.infrastructure.documentmanagement.domain.StorageType;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MandatesProcessingWritePlatformServiceImpl implements MandatesProcessingWritePlatformService {

        private final JdbcTemplate jdbcTemplate;
        private final PlatformSecurityContext context;
        private final MandatesProcessingReadPlatformService readPlatformService;
        private final MandatesProcessDataValidator validator;
        private final MandatesAsyncProcessingHelper helper;
        private final ContentRepositoryFactory contentRepositoryFactory;
        private final DocumentRepository documentRepository;
        final SimpleDateFormat yyyyMMddFormatter = new SimpleDateFormat("yyyy-MM-dd");

        @Autowired
        public MandatesProcessingWritePlatformServiceImpl(final PlatformSecurityContext context,
                final RoutingDataSource dataSource,
                final MandatesProcessingReadPlatformService readPlatformService,
                final MandatesProcessDataValidator validator,
                final MandatesAsyncProcessingHelper helper,
                final ContentRepositoryFactory contentRepositoryFactory,
                final DocumentRepository documentRepository){

                this.context = context;
                this.jdbcTemplate = new JdbcTemplate(dataSource);
                this.readPlatformService = readPlatformService;
                this.validator = validator;
                this.helper = helper;
                this.contentRepositoryFactory = contentRepositoryFactory;
                this.documentRepository = documentRepository;
        }

        @Override
        public CommandProcessingResult downloadMandates(final JsonCommand command) {
                this.validator.validateMandatesDownload(command.json());
                final Long officeId = command.longValueOfParameterNamed(MandatesProcessingApiConstants.officeId);
                final boolean includeChildOffices = command.booleanPrimitiveValueOfParameterNamed(MandatesProcessingApiConstants.includeChildOffices);
                final boolean includeMandateScans = command.booleanPrimitiveValueOfParameterNamed(MandatesProcessingApiConstants.includeMandateScans);

                final boolean pendingRequestsExists = this.readPlatformService
                        .pendingMandateDownloadProcessExists(officeId);
                if(pendingRequestsExists){
                        throw new MandateAlreadyUnderProcessingException();
                }

                final boolean pendingMandatesExists = this.readPlatformService
                        .pendingMandateRequestsExists(officeId, includeChildOffices);
                if(!pendingMandatesExists){
                        throw new NoMandatesToBeProcessedException();
                }

                final Map<String, Object> parameters = new HashMap<>();
                parameters.put("request_date", yyyyMMddFormatter.format(new Date()));
                parameters.put("process_type",MandateProcessTypeEnum.MANDATES_DOWNLOAD.getValue());
                parameters.put("process_status", MandateProcessStatusEnum.REQUESTED.getValue());
                parameters.put("office_id", officeId);
                parameters.put("include_child_offices", includeChildOffices);
                parameters.put("include_mandate_scans", includeMandateScans);

                final Long requestId = insertMandateRequest(parameters);
                this.helper.processMandateDownload(requestId);

                return new CommandProcessingResultBuilder()
                        .withCommandId(command.commandId())
                        .withEntityId(requestId)
                        .build();
        }

        @Override
        public CommandProcessingResult uploadMandates(final JsonCommand command) {
                FormDataMultiPart multiPart = command.getFormDataMultiPart();
                if(null != multiPart){
                        final AppUser user = this.context.authenticatedUser();
                        final Map<String, Object> parameters = new HashMap<>();
                        parameters.put("request_date", yyyyMMddFormatter.format(new Date()));
                        parameters.put("process_type",MandateProcessTypeEnum.MANDATES_UPLOAD.getValue());
                        parameters.put("process_status", MandateProcessStatusEnum.REQUESTED.getValue());
                        parameters.put("office_id", user.getOffice().getId());
                        parameters.put("include_child_offices", true);
                        final Document document = saveDocument(multiPart);
                        parameters.put("document_id", document.getId());

                        final Long requestId = insertMandateRequest(parameters);
                        this.helper.processMandateUpload(requestId);

                        return new CommandProcessingResultBuilder()
                                .withCommandId(command.commandId())
                                .withEntityId(requestId)
                                .build();
                } else {
                        throw new UnableToReadFileException();
                }
        }

        @Override
        public CommandProcessingResult downloadTransactions(final JsonCommand command) {
                this.validator.validateTransactionsDownload(command.json());
                final Long officeId = command.longValueOfParameterNamed(MandatesProcessingApiConstants.officeId);
                final boolean includeChildOffices = command.booleanPrimitiveValueOfParameterNamed(MandatesProcessingApiConstants.includeChildOffices);
                final LocalDate startDate = command.localDateValueOfParameterNamed(MandatesProcessingApiConstants.paymentDueStartDate);
                final LocalDate endDate = command.localDateValueOfParameterNamed(MandatesProcessingApiConstants.paymentDueEndDate);
                final String paymentDueStartDate = yyyyMMddFormatter.format(startDate.toDate());
                final String paymentDueEndDate = yyyyMMddFormatter.format(endDate.toDate());
                final String[] includeFailedTransactions = command.arrayValueOfParameterNamed(MandatesProcessingApiConstants.includeFailedTransactions);

                final boolean pendingRequestsExists = this.readPlatformService
                        .pendingTransactionsDownloadProcessExists(officeId);
                if(pendingRequestsExists){
                        throw new MandateAlreadyUnderProcessingException();
                }

                final Map<String, Object> parameters = new HashMap<>();
                parameters.put("request_date", yyyyMMddFormatter.format(new Date()));
                parameters.put("process_type",MandateProcessTypeEnum.TRANSACTIONS_DOWNLOAD.getValue());
                parameters.put("process_status", MandateProcessStatusEnum.REQUESTED.getValue());
                parameters.put("office_id", officeId);
                parameters.put("include_child_offices", includeChildOffices);
                parameters.put("payment_due_start_date", paymentDueStartDate);
                parameters.put("payment_due_end_date", paymentDueEndDate);
                parameters.put("include_failed_transactions", StringUtils.join(includeFailedTransactions, ','));

                final Long requestId = insertMandateRequest(parameters);
                this.helper.processTransactionsDownload(requestId);

                return new CommandProcessingResultBuilder()
                        .withCommandId(command.commandId())
                        .withEntityId(requestId)
                        .build();
        }

        @Override
        public CommandProcessingResult uploadTransactions(final JsonCommand command) {
                FormDataMultiPart multiPart = command.getFormDataMultiPart();
                if(null != multiPart){
                        final AppUser user = this.context.authenticatedUser();
                        final Map<String, Object> parameters = new HashMap<>();
                        parameters.put("request_date", yyyyMMddFormatter.format(new Date()));
                        parameters.put("process_type",MandateProcessTypeEnum.TRANSACTIONS_UPLOAD.getValue());
                        parameters.put("process_status", MandateProcessStatusEnum.REQUESTED.getValue());
                        parameters.put("office_id", user.getOffice().getId());
                        parameters.put("include_child_offices", true);
                        final Document document = saveDocument(multiPart);
                        parameters.put("document_id", document.getId());

                        final Long requestId = insertMandateRequest(parameters);
                        this.helper.processTransactionsUpload(requestId);

                        return new CommandProcessingResultBuilder()
                                .withCommandId(command.commandId())
                                .withEntityId(requestId)
                                .build();
                } else {
                        throw new UnableToReadFileException();
                }
        }

        private Long insertMandateRequest(final Map<String, Object> parameters) {
                final SimpleJdbcInsert insertTemplate = new SimpleJdbcInsert(this.jdbcTemplate)
                        .withTableName("f_mandates_process").usingGeneratedKeyColumns("id");
                final Number key = insertTemplate.executeAndReturnKey(parameters);
                return key.longValue();
        }

        private Document saveDocument(FormDataMultiPart formParams) {
                final Set<String> modifiedParameters = null;
                final Long id = null;
                final String parentEntityType = "mandates";
                final Long parentEntityId = 1L;
                final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
                final FormDataBodyPart bodyPart = formParams.getField("file");
                final InputStream inputStream = bodyPart.getEntityAs(InputStream.class);
                final String name = bodyPart.getFormDataContentDisposition().getFileName();
                final Long size = new Long(formParams.getField("fileSize").getValue());
                final String type = bodyPart.getMediaType().toString();
                final String location = null;

                final DocumentCommand documentCommand = new DocumentCommand(modifiedParameters, id, parentEntityType, parentEntityId,
                        name, name, size, type, name, location);
                final String fileLocation = contentRepository.saveFile(inputStream, documentCommand);
                final Document document = Document.createNew(documentCommand.getParentEntityType(), documentCommand.getParentEntityId(),
                        documentCommand.getName(), documentCommand.getFileName(), documentCommand.getSize(), documentCommand.getType(),
                        documentCommand.getDescription(), fileLocation, contentRepository.getStorageType());

                this.documentRepository.save(document);
                return document;
        }

}
