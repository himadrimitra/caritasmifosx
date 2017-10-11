package com.finflux.risk.creditbureau.provider.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.risk.creditbureau.provider.data.CreditScore;

@Entity
@Table(name = "f_loan_creditbureau_score")
public class CreditBureauScore extends AbstractPersistable<Long> {

	@ManyToOne
	@JoinColumn(name = "loan_creditbureau_enquiry_id", nullable = false)
	private LoanCreditBureauEnquiry loanCreditBureauEnquiry;

	@Column(name = "score_name", nullable = true)
	private String scoreName;

	@Column(name = "score_card_name", nullable = true)
	private String scoreCardName;

	@Column(name = "score_card_version", nullable = true)
	private String scoreCardVersion;

	@Temporal(TemporalType.DATE)
	@Column(name = "score_card_date", nullable = true)
	private Date scoreCardDate;

	@Column(name = "score_value", nullable = false)
	private String scoreValue; //In CIBIL it is string 00-1

	protected CreditBureauScore() {

	}

	public CreditBureauScore(final LoanCreditBureauEnquiry loanCreditBureauEnquiry, final String scoreName,
			final String scoreCardName, final String scoreCardVersion, final Date scoreCardDate,
			final String scoreValue) {
		super();
		this.loanCreditBureauEnquiry = loanCreditBureauEnquiry;
		this.scoreName = scoreName;
		this.scoreCardName = scoreCardName;
		this.scoreCardVersion = scoreCardVersion;
		this.scoreCardDate = scoreCardDate;
		this.scoreValue = scoreValue;
	}

	public CreditBureauScore(final LoanCreditBureauEnquiry loanCreditBureauEnquiry, final CreditScore score) {
		this.loanCreditBureauEnquiry = loanCreditBureauEnquiry ;
		this.scoreName = score.getScoreName() ;
		this.scoreCardName = score.getScoreCardName() ;
		this.scoreCardVersion = score.getScoreCardVersion() ;
		this.scoreCardDate = score.getScoreCardDate() ;
		this.scoreValue = score.getScore() ;
	}

	public LoanCreditBureauEnquiry getLoanCreditBureauEnquiry() {
		return this.loanCreditBureauEnquiry;
	}

	public String getScoreName() {
		return this.scoreName;
	}

	public String getScoreCardName() {
		return this.scoreCardName;
	}

	public String getScoreCardVersion() {
		return this.scoreCardVersion;
	}

	public Date getScoreCardDate() {
		return this.scoreCardDate;
	}

	public String getScoreValue() {
		return this.scoreValue;
	}
}
