package com.finflux.risk.creditbureau.provider.equifax;

public interface EquifaxConstants {

    // Equifax Inquiry Purpose(s)
    String BUSINESSLAON = "OE";
    String PERSONALLOAN = "1E";
    String HOUSINGLOAN = "2E";
    String OTHERLOAN = "3E";
    String TESTING = "1G";

    // Phone type(s)
    String PHONETYPE_HOME = "H";
    String PHONETYPE_MOBILE = "M";
    String PHONETYPE_HOMEFAX = "P";
    String PHONETYPE_WORKFAX = "F";
    String PHONETYPE_WORK = "T";
    String PHONETYPE_EMPLOYER = "E";

    // Relations
    String FATHER = "K01";
    String HUSBAND = "K02";
    String BROTHER = "K03";
    String SON = "K04";
    String SONINLAW = "K12";
    String FATHERINLAW = "K09";
    String BROTHERINLAW = "K13";
    String OTHER = "K15";
    String MOTHER = "K03";
    String WIFE = "K06";
    String SISTER = "K14";
    String DAUGHTER = "K05";
    String DAUGHTERINLAW = "K10";
    String MOTHERINLAW = "K08";
    String SISTERINLAW = "K11";

    // Active Loan Status
    String DOUBTFUL = "Doubtful";
    String ACCOUNT_INACTIVE = "Account is Inactive";
    String DUE_61_TO_90 = "61-90 days past due";
    String DUE_31_TO_60 = "31-60 days past due";
    String DUE_120_TO_179 = "120 - 179 days past due";
    String DUE_90_TO_119 = "90-119 days past due";
    String CURRENT_ACCOUNT = "Current Account";
    String RESTRUCTURED_GV_MANDATE = "Restructured Loan - Govt Mandate";
    String DUE_30_TO_59 = "30-59 days past due";
    String DUE_1_TO_30 = "1-30 days past due";
    String RESTRUCTURED_NC = "Restructured Loan - Natural Calamity";
    String DUE_91_TO_120 = "91-120 days past due";
    String DUE_121_TO_179 = "121 - 179 days past due";
    String RESTRUCTURED_LOAN = "Restructured Loan";
    String DUE_60_TO_89 = "60-89 days past due";
    String DUE_180_TO_MORE = "180 or more days past due";
    String SUB_STANDARD = "Sub-standard";
    String SPECIAL_MENTION = "Special Mention";
    String DUE_1_TO_29 = "1-29 days past due";
    String SUIT_FILED = "Suit Filed";

    // Pending Loan Status
    String NEW_ACCOUNT = "New Account";
    String SUBMITTED = "Loan Submitted";

    // Approved Loan Status
    String APPROVED = "Loan Approved, Not Yet Disbursed";

    // Rejected Loan Status
    String REJECTED = "Loan Declined";

    // Written Off Loan Status
    String WILLFUL_DEFAULT = "Willful Default";
    String WRITTEN_OFF = "Charge Off/Written Off";
    String POST_WRITTEN_OFF = "Post Written Off Settled";

    // Closed Loan Status
    String SETTLED = "Settled";
    String CLOSED = "Closed Account";
    
    //Report Parameters
    String REPORT_CUSTOMER_TYPE = "MFI" ;
    String REPORT_GROUP_TYPE = "REP" ;
    String REPORT_REPORT_TYPE = "2" ; //Summary
}
