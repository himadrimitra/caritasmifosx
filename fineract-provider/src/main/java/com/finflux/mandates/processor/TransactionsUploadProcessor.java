package com.finflux.mandates.processor;

import com.finflux.mandates.data.MandateProcessCounts;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.domain.MandateProcessStatusEnum;
import com.finflux.mandates.exception.ProcessFailedException;
import com.finflux.mandates.service.MandatesProcessingReadPlatformService;
import com.finflux.mandates.service.MandatesProcessingStatusPlatformWriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionsUploadProcessor {

        private final MandatesProcessingReadPlatformService mandatesProcessingReadPlatformService;
        private final MandatesProcessingStatusPlatformWriteService mandatesProcessingWritePlatformService;
        private final RepaymentsProcessor repaymentsProcessor;

        @Autowired
        public TransactionsUploadProcessor(final MandatesProcessingReadPlatformService mandatesProcessingReadPlatformService,
                final MandatesProcessingStatusPlatformWriteService mandatesProcessingWritePlatformService,
                final RepaymentsProcessor repaymentsProcessor){

                this.mandatesProcessingReadPlatformService = mandatesProcessingReadPlatformService;
                this.mandatesProcessingWritePlatformService = mandatesProcessingWritePlatformService;
                this.repaymentsProcessor = repaymentsProcessor;
        }

        public void processTransactionUpload(final Long requestId) {
                try{
                        MandatesProcessData processData = this.mandatesProcessingReadPlatformService.retrieveMandateProcessData(requestId);
                        this.mandatesProcessingWritePlatformService.updateProcessStatus(processData.getId(), MandateProcessStatusEnum.INPROCESS, null, null, null, null);
                        MandateProcessCounts counts = new MandateProcessCounts();
                        this.repaymentsProcessor.processTransactionsResponse(processData, counts);
                        this.mandatesProcessingWritePlatformService.updateProcessStatus(processData.getId(), MandateProcessStatusEnum.PROCESSED, null, null, null, counts);
                }catch (Exception e){
                        this.mandatesProcessingWritePlatformService.updateProcessStatus(requestId, MandateProcessStatusEnum.FAILED,
                                null, "error.unknown", ""+e.getMessage(), null);
                        throw new ProcessFailedException(e);
                }
        }
}
