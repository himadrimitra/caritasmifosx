package org.apache.fineract.portfolio.client.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.client.api.ClientAccountLimitsApiConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_client_account_limits")
public class ClientAccountLimitDetails extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "total_disbursement_amount_limit", scale = 6, precision = 19, nullable = true)
    private BigDecimal limitOnTotalDisbursementAmount;

    @Column(name = "total_loan_outstanding_amount_limit", scale = 6, precision = 19, nullable = true)
    private BigDecimal limitOnTotalLoanOutstandingAmount;

    @Column(name = "daily_withdrawal_amount_limit", scale = 6, precision = 19, nullable = true)
    private BigDecimal dailyWithdrawalLimit;

    @Column(name = "daily_transfer_amount_limit", scale = 6, precision = 19, nullable = true)
    private BigDecimal dailyTransferLimit;

    @Column(name = "total_overdraft_amount_limit", scale = 6, precision = 19, nullable = true)
    private BigDecimal limitOnTotalOverdraftAmount;

    protected ClientAccountLimitDetails() {}

    public ClientAccountLimitDetails(final Client client, final BigDecimal limitOnTotalDisbursementAmount,
            final BigDecimal limitOnTotalLoanOutstandingAmount, final BigDecimal dailyWithdrawalLimit, final BigDecimal dailyTransferLimit,
            final BigDecimal limitOnTotalOverdraftAmount) {
        this.client = client;
        this.limitOnTotalDisbursementAmount = limitOnTotalDisbursementAmount;
        this.limitOnTotalLoanOutstandingAmount = limitOnTotalLoanOutstandingAmount;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
        this.dailyTransferLimit = dailyTransferLimit;
        this.limitOnTotalOverdraftAmount = limitOnTotalOverdraftAmount;
    }

    public static ClientAccountLimitDetails assembleFromJson(final JsonCommand command, final Client client) {

        final BigDecimal limitOnTotalDisbursementAmount = command
                .bigDecimalValueOfParameterNamed(ClientAccountLimitsApiConstants.limitOnTotalDisbursementAmountParamName);
        final BigDecimal limitOnTotalLoanOutstandingAmount = command
                .bigDecimalValueOfParameterNamed(ClientAccountLimitsApiConstants.limitOnTotalLoanOutstandingAmountParamName);
        final BigDecimal dailyWithdrawalLimit = command
                .bigDecimalValueOfParameterNamed(ClientAccountLimitsApiConstants.dailyWithdrawalLimitParamName);
        final BigDecimal dailyTransferLimit = command
                .bigDecimalValueOfParameterNamed(ClientAccountLimitsApiConstants.dailyTransferLimitParamName);
        final BigDecimal limitOnTotalOverdraftAmount = command
                .bigDecimalValueOfParameterNamed(ClientAccountLimitsApiConstants.limitOnTotalOverdraftAmountParamName);

        return new ClientAccountLimitDetails(client, limitOnTotalDisbursementAmount, limitOnTotalLoanOutstandingAmount,
                dailyWithdrawalLimit, dailyTransferLimit, limitOnTotalOverdraftAmount);
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new HashMap<>();
        if (command.isChangeInBigDecimalParameterNamed(ClientAccountLimitsApiConstants.limitOnTotalDisbursementAmountParamName,
                this.limitOnTotalDisbursementAmount)) {
            final BigDecimal newValue = command
                    .bigDecimalValueOfParameterNamed(ClientAccountLimitsApiConstants.limitOnTotalDisbursementAmountParamName);
            actualChanges.put(ClientAccountLimitsApiConstants.limitOnTotalDisbursementAmountParamName, newValue);
            this.limitOnTotalDisbursementAmount = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientAccountLimitsApiConstants.limitOnTotalLoanOutstandingAmountParamName,
                this.limitOnTotalLoanOutstandingAmount)) {
            final BigDecimal newValue = command
                    .bigDecimalValueOfParameterNamed(ClientAccountLimitsApiConstants.limitOnTotalLoanOutstandingAmountParamName);
            actualChanges.put(ClientAccountLimitsApiConstants.limitOnTotalLoanOutstandingAmountParamName, newValue);
            this.limitOnTotalLoanOutstandingAmount = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientAccountLimitsApiConstants.dailyWithdrawalLimitParamName,
                this.dailyWithdrawalLimit)) {
            final BigDecimal newValue = command
                    .bigDecimalValueOfParameterNamed(ClientAccountLimitsApiConstants.dailyWithdrawalLimitParamName);
            actualChanges.put(ClientAccountLimitsApiConstants.dailyWithdrawalLimitParamName, newValue);
            this.dailyWithdrawalLimit = newValue;
        }

        if (command
                .isChangeInBigDecimalParameterNamed(ClientAccountLimitsApiConstants.dailyTransferLimitParamName, this.dailyTransferLimit)) {
            final BigDecimal newValue = command
                    .bigDecimalValueOfParameterNamed(ClientAccountLimitsApiConstants.dailyTransferLimitParamName);
            actualChanges.put(ClientAccountLimitsApiConstants.dailyTransferLimitParamName, newValue);
            this.dailyTransferLimit = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ClientAccountLimitsApiConstants.limitOnTotalOverdraftAmountParamName,
                this.limitOnTotalOverdraftAmount)) {
            final BigDecimal newValue = command
                    .bigDecimalValueOfParameterNamed(ClientAccountLimitsApiConstants.limitOnTotalOverdraftAmountParamName);
            actualChanges.put(ClientAccountLimitsApiConstants.limitOnTotalOverdraftAmountParamName, newValue);
            this.limitOnTotalOverdraftAmount = newValue;
        }
        return actualChanges;
    }

    public BigDecimal getLimitOnTotalDisbursementAmount() {
        return this.limitOnTotalDisbursementAmount;
    }

    public BigDecimal getLimitOnTotalLoanOutstandingAmount() {
        return this.limitOnTotalLoanOutstandingAmount;
    }

    public BigDecimal getDailyWithdrawalLimit() {
        return this.dailyWithdrawalLimit;
    }

    public BigDecimal getDailyTransferLimit() {
        return this.dailyTransferLimit;
    }

    public BigDecimal getLimitOnTotalOverdraftAmount() {
        return this.limitOnTotalOverdraftAmount;
    }

}
