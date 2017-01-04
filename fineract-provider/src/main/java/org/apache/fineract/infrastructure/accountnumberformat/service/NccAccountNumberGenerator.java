package org.apache.fineract.infrastructure.accountnumberformat.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormat;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "nccAccountNumberGenerator")
public class NccAccountNumberGenerator implements CustomAccountNumberGenerator {

	private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper;
	private final static String SEPERATOR = "-";
	private final static String ACCOUNT_PREFIX = "1";
	private final static int maxLength = 9;

	@Autowired
	public NccAccountNumberGenerator(ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper) {
		this.applicationCurrencyRepositoryWrapper = applicationCurrencyRepositoryWrapper;
	}

	@Override
	public String generateAccountNumberForSavings(SavingsAccount savingsAccount,
			AccountNumberFormat accountNumberFormat) {

		String savingsAccountId = savingsAccount.getId().toString();
		String externalIdForSavingsProduct = savingsAccount.savingsProduct().getExternalId();
		String externalIdForCurreny = applicationCurrencyRepositoryWrapper
				.findOneWithNotFoundDetection(savingsAccount.getCurrency()).getExternalId();
		String externalIdforOffice = savingsAccount.office().getExternalId();

		return generateAccountNumber(savingsAccountId, externalIdForSavingsProduct, externalIdForCurreny,
				externalIdforOffice);
	}

	@Override
	public String generateAccountNumberForLoans(Loan loan, AccountNumberFormat accountNumberFormat) {
		String accountId = loan.getId().toString();
		String externalIdForProduct = loan.getLoanProduct().getExternalId();
		String externalIdForCurreny = applicationCurrencyRepositoryWrapper
				.findOneWithNotFoundDetection(loan.getCurrency()).getExternalId();
		String externalIdforOffice = loan.getOffice().getExternalId();
		return generateAccountNumber(accountId, externalIdForProduct, externalIdForCurreny, externalIdforOffice);
	}

	private String generateAccountNumber(String accountId, String externalIdForProduct, String externalIdForCurreny,
			String externalIdforOffice) {
		String accountnumber = StringUtils.leftPad(accountId, maxLength, '0');
		if (externalIdForProduct != null) {
			accountnumber = StringUtils.overlay(accountnumber, externalIdForProduct + SEPERATOR, 0, 0);
		}
		if (externalIdForCurreny != null) {
			accountnumber = StringUtils.overlay(accountnumber, externalIdForCurreny + SEPERATOR, 0, 0);
		}
		if (externalIdforOffice != null) {
			accountnumber = StringUtils.overlay(accountnumber, externalIdforOffice + SEPERATOR, 0, 0);
		} accountnumber = StringUtils.overlay(accountnumber, ACCOUNT_PREFIX, 0,0);
		return accountnumber;
	}

}
