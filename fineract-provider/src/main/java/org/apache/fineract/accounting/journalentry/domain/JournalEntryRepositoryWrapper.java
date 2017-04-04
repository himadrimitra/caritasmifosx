package org.apache.fineract.accounting.journalentry.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Component;

@Component
public class JournalEntryRepositoryWrapper extends AbstractPersistable<Long> {

    private final JournalEntryRepository journalEntryRepository;
    private final AccountRunningComputationDetailRepository accountRunningComputationDetailRepository;

    @Autowired
    public JournalEntryRepositoryWrapper(final JournalEntryRepository journalEntryRepository,
            final AccountRunningComputationDetailRepository accountRunningComputationDetailRepository) {
        this.journalEntryRepository = journalEntryRepository;
        this.accountRunningComputationDetailRepository = accountRunningComputationDetailRepository;
    }

    public void save(final JournalEntry journalEntry) {
        this.journalEntryRepository.save(journalEntry);
        updateAccountRunningComputationDetail(journalEntry);
    }

    public void save(final Collection<JournalEntry> journalEntries) {
        this.journalEntryRepository.save(journalEntries);
        for (JournalEntry journalEntry : journalEntries) {
            updateAccountRunningComputationDetail(journalEntry);
        }
    }

    private void updateAccountRunningComputationDetail(final JournalEntry journalEntry) {

        Set<Long> accountIds = new HashSet<>();
        for (JournalEntryDetail journalEntryDetail : journalEntry.getJournalEntryDetails()) {
            accountIds.add(journalEntryDetail.getGlAccount().getId());
        }
        Long[] accountIdsAsArray = new Long[accountIds.size()];
        accountIds.toArray(accountIdsAsArray);
        List<AccountRunningComputationDetail> computationDetails = this.accountRunningComputationDetailRepository
                .fetchAccountRunningComputationDetail(journalEntry.getOfficeId(), accountIdsAsArray);
        for (AccountRunningComputationDetail computationDetail : computationDetails) {
            accountIds.remove(computationDetail.getGlAccountId());
            if (!journalEntry.getEffectiveDateAsLocalDate().isAfter(computationDetail.getComputedTillDateAsLocalDate())) {
                computationDetail.setComputedTillDate(journalEntry.getEffectiveDateAsLocalDate().minusDays(1).toDate());
            }
        }

        for (Long accountId : accountIds) {
            computationDetails.add(new AccountRunningComputationDetail(journalEntry.getOfficeId(), accountId, journalEntry
                    .getEffectiveDateAsLocalDate().minusDays(1).toDate(),journalEntry.getCurrencyCode()));
        }
        this.accountRunningComputationDetailRepository.save(computationDetails);

    }

}
