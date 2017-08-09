package org.apache.fineract.portfolio.client.service;

import java.util.List;

import org.apache.fineract.portfolio.client.data.ClientRecurringChargeData;
import org.joda.time.LocalDate;

public interface ClientRecurringChargeReadPlatformService {

    List<ClientRecurringChargeData> retrieveRecurringClientCharges(Long clientId);

    ClientRecurringChargeData retriveRecurringClientCharge(Long clientId, Long recurringChargeId);

    List<ClientRecurringChargeData> retriveActiveRecurringChargesForJob(final LocalDate currentDate);
}
