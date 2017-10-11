package com.finflux.infrastructure.external.authentication.aadhar.data;

public class EkycRequestData {

    private String saCode;

    private String uuid;

    private String requestId;

    private String aadhaarId;

    private String hash;

    public String getSaCode() {
        return this.saCode;
    }

    public void setSaCode(String saCode) {
        this.saCode = saCode;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAadhaarId() {
        return this.aadhaarId;
    }

    public void setAadhaarId(String aadhaarId) {
        this.aadhaarId = aadhaarId;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

}
