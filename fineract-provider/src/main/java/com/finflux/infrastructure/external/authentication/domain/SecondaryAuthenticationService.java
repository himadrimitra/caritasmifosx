package com.finflux.infrastructure.external.authentication.domain;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_authentication")
public class SecondaryAuthenticationService extends AbstractPersistable<Long> {

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "auth_service_class_name")
	private String authServiceClassName;

	@Column(name = "is_active")
	private boolean isActive;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lastmodified_date", nullable = false)
	private Date lastModifiedDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lastmodifiedby_id", nullable = true)
	private AppUser lastModifiedById;

	public SecondaryAuthenticationService() {
		// TODO Auto-generated constructor stub
	}

	public Map<String, Object> update(final JsonCommand command, final AppUser modifiedUser) {
		final Map<String, Object> actualChanges = new LinkedHashMap<>(5);

		final String isActiveParamName = "isActive";
		if (command.isChangeInBooleanParameterNamed(isActiveParamName, this.isActive)) {
			final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(isActiveParamName);
			actualChanges.put(isActiveParamName, newValue);
			this.isActive = newValue;
		}
		if (!actualChanges.isEmpty()) {
			this.lastModifiedDate = DateUtils.getLocalDateTimeOfTenant().toDate();
			this.lastModifiedById = modifiedUser;
		}
		return actualChanges;
	}
	
	public String getName() {
		return name;
	}

	public String getAuthServiceClassName() {
		return authServiceClassName;
	}

	public void setAuthServiceClassName(String authServiceClassName) {
		this.authServiceClassName = authServiceClassName;
	}

	public boolean isActive() {
		return isActive;
	}


}
