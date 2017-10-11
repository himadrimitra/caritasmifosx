package com.finflux.portfolio.external.domain;

import javax.persistence.*;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_external_service_properties")
public class OtherExternalServiceProperty extends AbstractPersistable<Long> {


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "external_service_id")
	private OtherExternalService externalService;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "value")
	private String value;

	@Column(name = "is_encrypted")
	private Boolean isEncrypted;

	protected OtherExternalServiceProperty() {}

	public OtherExternalService getExternalService() {
		return externalService;
	}

	public void setExternalService(OtherExternalService externalService) {
		this.externalService = externalService;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getEncrypted() {
		return isEncrypted;
	}

	public void setEncrypted(Boolean encrypted) {
		isEncrypted = encrypted;
	}
}