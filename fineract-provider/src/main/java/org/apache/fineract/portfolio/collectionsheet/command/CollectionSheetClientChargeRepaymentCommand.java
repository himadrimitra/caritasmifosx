package org.apache.fineract.portfolio.collectionsheet.command;

import java.util.Locale;

import org.joda.time.LocalDate;

public class CollectionSheetClientChargeRepaymentCommand {
	 private final String dateFormat;
	 private final LocalDate transactionDate;
	 private final Locale locale;
   private final ClientChargeRepaymentCommand[] chargeTransactions;
   
	public CollectionSheetClientChargeRepaymentCommand(String dateFormat,
			Locale locale,LocalDate transactionDate, ClientChargeRepaymentCommand[] chargeTransactions) {
		this.dateFormat = dateFormat;
		this.transactionDate = transactionDate;
		this.locale = locale;
		this.chargeTransactions = chargeTransactions;
	}

	public LocalDate getTransactionDate() {
		return this.transactionDate;
	}

	public String getDateFormat() {
		return this.dateFormat;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public ClientChargeRepaymentCommand[] getChargeTransactions() {
		return this.chargeTransactions;
	}
  


}
