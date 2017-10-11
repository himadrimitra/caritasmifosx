package com.finflux.risk.creditbureau.provider.cibil.response.data;

public class AccountSummaryData extends Data {

    private final String MEMBER_SHORTNAME = "02";
    private final String NUMBEROF_ACCOUNTS = "03";
    private final String ACCOUNT_GROUP = "04";
    private final String LIVE_OR_CLOSED_INDICATOR = "05";
    private final String TOTAL_SANCTIONED_AMOUNT = "12";
    private final String CURRENT_BALANCE = "13";

    private String memberShortName;
    private String numberOfAccounts;
    private String accountGroup;
    private String liveOrClosedIndicator;
    private String sanctionedAmount;
    private String currentBalance;

    public AccountSummaryData() {

    }

    public String getMemberShortName() {
        return this.memberShortName;
    }

    public void setMemberShortName(String memberShortName) {
        this.memberShortName = memberShortName;
    }

    public String getNumberOfAccounts() {
        return this.numberOfAccounts;
    }

    public String getAccountGroup() {
        return this.accountGroup;
    }

    public String getLiveOrClosedIndicator() {
        return this.liveOrClosedIndicator;
    }

    public String getSanctionedAmount() {
        return this.sanctionedAmount;
    }

    public String getCurrentBalance() {
        return this.currentBalance;
    }

    @Override
    public void setValue(final String tagType, final String value) {
        switch (tagType) {
            case MEMBER_SHORTNAME:
                this.memberShortName = value;
            break;
            case NUMBEROF_ACCOUNTS:
                this.numberOfAccounts = value;
            break;
            case ACCOUNT_GROUP:
                this.accountGroup = value;
            break;
            case LIVE_OR_CLOSED_INDICATOR:
                this.liveOrClosedIndicator = value;
            break;
            case TOTAL_SANCTIONED_AMOUNT:
                this.sanctionedAmount = value;
            break;
            case CURRENT_BALANCE:
                this.currentBalance = value;
            break;
        }

    }

}
