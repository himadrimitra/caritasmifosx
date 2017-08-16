package com.finflux.risk.creditbureau.provider.cibil.response;

import java.util.ArrayList;
import java.util.List;

import com.finflux.risk.creditbureau.provider.cibil.response.data.AccountSummaryData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.Data;
import com.finflux.risk.creditbureau.provider.cibil.response.data.OtherAccountData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.OwnAccountData;

public class AccountSegment {

    private final Integer HEADERTAG_LENGTH = 8;
    private final Integer FIELDTAGTYPE_LENGTH = 2;
    private final Integer FIELDVALUE_LENGTH = 2;
    private final String MEMBER_SHORTNAME = "02" ;
    private final String ACCOUNT_GROUP = "04" ;
    private Integer sectionLength;

    private final List<OwnAccountData> accountsList = new ArrayList<>();
    private final List<AccountSummaryData> accountSummaryList = new ArrayList<>();
    private final List<OtherAccountData> otherAccountsList = new ArrayList<>();

    public Integer parseSection(byte[] response, Integer startIndex) {
        Integer index = startIndex;
        final String recordTag = new String(response, index, HEADERTAG_LENGTH);
        index += HEADERTAG_LENGTH;

        String fieldTagType = new String(response, index, FIELDTAGTYPE_LENGTH);
        index += FIELDTAGTYPE_LENGTH;
        Integer valueLength = Integer.parseInt(new String(response, index, FIELDVALUE_LENGTH));
        index += FIELDVALUE_LENGTH;
        String value = new String(response, index, valueLength);
        index += valueLength;
        Data accountData = createObject(fieldTagType, value);
        accountData.setValue(fieldTagType, value);
        boolean isDone = false;

        while (!isDone) {
            fieldTagType = new String(response, index, FIELDTAGTYPE_LENGTH);
            if (ResponseSegment.possibleNextSections.contains(fieldTagType)) {
                this.sectionLength = index - startIndex;
                if (accountData instanceof OwnAccountData) {
                    this.accountsList.add((OwnAccountData) accountData);
                } else if (accountData instanceof AccountSummaryData) {
                    this.accountSummaryList.add((AccountSummaryData) accountData);
                } else if (accountData instanceof OtherAccountData) {
                    this.otherAccountsList.add((OtherAccountData) accountData);
                }
                break;
            }
            index += FIELDTAGTYPE_LENGTH;
            valueLength = Integer.parseInt(new String(response, index, FIELDVALUE_LENGTH));
            index += FIELDVALUE_LENGTH;
            value = new String(response, index, valueLength);
            accountData.setValue(fieldTagType, value);
            index += valueLength;
        }
        return this.sectionLength;
    }

    private Data createObject(final String fieldTagType, final String fieldValue) {
        Data accountData = null;
        switch (fieldTagType) {
            case MEMBER_SHORTNAME:
                if ("ACCTREVIEW_SUMM".equals(fieldValue)) {
                    accountData = new AccountSummaryData();
                } else {
                    accountData = new OwnAccountData();
                }
            break;
            case ACCOUNT_GROUP:
                accountData = new OtherAccountData();
        }
        return accountData;
    }

    public List<AccountSummaryData> getAccoutSummaryList() {
        return this.accountSummaryList;
    }

    public List<OwnAccountData> getOwnAccountsList() {
        return this.accountsList;
    }

    public List<OtherAccountData> getOtherAccountsList() {
        return this.otherAccountsList;
    }
}
