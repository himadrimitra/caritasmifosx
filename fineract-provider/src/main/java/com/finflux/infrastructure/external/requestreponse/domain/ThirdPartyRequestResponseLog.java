package com.finflux.infrastructure.external.requestreponse.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

@Entity
@Table(name = "f_thirdparty_request_reponse_log")
public class ThirdPartyRequestResponseLog extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "entity_type", length = 3, nullable = false)
    private Integer entityType;

    @Column(name = "entity_id", length = 20, nullable = false)
    private Long entityId;

    @Column(name = "request_method", length = 16, nullable = false)
    private String requestMethod;

    @Column(name = "url", length = 512, nullable = false)
    private String url;

    @Column(name = "request")
    private String request;

    @Column(name = "response")
    private String response;

    @Column(name = "http_status_code", length = 4)
    private Integer httpStatusCode;

    @Column(name = "response_time_ms", length = 20)
    private Long responseTimeInMs;

    protected ThirdPartyRequestResponseLog() {}

    public ThirdPartyRequestResponseLog(Integer entityType, Long entityId, String requestMethod, String url,
                                        String request) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.requestMethod = requestMethod;
        this.url = url;
        this.request = request;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public Long getResponseTimeInMs() {
        return responseTimeInMs;
    }

    public void setResponseTimeInMs(Long responseTimeInMs) {
        this.responseTimeInMs = responseTimeInMs;
    }
}
