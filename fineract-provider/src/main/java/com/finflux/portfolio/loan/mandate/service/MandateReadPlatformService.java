package com.finflux.portfolio.loan.mandate.service;

import com.finflux.mandates.data.MandatesSummaryData;
import com.finflux.portfolio.loan.mandate.data.MandateData;
import org.apache.fineract.infrastructure.core.service.Page;

import java.util.Collection;
import java.util.Date;

public interface MandateReadPlatformService {

        MandateData retrieveTemplate(Long loanId, String commandParam);

        Collection<MandateData> retrieveMandates(Long loanId);

        Collection<MandateData> retrieveRequestedMandates(Long officeId, Boolean includeChildOffices);

        Collection<MandateData> retrieveMandatesWithStatus(Long loanId, Integer[] statuses);

        MandateData retrieveActiveMandate(Long loanId);

        MandateData retrieveMandate(Long loanId, Long mandateId);

        Collection<MandatesSummaryData> retrieveMandateSummary(Long officeId, Boolean includeChildOffices, Date fromDate, Date toDate);

        Page<MandateData> retrieveAllMandates(Long officeId, Boolean includeChildOffices, Date fromDate, Date toDate, Integer offset, Integer limit);
}
