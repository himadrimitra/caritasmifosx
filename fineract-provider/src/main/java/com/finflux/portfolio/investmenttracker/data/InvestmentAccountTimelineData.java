package com.finflux.portfolio.investmenttracker.data;

import org.joda.time.LocalDate;


public class InvestmentAccountTimelineData {

    private final LocalDate submittedOnDate;
    private final String submittedByUsername;
    private final String submittedByFirstname;
    private final String submittedByLastname;
    
    private final LocalDate approvedOnDate;
    private final String approvedByUsername;
    private final String approvedByFirstname;
    private final String approvedByLastname;
    
    private final LocalDate activatedOnDate;
    private final String activatedByUsername;
    private final String activatedByFirstname;
    private final String activatedByLastname;
    
    private final LocalDate investmentOnData;
    private final String investedByUsername;
    private final String investedByFirstname;
    private final String investedByLastname;
    
    private final LocalDate maturityOnDate;
    private final String maturityByUsername;
    private final String maturityByFirstname;
    private final String maturityByLastname;
    
    public InvestmentAccountTimelineData(LocalDate submittedOnDate, String submittedByUsername, String submittedByFirstname,
            String submittedByLastname, LocalDate approvedOnDate, String approvedByUsername, String approvedByFirstname,
            String approvedByLastname, LocalDate activatedOnDate, String activatedByUsername, String activatedByFirstname,
            String activatedByLastname, LocalDate investmentOnData, String investedByUsername, String investedByFirstname,
            String investedByLastname, LocalDate maturityOnDate, String maturityByUsername, String maturityByFirstname,
            String maturityByLastname) {
        this.submittedOnDate = submittedOnDate;
        this.submittedByUsername = submittedByUsername;
        this.submittedByFirstname = submittedByFirstname;
        this.submittedByLastname = submittedByLastname;
        this.approvedOnDate = approvedOnDate;
        this.approvedByUsername = approvedByUsername;
        this.approvedByFirstname = approvedByFirstname;
        this.approvedByLastname = approvedByLastname;
        this.activatedOnDate = activatedOnDate;
        this.activatedByUsername = activatedByUsername;
        this.activatedByFirstname = activatedByFirstname;
        this.activatedByLastname = activatedByLastname;
        this.investmentOnData = investmentOnData;
        this.investedByUsername = investedByUsername;
        this.investedByFirstname = investedByFirstname;
        this.investedByLastname = investedByLastname;
        this.maturityOnDate = maturityOnDate;
        this.maturityByUsername = maturityByUsername;
        this.maturityByFirstname = maturityByFirstname;
        this.maturityByLastname = maturityByLastname;
    }

    
    public LocalDate getSubmittedOnDate() {
        return this.submittedOnDate;
    }

    
    public String getSubmittedByUsername() {
        return this.submittedByUsername;
    }

    
    public String getSubmittedByFirstname() {
        return this.submittedByFirstname;
    }

    
    public String getSubmittedByLastname() {
        return this.submittedByLastname;
    }

    
    public LocalDate getApprovedOnDate() {
        return this.approvedOnDate;
    }

    
    public String getApprovedByUsername() {
        return this.approvedByUsername;
    }

    
    public String getApprovedByFirstname() {
        return this.approvedByFirstname;
    }

    
    public String getApprovedByLastname() {
        return this.approvedByLastname;
    }

    
    public LocalDate getActivatedOnDate() {
        return this.activatedOnDate;
    }

    
    public String getActivatedByUsername() {
        return this.activatedByUsername;
    }

    
    public String getActivatedByFirstname() {
        return this.activatedByFirstname;
    }

    
    public String getActivatedByLastname() {
        return this.activatedByLastname;
    }

    
    public LocalDate getInvestmentOnData() {
        return this.investmentOnData;
    }

    
    public String getInvestedByUsername() {
        return this.investedByUsername;
    }

    
    public String getInvestedByFirstname() {
        return this.investedByFirstname;
    }

    
    public String getInvestedByLastname() {
        return this.investedByLastname;
    }

    
    public LocalDate getMaturityOnDate() {
        return this.maturityOnDate;
    }

    
    public String getMaturityByUsername() {
        return this.maturityByUsername;
    }

    
    public String getMaturityByFirstname() {
        return this.maturityByFirstname;
    }

    
    public String getMaturityByLastname() {
        return this.maturityByLastname;
    } 
    
    
}
