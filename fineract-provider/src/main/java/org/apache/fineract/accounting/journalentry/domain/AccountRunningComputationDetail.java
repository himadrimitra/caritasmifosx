package org.apache.fineract.accounting.journalentry.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_running_balance_computation_detail")
public class AccountRunningComputationDetail extends AbstractPersistable<Long>{

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @Column(name = "account_id", nullable = false)
    private Long glAccountId;

    @Column(name = "computed_till_date")
    @Temporal(TemporalType.DATE)
    private Date computedTillDate;
    
    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode;
    
    protected AccountRunningComputationDetail() {

    }

    public AccountRunningComputationDetail(final Long officeId, final Long glAccountId, final Date computedTillDate,
            final String currencyCode) {
        this.officeId = officeId;
        this.glAccountId = glAccountId;
        this.computedTillDate = computedTillDate;
        this.currencyCode = currencyCode;
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public Long getGlAccountId() {
        return this.glAccountId;
    }

    public Date getComputedTillDate() {
        return this.computedTillDate;
    }

    public LocalDate getComputedTillDateAsLocalDate() {
        LocalDate computedTillDate = null;
        if (this.computedTillDate != null) {
            computedTillDate = new LocalDate(this.computedTillDate);
        }
        return computedTillDate;
    }

    public void setComputedTillDate(Date computedTillDate) {
        this.computedTillDate = computedTillDate;
    }
}
