package com.finflux.organisation.transaction.authentication.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class SupportedTransactionTypeEnumerations {

	public static EnumOptionData transactionType(final int id) {
		return supportedAuthenticaionTransactionTypes(SupportedAuthenticaionTransactionTypes.fromInt(id));
	}

	public static EnumOptionData supportedAuthenticaionTransactionTypes(final SupportedAuthenticaionTransactionTypes supportedAuthenticaionTransactionTypes) {
		EnumOptionData enumOptionData = null;
		switch (supportedAuthenticaionTransactionTypes) {
		case DISBURSEMENT:
			enumOptionData = new EnumOptionData(SupportedAuthenticaionTransactionTypes.DISBURSEMENT.getValue().longValue(),
					SupportedAuthenticaionTransactionTypes.DISBURSEMENT.getCode(), "Disbursement");
			break;
		case REYPAYMENT:
			enumOptionData = new EnumOptionData(SupportedAuthenticaionTransactionTypes.REYPAYMENT.getValue().longValue(),
					SupportedAuthenticaionTransactionTypes.REYPAYMENT.getCode(), "Reypayment");
			break;
		case DEPOSIT:
			enumOptionData = new EnumOptionData(SupportedAuthenticaionTransactionTypes.DEPOSIT.getValue().longValue(),
					SupportedAuthenticaionTransactionTypes.DEPOSIT.getCode(), "Deposit");
			break;
		case WITHDRAWAL:
			enumOptionData = new EnumOptionData(SupportedAuthenticaionTransactionTypes.WITHDRAWAL.getValue().longValue(),
					SupportedAuthenticaionTransactionTypes.WITHDRAWAL.getCode(), "Withdrawal");
			break;
		default:
			enumOptionData = new EnumOptionData(SupportedAuthenticaionTransactionTypes.INVALID.getValue().longValue(),
					SupportedAuthenticaionTransactionTypes.INVALID.getCode(), "Invalid");
			break;
		}
		return enumOptionData;
	}

}
