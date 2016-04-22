package com.finflux.risk.existingloans.data;

import org.joda.time.LocalDate;

public class ExistingLoanTimelineData {

    private final LocalDate disbursedDate;
    private final LocalDate maturityDate;
    private final String createdByUsername;
    private final String lastmodifiedbyUsername;

    public ExistingLoanTimelineData(LocalDate disbursedDate, LocalDate maturityDate, String createdByUsername, String lastmodifiedbyUsername) {
        this.disbursedDate = disbursedDate;
        this.maturityDate = maturityDate;
        this.createdByUsername = createdByUsername;
        this.lastmodifiedbyUsername = lastmodifiedbyUsername;
    }

    public LocalDate getDisbursedDate() {
        return disbursedDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public String getLastmodifiedbyUsername() {
        return lastmodifiedbyUsername;
    }

}
