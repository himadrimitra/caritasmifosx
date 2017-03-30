package com.finflux.mandates.service;

import com.finflux.mandates.data.MandateProcessCounts;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.domain.MandateProcessStatusEnum;
import com.finflux.portfolio.loan.mandate.data.MandateData;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface MandatesProcessingStatusPlatformWriteService {
        void updateMandateStatusAsInProcess(Collection<MandateData> mandatesToProcess);

        void updateProcessStatus(Long requestId, MandateProcessStatusEnum status, Long documentId, String failureReasonCode,
                String failureReasonDesc, MandateProcessCounts counts);

        Document saveDocument(InputStream inputStream, String name, Long size, String type);

        void updateDocument(MandatesProcessData processData, FileData fileData) throws IOException;

}
