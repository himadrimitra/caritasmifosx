package org.apache.fineract.portfolio.client.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.portfolio.client.data.ClientRecurringChargeData;
import org.joda.time.LocalDate;

public interface ClientRecurringChargeReadPlatformService {

    List<ClientRecurringChargeData> retrieveRecurringClientCharges(Long clientId);

    ClientRecurringChargeData retriveRecurringClientCharge(Long clientId, Long recurringChargeId);

    List<ClientRecurringChargeData> retriveActiveRecurringChargesForJob(final LocalDate currentDate);

    Collection<Map<String, Object>> retrieveClientRecurringChargeIdByOfficesAndHoliday(final Long officeId, final List<Holiday> holidays,
            final Collection<Integer> chargeTimeTypes, final LocalDate recalculateFrom);
}
