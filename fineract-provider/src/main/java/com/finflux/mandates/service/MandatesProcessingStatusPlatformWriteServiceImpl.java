package com.finflux.mandates.service;

import com.finflux.mandates.data.MandateProcessCounts;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.domain.MandateProcessStatusEnum;
import com.finflux.portfolio.loan.mandate.data.MandateData;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.infrastructure.documentmanagement.domain.DocumentRepository;
import org.apache.fineract.infrastructure.documentmanagement.domain.StorageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class MandatesProcessingStatusPlatformWriteServiceImpl implements MandatesProcessingStatusPlatformWriteService {

        private final JdbcTemplate jdbcTemplate;
        private final ContentRepositoryFactory contentRepositoryFactory;
        private final DocumentRepository documentRepository;

        @Autowired
        public MandatesProcessingStatusPlatformWriteServiceImpl(final RoutingDataSource dataSource,
                final ContentRepositoryFactory contentRepositoryFactory,
                final DocumentRepository documentRepository){

                this.jdbcTemplate = new JdbcTemplate(dataSource);
                this.contentRepositoryFactory = contentRepositoryFactory;
                this.documentRepository = documentRepository;
        }

        @Override
        public void updateMandateStatusAsInProcess(Collection<MandateData> mandatesToProcess) {
                String sql = "update f_loan_mandates as f set f.mandate_status_enum = ? where f.id = ?";
                List<Object[]> params = new ArrayList<>();
                for(MandateData data : mandatesToProcess){
                        params.add(new Object[]{data.getMandateStatus().getId().intValue()+1, data.getId()});
                }
                this.jdbcTemplate.batchUpdate(sql, params);
        }

        @Override
        public void updateProcessStatus(final Long requestId, final MandateProcessStatusEnum status, final Long documentId,
                final String failureReasonCode, final String failureReasonDesc, final MandateProcessCounts counts) {
                String failureReason = null;
                if(null == failureReasonDesc){
                        failureReason = "";
                }else{
                        failureReason = failureReasonDesc.replaceAll("'","#");
                }
                if(failureReason.length() > 99){
                        failureReason = failureReason.substring(0,99);
                }

                StringBuilder sql = new StringBuilder(" update f_mandates_process as f ")
                        .append(" set f.process_status = ").append(status.getValue());
                if(null != documentId){
                        sql.append(" , f.document_id = ").append(documentId);
                }
                if(null != failureReasonCode){
                        sql.append(" , f.failed_reason_code = '").append(failureReasonCode).append("' ");
                }
                if(null != failureReasonDesc){
                        sql.append(" , f.failed_reason_desc = '").append(failureReason).append("' ");
                }
                if(null != counts){
                        sql.append(" , f.total_records = ").append(counts.getTotalRecords())
                                .append(" , f.success_records = ").append(counts.getSuccessRecords())
                                .append(" , f.failed_records = ").append(counts.getFailedRecords())
                                .append(" , f.unprocessed_records = ").append(counts.getUnprocessedRecords());                }
                sql.append(" where f.id = ").append(requestId);

                this.jdbcTemplate.update(sql.toString());
        }

        @Override
        public Document saveDocument(final InputStream inputStream, final String name, final Long size, final String type) {
                final Set<String> modifiedParameters = null;
                final Long id = null;
                final String parentEntityType = "mandates";
                final Long parentEntityId = 1L;
                final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
                final String location = null;
                final Long reportIdentifier = null ;
                final Long tagIdentifier = null ;
                final DocumentCommand documentCommand = new DocumentCommand(modifiedParameters, id, parentEntityType, parentEntityId,
                        name, name, size, type, name, location, reportIdentifier, tagIdentifier);
                final String fileLocation = contentRepository.saveFile(inputStream, documentCommand, false);
                final Document document = Document.createNew(documentCommand.getParentEntityType(), documentCommand.getParentEntityId(),
                        documentCommand.getName(), documentCommand.getFileName(), documentCommand.getSize(), documentCommand.getType(),
                        documentCommand.getDescription(), fileLocation, contentRepository.getStorageType());

                this.documentRepository.save(document);
                return document;
        }

        @Override
        public void updateDocument(final MandatesProcessData processData, final FileData fileData) throws IOException {
                final Document documentForUpdate = this.documentRepository.findOne(processData.getDocumentId());
                final StorageType documentStoreType = documentForUpdate.storageType();
                String oldLocation = documentForUpdate.getLocation();
                final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
                final Long reportIdentifier = null ;
                final Long tagIdentifier = null ;
                final DocumentCommand documentCommand = new DocumentCommand(null, documentForUpdate.getId(),
                        "mandates", 1L, documentForUpdate.getName(), documentForUpdate.getFileName(),
                        (long) fileData.file().available(), fileData.contentType(),
                        documentForUpdate.getDescription(), documentForUpdate.getLocation(),reportIdentifier, tagIdentifier);

                documentCommand.setLocation(contentRepository.saveFile(fileData.file(), documentCommand));
                documentForUpdate.setLocation(documentCommand.getLocation());
                contentRepository.deleteFile(documentCommand.getName(), oldLocation);

                this.documentRepository.saveAndFlush(documentForUpdate);
        }


}
