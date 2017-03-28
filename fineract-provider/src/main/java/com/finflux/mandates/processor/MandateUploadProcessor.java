package com.finflux.mandates.processor;

import com.finflux.mandates.data.MandateProcessCounts;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.data.ProcessResponseData;
import com.finflux.mandates.domain.MandateProcessStatusEnum;
import com.finflux.mandates.exception.ProcessFailedException;
import com.finflux.mandates.fileformat.MandatesFileFormatHelper;
import com.finflux.mandates.service.MandatesProcessingReadPlatformService;
import com.finflux.mandates.service.MandatesProcessingStatusPlatformWriteService;
import com.finflux.portfolio.loan.mandate.domain.Mandate;
import com.finflux.portfolio.loan.mandate.domain.MandateRepository;
import org.apache.fineract.infrastructure.configuration.data.NACHCredentialsData;
import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentReadPlatformService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;

@Component
public class MandateUploadProcessor {

        private final static String FAILED = "FAILED";
        private final static String SUCCESS = "SUCCESS";
        private final static String PROCESSED = "PROCESSED";
        private final static String NOT_PROCESSED = "NOT PROCESSED";

        private final MandatesProcessingReadPlatformService mandatesProcessingReadPlatformService;
        private final MandatesProcessingStatusPlatformWriteService mandatesProcessingWritePlatformService;
        private final DocumentReadPlatformService documentReadPlatformService;
        private final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService;
        private final ApplicationContext applicationContext;
        private final MandateRepository mandateRepository;

        @Autowired
        public MandateUploadProcessor(final MandatesProcessingReadPlatformService mandatesProcessingReadPlatformService,
                final MandatesProcessingStatusPlatformWriteService mandatesProcessingWritePlatformService,
                final DocumentReadPlatformService documentReadPlatformService,
                final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService,
                final ApplicationContext applicationContext,
                final MandateRepository mandateRepository){

                this.mandatesProcessingReadPlatformService = mandatesProcessingReadPlatformService;
                this.mandatesProcessingWritePlatformService = mandatesProcessingWritePlatformService;
                this.documentReadPlatformService = documentReadPlatformService;
                this.externalServicesPropertiesReadPlatformService = externalServicesPropertiesReadPlatformService;
                this.applicationContext = applicationContext;
                this.mandateRepository = mandateRepository;
        }

        public void processMandateUpload(final Long requestId){
                try{
                        MandatesProcessData processData = this.mandatesProcessingReadPlatformService.retrieveMandateProcessData(requestId);
                        this.mandatesProcessingWritePlatformService.updateProcessStatus(processData.getId(), MandateProcessStatusEnum.INPROCESS, null, null, null, null);
                        MandateProcessCounts counts = new MandateProcessCounts();
                        processMandatesResponse(processData, counts);
                        this.mandatesProcessingWritePlatformService.updateProcessStatus(processData.getId(), MandateProcessStatusEnum.PROCESSED, null, null, null, counts);
                }catch (Exception e){
                        this.mandatesProcessingWritePlatformService.updateProcessStatus(requestId, MandateProcessStatusEnum.FAILED,
                                null, "error.unknown", ""+e.getMessage(), null);
                        throw new ProcessFailedException(e);
                }

        }

        @Transactional
        public void processMandatesResponse(MandatesProcessData processData, MandateProcessCounts counts) throws IOException, InvalidFormatException {
                FileData fileData = this.documentReadPlatformService.retrieveFileData("mandates", 1L, processData.getDocumentId());
                NACHCredentialsData nachProperties = this.externalServicesPropertiesReadPlatformService.getNACHCredentials();
                MandatesFileFormatHelper formatter = this.applicationContext.getBean(nachProperties.getPROCESSOR_QUALIFIER()+"MandatesFileFormatHelper",
                        MandatesFileFormatHelper.class);
                Collection<ProcessResponseData> responseDatas = formatter.formatMandateResponseData(processData, nachProperties, fileData);
                if(null != responseDatas && responseDatas.size() > 0){
                        for(ProcessResponseData data : responseDatas){
                                updateMandateResponseStatus(data, processData.getId(), counts);
                        }
                }
                FileData processedFileData = formatter.updateProcessStatusToFile(processData, responseDatas, fileData);
                this.mandatesProcessingWritePlatformService.updateDocument(processData, processedFileData);
                counts.setTotalRecords(counts.getSuccessRecords()+counts.getFailedRecords()+counts.getUnprocessedRecords());
        }

        private void updateMandateResponseStatus(final ProcessResponseData data, final Long requestId, MandateProcessCounts counts) {
                Mandate mandate = this.mandateRepository.findOneByLoanAccountNoAndInprocessStatus(data.getReference());
                if(null == mandate){
                        data.setProcessStatus(NOT_PROCESSED, "Couldn't find matching Mandate based on reference");
                        counts.setUnprocessedRecords(counts.getUnprocessedRecords()+1);
                }else{
                        if(data.getStatus().equalsIgnoreCase(FAILED)){
                                mandate.setFailed(data.getFailureReason(), requestId);
                                this.mandateRepository.save(mandate);
                                data.setProcessStatus(PROCESSED,"Mandate Id:"+mandate.getId());
                                counts.setFailedRecords(counts.getFailedRecords()+1);
                        }else if(data.getStatus().equalsIgnoreCase(SUCCESS)){
                                Mandate oldActiveMandate = this.mandateRepository.findOneByLoanAccountNoAndActiveStatus(data.getReference());
                                if(null != oldActiveMandate){
                                        oldActiveMandate.setInactive();
                                        this.mandateRepository.save(oldActiveMandate);
                                }
                                mandate.setSuccess(requestId, data.getUMRN());
                                this.mandateRepository.save(mandate);
                                data.setProcessStatus(PROCESSED,"Mandate Id:"+mandate.getId());
                                counts.setSuccessRecords(counts.getSuccessRecords()+1);
                        }else{
                                data.setProcessStatus(NOT_PROCESSED, "Couldn't parse status column");
                                counts.setUnprocessedRecords(counts.getUnprocessedRecords()+1);
                        }
                }
        }

}
