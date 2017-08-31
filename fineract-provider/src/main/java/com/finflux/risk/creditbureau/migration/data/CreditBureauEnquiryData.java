package com.finflux.risk.creditbureau.migration.data;

public class CreditBureauEnquiryData {

    private final Long id;
    private final String request;
    private final String response;
    private final Integer status;

    public CreditBureauEnquiryData(final Long id, final String request, final String response, final Integer status) {
        super();
        this.id = id;
        this.request = request;
        this.response = response;
        this.status = status;
    }

    public Long getId() {
        return this.id;
    }

    public String getRequest() {
        return this.request;
    }

    public String getResponse() {
        return this.response;
    }
    
    public Integer getStatus() {
        return this.status ;
    }
}
