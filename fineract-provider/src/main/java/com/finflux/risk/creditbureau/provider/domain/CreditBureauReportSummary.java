package com.finflux.risk.creditbureau.provider.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_creditbureau_report_summary")
public class CreditBureauReportSummary extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_creditbureau_enquiry_id", nullable = false)
    LoanCreditBureauEnquiryMapping loanCreditBureauEnquiryMapping;

    @Column(name = "comments")
    private String comments;

    @Column(name = "active_loan_count")
    private Long activeLoanCount;

    @Column(name = "closed_loan_count")
    private Long closedLoanCount;

    @Column(name = "delinquent_loan_count")
    private Long delinquentLoanCount;

    @Column(name = "total_outstanding")
    private BigDecimal totalOutstanding;

    @Column(name = "total_overdues")
    private BigDecimal totalOverDues;

    @Column(name = "total_montlhy_due")
    private BigDecimal totalMonthlyDues;

    public CreditBureauReportSummary() {}

    public CreditBureauReportSummary(LoanCreditBureauEnquiryMapping loanCreditBureauEnquiryMapping, String comments, Long activeLoanCount,
            Long closedLoanCount, Long delinquentLoanCount, BigDecimal totalOutstanding, BigDecimal totalOverDues,
            BigDecimal totalMonthlyDues) {
        this.loanCreditBureauEnquiryMapping = loanCreditBureauEnquiryMapping;
        this.comments = comments;
        this.activeLoanCount = activeLoanCount;
        this.closedLoanCount = closedLoanCount;
        this.delinquentLoanCount = delinquentLoanCount;
        this.totalOutstanding = totalOutstanding;
        this.totalOverDues = totalOverDues;
        this.totalMonthlyDues = totalMonthlyDues;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getActiveLoanCount() {
        return this.activeLoanCount;
    }

    public void setActiveLoanCount(Long activeLoanCount) {
        this.activeLoanCount = activeLoanCount;
    }

    public Long getClosedLoanCount() {
        return this.closedLoanCount;
    }

    public void setClosedLoanCount(Long closedLoanCount) {
        this.closedLoanCount = closedLoanCount;
    }

    public Long getDelinquentLoanCount() {
        return this.delinquentLoanCount;
    }

    public void setDelinquentLoanCount(Long delinquentLoanCount) {
        this.delinquentLoanCount = delinquentLoanCount;
    }

    public BigDecimal getTotalOutstanding() {
        return this.totalOutstanding;
    }

    public void setTotalOutstanding(BigDecimal totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }

    public BigDecimal getTotalOverDues() {
        return this.totalOverDues;
    }

    public void setTotalOverDues(BigDecimal totalOverDues) {
        this.totalOverDues = totalOverDues;
    }

    public BigDecimal getTotalMonthlyDues() {
        return this.totalMonthlyDues;
    }

    public void setTotalMonthlyDues(BigDecimal totalInstallments) {
        this.totalMonthlyDues = totalInstallments;
    }

}
