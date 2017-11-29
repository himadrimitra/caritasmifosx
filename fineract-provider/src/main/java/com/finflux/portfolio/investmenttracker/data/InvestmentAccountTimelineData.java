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
    
    private  LocalDate investmentOnData;
    private final String investedByUsername;
    private final String investedByFirstname;
    private final String investedByLastname;
    
    private  LocalDate maturityOnDate;
    private final String maturityByUsername;
    private final String maturityByFirstname;
    private final String maturityByLastname;
    
    private final LocalDate rejectOnData;
    private final String rejectByUsername;
    private final String rejectByFirstname;
    private final String rejectByLastname;
    
    private final LocalDate closeOnDate;
    private final String closeByUsername;
    private final String closeByFirstname;
    private final String closeByLastname;
    


    
    public InvestmentAccountTimelineData(LocalDate submittedOnDate, String submittedByUsername, String submittedByFirstname,
            String submittedByLastname, LocalDate approvedOnDate, String approvedByUsername, String approvedByFirstname,
            String approvedByLastname, LocalDate activatedOnDate, String activatedByUsername, String activatedByFirstname,
            String activatedByLastname, LocalDate investmentOnData, String investedByUsername, String investedByFirstname,
            String investedByLastname, LocalDate maturityOnDate, String maturityByUsername, String maturityByFirstname,
            String maturityByLastname, LocalDate rejectOnData, String rejectByUsername, String rejectByFirstname, String rejectByLastname,
            LocalDate closeOnDate, String closeByUsername, String closeByFirstname, String closeByLastname) {
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
        this.rejectOnData = rejectOnData;
        this.rejectByUsername = rejectByUsername;
        this.rejectByFirstname = rejectByFirstname;
        this.rejectByLastname = rejectByLastname;
        this.closeOnDate = closeOnDate;
        this.closeByUsername = closeByUsername;
        this.closeByFirstname = closeByFirstname;
        this.closeByLastname = closeByLastname;
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


    
    public LocalDate getRejectOnData() {
        return this.rejectOnData;
    }


    
    public String getRejectByUsername() {
        return this.rejectByUsername;
    }


    
    public String getRejectByFirstname() {
        return this.rejectByFirstname;
    }


    
    public String getRejectByLastname() {
        return this.rejectByLastname;
    }


    
    public LocalDate getCloseOnDate() {
        return this.closeOnDate;
    }


    
    public String getCloseByUsername() {
        return this.closeByUsername;
    }


    
    public String getCloseByFirstname() {
        return this.closeByFirstname;
    }

    
    public String getCloseByLastname() {
        return this.closeByLastname;
    }

    
    public void setMaturityOnDate(LocalDate maturityOnDate) {
        this.maturityOnDate = maturityOnDate;
    }


    
    public void setInvestmentOnData(LocalDate investmentOnData) {
        this.investmentOnData = investmentOnData;
    }
    
    
}
