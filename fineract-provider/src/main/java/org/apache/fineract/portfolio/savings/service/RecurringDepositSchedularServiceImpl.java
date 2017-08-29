package org.apache.fineract.portfolio.savings.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.core.exception.ExceptionHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountStatusType;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecurringDepositSchedularServiceImpl implements RecurringDepositSchedularService {

    private final static Logger logger = LoggerFactory.getLogger(RecurringDepositSchedularServiceImpl.class);

    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private final DepositApplicationProcessWritePlatformService depositApplicationProcessWritePlatformService;

    @Autowired
    public RecurringDepositSchedularServiceImpl(final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
            final DepositApplicationProcessWritePlatformService depositApplicationProcessWritePlatformService) {
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.depositApplicationProcessWritePlatformService = depositApplicationProcessWritePlatformService;
    }

    @Override
    public void applyHolidaysToRecurringDeposits(final HolidayDetailDTO holidayDetailDTO, final Map<Long, List<Holiday>> officeIds,
            final Set<Long> failedForOffices, final StringBuilder sb) throws JobExecutionException {
        final String errorMessage = "Apply Holidays for recurring deposit failed for account:";
        final Collection<Integer> savingStatuses = new ArrayList<>(Arrays.asList(
                SavingsAccountStatusType.SUBMITTED_AND_PENDING_APPROVAL.getValue(), SavingsAccountStatusType.APPROVED.getValue(),
                SavingsAccountStatusType.ACTIVE.getValue()));

        for (final Map.Entry<Long, List<Holiday>> entry : officeIds.entrySet()) {
            try {
                final LocalDate recalculateFrom = DateUtils.getLocalDateOfTenant();
                final List<Holiday> holidays = entry.getValue();
                final List<Holiday> applicableHolidays = new ArrayList<>();
                for (final Holiday holiday : holidays) {
                    if (!holiday.getFromDateLocalDate().isBefore(recalculateFrom)) {
                        applicableHolidays.add(holiday);
                    }
                }
                if (!applicableHolidays.isEmpty()) {
                    final Collection<Long> recurringDepositForProcess = this.savingsAccountReadPlatformService
                            .retrieveRecurringDepositsIdByOfficesAndHoliday(entry.getKey(), applicableHolidays, savingStatuses,
                                    recalculateFrom);
                    for (final Long recurringDepositId : recurringDepositForProcess) {
                        try {
                            this.depositApplicationProcessWritePlatformService.updateScheduleDates(recurringDepositId, holidayDetailDTO,
                                    recalculateFrom);
                        } catch (final Exception e) {
                            ExceptionHelper.handleExceptions(e, sb, errorMessage, recurringDepositId, logger);
                            failedForOffices.add(entry.getKey());
                        }
                    }
                }

            } catch (Exception e) {
                final String rootCause = ExceptionHelper.fetchExceptionMessage(e);
                logger.error("Apply Holidays for recurring deposit failed  with message " + rootCause);
                sb.append("Apply Holidays for recurring deposit failed with message ").append(rootCause);
                failedForOffices.add(entry.getKey());
            }
        }
    }
}
