package com.finflux.pdcm.service;

import java.util.Date;

import org.joda.time.LocalDate;

public class PDCSearchParameters {

    private final Long officeId;
    private final Integer chequeType;
    private final Integer chequeStatus;
    private Date fromDate;
    private Date toDate;

    private PDCSearchParameters(final Long officeId, final Integer chequeType, final Integer chequeStatus, final LocalDate fromDate,
            final LocalDate toDate) {
        this.officeId = officeId;
        this.chequeType = chequeType;
        this.chequeStatus = chequeStatus;
        if (fromDate != null) {
            this.fromDate = fromDate.toDate();
        }
        if (toDate != null) {
            this.toDate = toDate.toDate();
        }
    }

    public static PDCSearchParameters from(final Long officeId, final Integer chequeType, final Integer chequeStatus,
            final LocalDate fromDate, final LocalDate toDate) {
        return new PDCSearchParameters(officeId, chequeType, chequeStatus, fromDate, toDate);
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public Integer getChequeType() {
        return this.chequeType;
    }

    public Integer getChequeStatus() {
        return this.chequeStatus;
    }

    public Date getFromDate() {
        return this.fromDate;
    }

    public Date getToDate() {
        return this.toDate;
    }

}