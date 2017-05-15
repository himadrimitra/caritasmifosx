package com.finflux.vouchers.domain;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_voucher_details")
public class Voucher extends AbstractPersistable<Long> {

    @Column(name = "voucher_type")
    private Integer voucherType;

    @Column(name = "voucher_number")
    private String voucherNumber;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "jentry_id", nullable = true)
    private JournalEntry journalEntry;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "financial_year", nullable=false)
    private String financialYear ;
    
    @Column(name="related_voucher_id", nullable=true)
    private Long relatedVoucherId ;
    
    Voucher() {
        // For JPA
    }

    public Voucher(final Integer voucherType, final String voucherNumber, final JournalEntry journalEntry, final BigDecimal amount,
            final String financialYear, final Long relatedVoucherId) {
        this.voucherType = voucherType;
        this.voucherNumber = voucherNumber;
        this.journalEntry = journalEntry;
        this.amount = amount;
        this.financialYear = financialYear ;
        this.relatedVoucherId = relatedVoucherId ;
    }

    public Integer getVoucherType() {
        return this.voucherType;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getVoucherNumber() {
        return this.voucherNumber;
    }

    public Long getOfficeId() {
        return this.journalEntry.getOfficeId();
    }
    
    public Long getPaymentDetailsId() {
        return this.journalEntry.getPaymentDetailId() ;
    }
    
    public JournalEntry getJournalEntry() {
        return this.journalEntry ;
    }
    
    public void setRelatedVoucherId(final Long relatedVoucherId) {
        this.relatedVoucherId = relatedVoucherId ;
    }
    
    public Long getRelatedVoucherId() {
        return this.relatedVoucherId ;
    }
    
    public boolean isReversed() {
        return this.journalEntry.isReversed() ;
    }
    
    public String getTransactionId() {
        return this.journalEntry.getTransactionIdentifier() ;
    }
}
