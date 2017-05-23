package org.apache.fineract.portfolio.collectionsheet.command;

import java.math.BigDecimal;

public class ClientChargeRepaymentCommand {
	private final Long clientId;
    private final Long chargeId;
    private final BigDecimal transactionAmount;
    
    
    public ClientChargeRepaymentCommand(Long clientId, Long chargeId,
			BigDecimal transactionAmount) {
		this.clientId = clientId;
		this.chargeId = chargeId;
		this.transactionAmount = transactionAmount;
	}
    
	public Long getClientId() {
		return this.clientId;
	}

	public Long getChargeId() {
		return this.chargeId;
	}

	public BigDecimal getTransactionAmount() {
		return this.transactionAmount;
	}
}
