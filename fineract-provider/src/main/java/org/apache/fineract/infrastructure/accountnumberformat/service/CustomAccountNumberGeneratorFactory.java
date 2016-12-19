package org.apache.fineract.infrastructure.accountnumberformat.service;

import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormatEnumerations.AccountNumberCustomPrefixType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CustomAccountNumberGeneratorFactory {

	private final ApplicationContext applicationContext;

	@Autowired
	public CustomAccountNumberGeneratorFactory(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public CustomAccountNumberGenerator determineGenerator(final Integer customPrefixTypeId) {
		AccountNumberCustomPrefixType accountNumberPrefixType = AccountNumberCustomPrefixType
				.fromInt(customPrefixTypeId);

		CustomAccountNumberGenerator accountNumberGenerator = null;
		switch (accountNumberPrefixType) {
		case NCC:
			accountNumberGenerator = this.applicationContext.getBean("nccAccountNumberGenerator",
					CustomAccountNumberGenerator.class);
			break;

		default:
			break;
		}
		return accountNumberGenerator;
	}
}
