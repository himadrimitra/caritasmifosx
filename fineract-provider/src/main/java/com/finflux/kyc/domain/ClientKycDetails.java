package com.finflux.kyc.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_client_kyc_details")
public class ClientKycDetails extends AbstractPersistable<Long> {

    @Column(name = "client_id", length = 20, nullable = false)
    private Long clientId;

    @Temporal(TemporalType.DATE)
    @Column(name = "requested_date", nullable = false)
    private Date requestedDate;

    @Column(name = "kyc_type", nullable = false)
    private Integer kycType;

    @Column(name = "kyc_source", nullable = false)
    private Integer kycSource;

    @Column(name = "kyc_mode", nullable = false)
    private Integer kycMode;

    @Column(name = "response_data", nullable = true)
    private String responseData;

    @Column(name = "identifier_id", nullable = false)
    private String identifierId;

    protected ClientKycDetails() {}

    private ClientKycDetails(final Long clientId, final Date requestedDate, final Integer kycType, final Integer kycSource, final Integer kycMode,
            final String responseData, final String identifierId) {
        this.clientId = clientId;
        this.requestedDate = requestedDate;
        this.kycMode = kycMode;
        this.kycSource = kycSource;
        this.kycType = kycType;
        this.responseData = responseData;
        this.identifierId = identifierId;
    }

    public static ClientKycDetails createKYCDetails(final Long clientId, final LocalDate requestedDate, final KycType kycType, final KycSource kycSource,
            final KycMode kycMode, final String responseData, final String identifierId) {
        return new ClientKycDetails(clientId, requestedDate.toDate(), kycType.getValue(), kycSource.getValue(), kycMode.getValue(), responseData,
                identifierId);
    }
}
