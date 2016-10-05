package com.finflux.organisation.transaction.authentication.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum SupportedAuthenticationPortfolioTypes {

	INVALID(0, "transactionAuthentication.invalid"), //
	LOANS(1, "transactionAuthentication.loan"), //
	SAVINGS(2, "transactionAuthentication.savings");

	private final Integer value;
	private final String code;

	private SupportedAuthenticationPortfolioTypes(final Integer value, final String code) {
		this.value = value;
		this.code = code;
	}

	public Integer getValue() {
		return this.value;
	}

	public String getCode() {
		return this.code;
	}

	public static SupportedAuthenticationPortfolioTypes fromInt(final Integer chargeAppliesTo) {
		SupportedAuthenticationPortfolioTypes supportedAuthenticationPortfolioTypes = SupportedAuthenticationPortfolioTypes.INVALID;

		if (chargeAppliesTo != null) {
			switch (chargeAppliesTo) {
			case 1:
				supportedAuthenticationPortfolioTypes = LOANS;
				break;
			case 2:
				supportedAuthenticationPortfolioTypes = SAVINGS;
				break;
			default:
				supportedAuthenticationPortfolioTypes = INVALID;
				break;
			}
		}

		return supportedAuthenticationPortfolioTypes;
	}

	public boolean isLoanCharge() {
		return this.value.equals(SupportedAuthenticationPortfolioTypes.LOANS.getValue());
	}

	public boolean isSavingsCharge() {
		return this.value.equals(SupportedAuthenticationPortfolioTypes.SAVINGS.getValue());
	}

	public static Object[] validValues() {
		return new Object[] { SupportedAuthenticationPortfolioTypes.LOANS.getValue(),
				SupportedAuthenticationPortfolioTypes.SAVINGS.getValue() };
	}

	public static EnumOptionData chargeAppliesTo(final SupportedAuthenticationPortfolioTypes type) {
		EnumOptionData optionData = null;
		switch (type) {
		case LOANS:
			optionData = new EnumOptionData(SupportedAuthenticationPortfolioTypes.LOANS.getValue().longValue(),
					SupportedAuthenticationPortfolioTypes.LOANS.getCode(), "Loans");
			break;
		case SAVINGS:
			optionData = new EnumOptionData(SupportedAuthenticationPortfolioTypes.SAVINGS.getValue().longValue(),
					SupportedAuthenticationPortfolioTypes.SAVINGS.getCode(), "Savings");
			break;
		default:
			optionData = new EnumOptionData(SupportedAuthenticationPortfolioTypes.INVALID.getValue().longValue(),
					SupportedAuthenticationPortfolioTypes.INVALID.getCode(), "Invalid");
			break;
		}
		return optionData;
	}

}
