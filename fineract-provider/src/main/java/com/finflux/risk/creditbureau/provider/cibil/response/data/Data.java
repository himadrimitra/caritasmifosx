package com.finflux.risk.creditbureau.provider.cibil.response.data;

public abstract class Data {

    final Integer HEADERTAG_LENGTH = 7;
    private String headerData;

    public abstract void setValue(final String tagType, final String value);

    public Integer getSegmentHeaderLength() {
        return this.HEADERTAG_LENGTH;
    }

    public String getHeaderData() {
        return this.headerData;
    }

    public void setHeaderData(String headerData) {
        this.headerData = headerData;
    }

}
