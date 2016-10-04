package com.finflux.organisation.transaction.authentication.domain;

public enum SupportedAuthenticaionTransactionTypes {
	INVALID(0, "transactionType.invalid"), //
	DISBURSEMENT(1, "transactionType.disbursement"), // only for loans
	REYPAYMENT(2, "transactionType.reymapayment"), // only for loan; //
	DEPOSIT(3, "transactionType.deposit"), // only for savings
	WITHDRAWAL(4, "transactionType.withdrawal"); // only for savings

	private final Integer value;
	private final String code;

	private SupportedAuthenticaionTransactionTypes(Integer value, String code) {
		this.value = value;
		this.code = code;
	}

	public Integer getValue() {
		return this.value;
	}

	public String getCode() {
		return this.code;
	}

	public static Object[] validLoanValues() {
		return new Integer[] { SupportedAuthenticaionTransactionTypes.DISBURSEMENT.getValue(), SupportedAuthenticaionTransactionTypes.REYPAYMENT.getValue() };
	}

	public static Object[] validSavingValues() {
		return new Integer[] { SupportedAuthenticaionTransactionTypes.DEPOSIT.getValue(), SupportedAuthenticaionTransactionTypes.WITHDRAWAL.getValue() };
	}

	public static SupportedAuthenticaionTransactionTypes fromInt(final Integer transaction) {
		SupportedAuthenticaionTransactionTypes supportedAuthenticaionTransactionTypes = SupportedAuthenticaionTransactionTypes.INVALID;
		if (transaction != null) {
			switch (transaction) {
			case 1:
				supportedAuthenticaionTransactionTypes = DISBURSEMENT;
				break;
			case 2:
				supportedAuthenticaionTransactionTypes = REYPAYMENT;
				break;
			case 3:
				supportedAuthenticaionTransactionTypes = DEPOSIT;
				break;
			case 4:
				supportedAuthenticaionTransactionTypes = WITHDRAWAL;
				break;
			default:
				supportedAuthenticaionTransactionTypes = INVALID;
				break;
			}
		}
		return supportedAuthenticaionTransactionTypes;
	}

	public boolean isDisbursement() {
		return SupportedAuthenticaionTransactionTypes.DISBURSEMENT.getValue().equals(this.value);
	}

	public boolean isReypayment() {
		return SupportedAuthenticaionTransactionTypes.REYPAYMENT.getValue().equals(this.value);
	}

	public boolean isDeposit() {
		return SupportedAuthenticaionTransactionTypes.DEPOSIT.getValue().equals(this.value);
	}

	public boolean isWithdrawal() {
		return SupportedAuthenticaionTransactionTypes.WITHDRAWAL.getValue().equals(this.value);
	}
}
