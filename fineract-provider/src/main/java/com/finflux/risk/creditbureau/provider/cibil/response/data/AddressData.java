package com.finflux.risk.creditbureau.provider.cibil.response.data;

import java.util.ArrayList;
import java.util.List;

public final class AddressData extends Data {

    private final String ADDRESSLINE_ONE = "01";
    private final String ADDRESSLINE_TWO = "02";
    private final String ADDRESSLINE_THREE = "03";
    private final String ADDRESSLINE_FOUR = "04";
    private final String ADDRESSLINE_FIVE = "05";
    private final String STATE_CODE = "06";
    private final String PIN_CODE = "07";
    private final String ADDRESS_CATEGORY = "08";
    private final String RESIDENCE_CODE = "09";
    private final String DATE_REPORTED = "10";
    private final String MEMBER_SHORTNAME = "11";
    private final String ENRICHED_ENQUIRY = "90";

    private List<String> addressLineList = new ArrayList<>();
    private String stateCode;
    private String pinCode;
    private String addressCategory;
    private String resideneCode;
    private String dateReported;
    private String memberShortName;
    private String enrichThroughEnquiry; // only Y

    public AddressData() {

    }

    public List<String> getAddressLines() {
        return this.addressLineList;
    }

    public String getStateCode() {
        return this.stateCode;
    }

    public String getPinCode() {
        return this.pinCode;
    }

    public String getAddressCategory() {
        return this.addressCategory;
    }

    public String getResideneCode() {
        return this.resideneCode;
    }

    public String getDateReported() {
        return this.dateReported;
    }

    public void setDateReported(String dateReported) {
        this.dateReported = dateReported;
    }

    public String getMemberShortName() {
        return this.memberShortName;
    }

    public String getEnrichThroughEnquiry() {
        return this.enrichThroughEnquiry;
    }

    @Override
    public void setValue(String tagType, String value) {
        switch (tagType) {
            case ADDRESSLINE_ONE:
            case ADDRESSLINE_TWO:
            case ADDRESSLINE_THREE:
            case ADDRESSLINE_FOUR:
            case ADDRESSLINE_FIVE:
                this.addressLineList.add(value);
            break;
            case STATE_CODE:
                this.stateCode = value;
            break;
            case PIN_CODE:
                this.pinCode = value;
            break;
            case ADDRESS_CATEGORY:
                this.addressCategory = value;
            break;
            case RESIDENCE_CODE:
                this.resideneCode = value;
            break;
            case DATE_REPORTED:
                setDateReported(value);
            break;
            case MEMBER_SHORTNAME:
                this.memberShortName = value;
            break;
            case ENRICHED_ENQUIRY:
                this.enrichThroughEnquiry = value;
            break;
        }
    }

}