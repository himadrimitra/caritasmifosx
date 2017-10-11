package com.finflux.risk.creditbureau.summary.data;

import java.util.ArrayList;
import java.util.List;

import com.finflux.risk.creditbureau.provider.data.CreditScore;
import com.finflux.risk.existingloans.data.ExistingLoanData;

public class CreditBureauSummaryData {

	private List<ExistingLoanData> existingLoans = new ArrayList<>();

	// In CIBIL there is a chance of two records
	private List<CreditScore> creditScores = new ArrayList<>();

	public void addExistingLoan(final ExistingLoanData existingLoan) {
		if (!this.existingLoans.contains(existingLoan)) {
			this.existingLoans.add(existingLoan);
		}
	}

	public void addAllExistingLoan(final List<ExistingLoanData> existingLoans) {
		if (existingLoans != null && !existingLoans.isEmpty()) {
			this.existingLoans.addAll(existingLoans);
		}
	}

	public void addCreditScore(final CreditScore creditScoreData) {
		if (!this.creditScores.contains(creditScoreData)) {
			this.creditScores.add(creditScoreData);
		}
	}

	public void addAllScores(final List<CreditScore> scores) {
		if (scores != null && !scores.isEmpty()) {
			this.creditScores.addAll(scores);
		}
	}
}
