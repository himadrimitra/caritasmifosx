package com.finflux.infrastructure.external.authentication.data;

public class ExternalAuthenticationServiceData {
	private final Long id;
	private final String name;
	private final String description;
	private final String authServiceClassName;
	private final boolean isActive;

	private ExternalAuthenticationServiceData(Long id, String name, String description, String authServiceClassName,
			boolean isActive) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.authServiceClassName = authServiceClassName;
		this.isActive = isActive;
	}

	public boolean isActive() {
		return isActive;
	}

	public String getName() {
		return name;
	}

	public static ExternalAuthenticationServiceData instance(Long id, String name, String description,
			String authServiceClassName, boolean isActive) {
		return new ExternalAuthenticationServiceData(id, name, description, authServiceClassName, isActive);
	}
}
