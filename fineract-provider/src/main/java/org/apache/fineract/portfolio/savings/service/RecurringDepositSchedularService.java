package org.apache.fineract.portfolio.savings.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;

public interface RecurringDepositSchedularService {

    void applyHolidaysToRecurringDeposits(final HolidayDetailDTO holidayDetailDTO, final Map<Long, List<Holiday>> officeIds,
            final Set<Long> failedForOffices, final StringBuilder sb) throws JobExecutionException;
}