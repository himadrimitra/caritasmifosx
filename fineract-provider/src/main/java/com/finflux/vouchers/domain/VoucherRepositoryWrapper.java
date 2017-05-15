package com.finflux.vouchers.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.fineract.accounting.journalentry.domain.AccountRunningComputationDetail;
import org.apache.fineract.accounting.journalentry.domain.AccountRunningComputationDetailRepository;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.vouchers.exception.VoucherNotFoundException;

@Service
public class VoucherRepositoryWrapper {

    private final VoucherRepository voucherRepository;
    private final AccountRunningComputationDetailRepository accountRunningComputationDetailRepository;
    
    @Autowired
    public VoucherRepositoryWrapper(final VoucherRepository voucherRepository,
            final AccountRunningComputationDetailRepository accountRunningComputationDetailRepository) {
        this.voucherRepository = voucherRepository;
        this.accountRunningComputationDetailRepository = accountRunningComputationDetailRepository ;
    }

    public Voucher save(final Voucher voucher) {
        Voucher returnedVoucher = this.voucherRepository.save(voucher);
        updateAccountRunningComputationDetail(returnedVoucher.getJournalEntry()) ;
        return returnedVoucher ;
    }

    public List<Voucher> save(final List<Voucher> vouchers) {
        List<Voucher> updatedList = this.voucherRepository.save(vouchers);
        for(Voucher voucher: updatedList) {
            updateAccountRunningComputationDetail(voucher.getJournalEntry()) ;
        }
        return updatedList ;
    }
    
    public Integer retrieveVouchersCount(final Integer voucherType, final String financialYear) {
        return this.voucherRepository.retrieveVouchersCount(voucherType, financialYear);
    }
    
    public Voucher findVoucher(final Long voucherId) {
        Voucher voucher = null ;
        if(voucherId != null) voucher = this.voucherRepository.findOne(voucherId) ;
        if(voucher == null) throw new VoucherNotFoundException(voucherId) ;
        return voucher ;
    }
    
    public Voucher findVoucherByRelatedVoucherId(final Long relatedVoucherId) {
        return this.voucherRepository.findVoucherByRelatedVoucherId(relatedVoucherId) ;
    }
    
    private void updateAccountRunningComputationDetail(final JournalEntry journalEntry) {

        Set<Long> accountIds = new HashSet<>();
        for (JournalEntryDetail journalEntryDetail : journalEntry.getJournalEntryDetails()) {
            accountIds.add(journalEntryDetail.getGlAccount().getId());
        }
        Long[] accountIdsAsArray = new Long[accountIds.size()];
        accountIds.toArray(accountIdsAsArray);
        List<AccountRunningComputationDetail> computationDetails = this.accountRunningComputationDetailRepository
                .fetchAccountRunningComputationDetail(journalEntry.getOfficeId(), accountIdsAsArray, journalEntry.getCurrencyCode());
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
