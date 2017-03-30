package com.finflux.mandates.processor;

import com.finflux.mandates.data.MandateProcessCounts;
import com.finflux.mandates.data.MandateTransactionsData;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.domain.MandateProcessStatusEnum;
import com.finflux.mandates.exception.ProcessFailedException;
import com.finflux.mandates.fileformat.TransactionsFileFormatHelper;
import com.finflux.mandates.service.MandatesProcessingReadPlatformService;
import com.finflux.mandates.service.MandatesProcessingStatusPlatformWriteService;
import com.finflux.mandates.service.TransactionsProcessingReadPlatformService;
import com.finflux.mandates.service.TransactionsProcessingWritePlatformService;
import org.apache.fineract.infrastructure.configuration.data.NACHCredentialsData;
import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class TransactionsDownloadProcessor {

        private final MandatesProcessingReadPlatformService mandatesProcessingReadPlatformService;
        private final MandatesProcessingStatusPlatformWriteService mandatesProcessingWritePlatformService;
        private final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService;
        private final ApplicationContext applicationContext;
        private final TransactionsProcessingReadPlatformService transactionsProcessingReadPlatformService;
        private final TransactionsProcessingWritePlatformService transactionsProcessingWritePlatformService;

        @Autowired
        public TransactionsDownloadProcessor(final MandatesProcessingReadPlatformService mandatesProcessingReadPlatformService,
                final MandatesProcessingStatusPlatformWriteService mandatesProcessingWritePlatformService,
                final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService,
                final ApplicationContext applicationContext,
                final TransactionsProcessingReadPlatformService transactionsProcessingReadPlatformService,
                final TransactionsProcessingWritePlatformService transactionsProcessingWritePlatformService){

                this.mandatesProcessingReadPlatformService = mandatesProcessingReadPlatformService;
                this.mandatesProcessingWritePlatformService = mandatesProcessingWritePlatformService;
                this.externalServicesPropertiesReadPlatformService = externalServicesPropertiesReadPlatformService;
                this.applicationContext = applicationContext;
                this.transactionsProcessingReadPlatformService = transactionsProcessingReadPlatformService;
                this.transactionsProcessingWritePlatformService = transactionsProcessingWritePlatformService;
        }

        public void processTransactionDownload(final Long requestId) {
                try{
                        MandatesProcessData processData = this.mandatesProcessingReadPlatformService.retrieveMandateProcessData(requestId);
                        this.mandatesProcessingWritePlatformService.updateProcessStatus(requestId, MandateProcessStatusEnum.INPROCESS, null, null, null, null);

                        int recordsCreated = this.transactionsProcessingWritePlatformService.addTransactionsWithRequestStatus(processData);
                        if(recordsCreated < 1){
                                this.mandatesProcessingWritePlatformService.updateProcessStatus(requestId, MandateProcessStatusEnum.FAILED, null,
                                        "error.no.transactions", "No transactions found for processing", null);
                                return;
                        }
                        Collection<MandateTransactionsData> transactionsToProcess = this.transactionsProcessingReadPlatformService
                                                                .retrieveRequestStatusTransactions(processData);
                        FileData fileData = createDataFile(processData, transactionsToProcess);
                        Document document = this.mandatesProcessingWritePlatformService.saveDocument(fileData.file(), fileData.name(),
                                new Long(fileData.file().available()), fileData.contentType());
                        this.transactionsProcessingWritePlatformService.updateTransactionsStatusAsInProcess(transactionsToProcess);
                        MandateProcessCounts counts = new MandateProcessCounts();
                        counts.setTotalRecords(transactionsToProcess.size());
                        this.mandatesProcessingWritePlatformService.updateProcessStatus(requestId, MandateProcessStatusEnum.PROCESSED, document.getId(), null, null, counts);

                }catch (Exception e){
                        this.mandatesProcessingWritePlatformService.updateProcessStatus(requestId, MandateProcessStatusEnum.FAILED, null,
                                "error.unknown", ""+e.getMessage(), null);
                        throw new ProcessFailedException(e);
                }
        }

        private FileData createDataFile(MandatesProcessData processData, Collection<MandateTransactionsData> transactionsToProcess)
                throws IOException, InvalidFormatException {
                NACHCredentialsData nachProperties = this.externalServicesPropertiesReadPlatformService.getNACHCredentials();
                TransactionsFileFormatHelper formatter = this.applicationContext
                        .getBean(nachProperties.getPROCESSOR_QUALIFIER()+"TransactionsFileFormatHelper",
                                        TransactionsFileFormatHelper.class);
                return formatter.formatDownloadFile(processData, nachProperties, transactionsToProcess);
        }

}
