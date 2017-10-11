package org.apache.fineract.accounting.journalentry.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_journal_entry_detail")
public class JournalEntryDetail extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private GLAccount glAccount;

    @Column(name = "type_enum", nullable = false)
    private Integer type;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;
    
    
    protected JournalEntryDetail() {
        // TODO Auto-generated constructor stub
    }
    
    public JournalEntryDetail(final GLAccount glAccount, final Integer type, final BigDecimal amount) {
        this.glAccount = glAccount;
        this.type = type;
        this.amount = amount;
    }

    public static JournalEntryDetail createNew(GLAccount glAccount, JournalEntryType type, BigDecimal amount) {
        return new JournalEntryDetail(glAccount, type.getValue(), amount);
    }

    
    public GLAccount getGlAccount() {
        return this.glAccount;
    }

    
    public Integer getType() {
        return this.type;
    }

    
    public BigDecimal getAmount() {
        return this.amount;
    }
    
    public boolean isDebitEntry() {
        return JournalEntryType.DEBIT.getValue().equals(this.type);
    }
    
    public JournalEntryDetail reversalJournalEntry(){
        JournalEntryType type = JournalEntryType.DEBIT;
        if(isDebitEntry()){
            type = JournalEntryType.CREDIT;
        }
        return new JournalEntryDetail(getGlAccount(), type.getValue(), getAmount());
        
    }
    
}