package com.finflux.risk.creditbureau.provider.cibil;

public interface CibilConstants {

    String PAN = "01";
    String PASSPORT = "02";
    String VOTER_ID = "03";
    String DRIVINGLICENSE = "04";
    String RATIONCARD = "05";
    String AADHAAR_UID = "06";

    // Active Loan Status
    String ACTIVE = "Active";
    // Written Off Loan Status
    String WILLFUL_DEFAULT = "Willful Default";
    String WRITTEN_OFF = "Charge Off/Written Off";
    String POST_WRITTEN_OFF = "Post Written Off Settled";

    // Closed Loan Status
    String SETTLED = "Settled";
    String CLOSED = "Closed Account";

    // Report Parameters
    String REPORT_CUSTOMER_TYPE = "MFI";
    String REPORT_GROUP_TYPE = "REP";
    String REPORT_REPORT_TYPE = "2"; // Summary
}
