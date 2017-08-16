package com.finflux.risk.creditbureau.summary.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.provider.data.CreditScore;
import com.finflux.risk.creditbureau.summary.data.CreditBureauSummaryData;
import com.finflux.risk.existingloans.data.ExistingLoanData;
import com.finflux.risk.existingloans.service.ExistingLoanReadPlatformService;

@Service
public class CreditBureauSummaryReadPlatformServiceImpl implements CreditBureauSummaryReadPlatformService {

	private final ExistingLoanReadPlatformService existingLoanReadPlatformService;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public CreditBureauSummaryReadPlatformServiceImpl(
			final ExistingLoanReadPlatformService existingLoanReadPlatformService, final RoutingDataSource dataSource) {
		this.existingLoanReadPlatformService = existingLoanReadPlatformService;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public CreditBureauSummaryData retrieveCreditSummary(final Long clientId, Long loanApplicationId, Long loanId,
			Long trancheDisbursalId) {
		List<ExistingLoanData> existingLoanData = this.existingLoanReadPlatformService.retriveAll(clientId,
				loanApplicationId, loanId, trancheDisbursalId);
		CreditBureauSummaryData creditSummaryData = new CreditBureauSummaryData();
		creditSummaryData.addAllExistingLoan(existingLoanData);
		if (existingLoanData != null && !existingLoanData.isEmpty()) {
			List<CreditScore> scores = retriveScores(existingLoanData.get(0).getLoanCreditBureauEnquiryId());
			creditSummaryData.addAllScores(scores);
		}
		return creditSummaryData;
	}

	public List<CreditScore> retriveScores(final Long loanCreditBureauEnquiryId) {
		final CreditScoreMapper mapper = new CreditScoreMapper();
		final StringBuilder query = new StringBuilder();
		query.append("select ");
		query.append(mapper.query());
		query.append(" where score.loan_creditbureau_enquiry_id = ?");
		return this.jdbcTemplate.query(query.toString(), mapper, new Object[] { loanCreditBureauEnquiryId });
	}

	class CreditScoreMapper implements RowMapper<CreditScore> {

		private final StringBuilder builder = new StringBuilder();

		CreditScoreMapper() {
			builder.append("score.score_name as scoreName, score.score_card_name as scoreCardName, ");
			builder.append("score.score_card_version as scoreCardVersion, score.score_card_date as scoreCardDate, ");
			builder.append("score.score_value as scoreCardValue from f_loan_creditbureau_score score ");
		}

		@Override
		@SuppressWarnings("unused")
		public CreditScore mapRow(ResultSet rs, int rowNum) throws SQLException {
			final String scoreName = rs.getString("scoreName");
			final String scoreCardName = rs.getString("scoreCardName");
			final String scoreCardVersion = rs.getString("scoreCardVersion");
			final Date scoreCardDate = rs.getDate("scoreCardDate");
			final String scoreCardValue = rs.getString("scoreCardValue");
			return new CreditScore(scoreName, scoreCardName, scoreCardVersion, scoreCardDate, scoreCardValue);
		}

		public String query() {
			return this.builder.toString();
		}
	}
}
