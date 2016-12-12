package org.apache.fineract.portfolio.validations.data;

public class EntityFieldRegexValidation {
	private final Integer entityType;
	private final String preriquisitesType;
	private final String fieldName;
	private final String regex;
	private final String errorMsg;

	public EntityFieldRegexValidation(Integer entityType, String preriquisitesType, String fieldName, String regex,
			String errorMsg) {
		super();
		this.entityType = entityType;
		this.preriquisitesType = preriquisitesType;
		this.fieldName = fieldName;
		this.regex = regex;
		this.errorMsg = errorMsg;
	}

	public Integer getEntityType() {
		return this.entityType;
	}

	public String getPreriquisitesType() {
		return this.preriquisitesType;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public String getRegex() {
		return this.regex;
	}

	public String getErrorMsg() {
		return this.errorMsg;
	}

	public static EntityFieldRegexValidation intance(final Integer entityType, final String preriquisitesType,
			final String fieldName, final String regex, final String errorMsg) {
		return new EntityFieldRegexValidation(entityType, preriquisitesType, fieldName, regex, errorMsg);
	}

}
