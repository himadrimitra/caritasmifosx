package com.finflux.portfolio.external.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_external_service_details")
public class OtherExternalService extends AbstractPersistable<Long> {


	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "display_code", nullable = false)
	private String displayCode;

	@Column(name = "type", nullable = false)
	private Integer type;

	protected OtherExternalService() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayCode() {
		return displayCode;
	}

	public void setDisplayCode(String displayCode) {
		this.displayCode = displayCode;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}