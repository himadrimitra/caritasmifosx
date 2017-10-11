package com.finflux.mandates.service;

import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.domain.MandateProcessStatusEnum;
import com.finflux.mandates.domain.MandateProcessTypeEnum;

import java.util.Collection;
import java.util.Date;

public interface MandatesProcessingReadPlatformService {

        MandatesProcessData retrieveMandateDownloadTemplate();

        MandatesProcessData retrieveTransactionsDownloadTemplate();

        MandatesProcessData retrieveMandateProcessData(Long id);

        Collection<MandatesProcessData> retrieveMandates(MandateProcessTypeEnum type, Date requestDate, Long officeId);

        Collection<MandatesProcessData> retrieveMandatesWithStatus(Integer[] status);

        boolean pendingMandateDownloadProcessExists(Long officeId);

        boolean pendingMandateRequestsExists(Long officeId, boolean includeChildOffices);

        boolean pendingTransactionsDownloadProcessExists(Long officeId);
}
