package com.finflux.risk.creditbureau.provider.data;

import java.util.Date;

public class CreditScore {

	private final String scoreName;
	private final String scoreCardName;
	private final String scoreCardVersion;
	private final Date scoreCardDate;
	private final String score;

	public CreditScore(final String scoreName, String scoreCardName, String scoreCardVersion, Date scoreCardDate,
			String score) {
		this.scoreName = scoreName;
		this.scoreCardName = scoreCardName;
		this.scoreCardVersion = scoreCardVersion;
		this.scoreCardDate = scoreCardDate;
		this.score = score;
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

	public String getScore() {
		return this.score;
	}
}
