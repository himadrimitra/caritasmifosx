package com.finflux.risk.creditbureau.provider.cibil.request;

public class Address {

    public static final String PERMANENT = "01";
    public static final String RESIDENT = "02";
    public static final String OFFICE = "03";
    public static final String NOTCATEGORIZED = "04";
    public static final String OWNED = "01";
    public static final String RENTED = "02";
    private final String addressLine;
    private final String stateCode;
    private final String pinCode;
    private final String addressCategory;
    private final String resideneCode;

    public Address(String addressCategory, String addressLine, String stateCode, String pinCode, String resideneCode) {
        super();
        this.addressLine = addressLine;
        this.stateCode = stateCode;
        this.pinCode = pinCode;
        this.addressCategory = addressCategory;
        this.resideneCode = resideneCode;
    }

    public String getAddressLine() {
        return this.addressLine;
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
}