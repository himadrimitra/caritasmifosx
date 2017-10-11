package com.finflux.risk.creditbureau.provider.cibil.response.data;

import java.util.ArrayList;
import java.util.List;

public class ConsumerDisputeData extends Data {

    private final String ENTRY_DATE = "01";
    private final String REMARKS_LINE1 = "02";
    private final String REMARKS_LINE2 = "03";
    private final String REMARKS_LINE3 = "04";
    private final String REMARKS_LINE4 = "05";
    private final String REMARKS_LINE5 = "06";
    private final String REMARKS_LINE6 = "07";

    private String disputeEntryDate;
    private List<String> remarksList = new ArrayList<>();

    @Override
    public void setValue(final String tagType, final String value) {
        switch (tagType) {
            case ENTRY_DATE:
                this.disputeEntryDate = value;
            break;
            case REMARKS_LINE1:
            case REMARKS_LINE2:
            case REMARKS_LINE3:
            case REMARKS_LINE4:
            case REMARKS_LINE5:
            case REMARKS_LINE6:
                this.remarksList.add(value);
            break;
        }
    }

    public String getDisputeEntryDate() {
        return this.disputeEntryDate;
    }

    public List<String> getRemarksList() {
        return this.remarksList;
    }

}
