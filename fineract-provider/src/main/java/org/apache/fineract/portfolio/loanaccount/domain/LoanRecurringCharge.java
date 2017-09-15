package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargePercentagePeriodType;
import org.apache.fineract.portfolio.charge.domain.ChargePercentageType;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.common.domain.LoanPeriodFrequencyType;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_loan_recurring_charge")
public class LoanRecurringCharge extends AbstractPersistable<Long> {

    @Column(name = "charge_id")
    private Long chargeId;

    @Column(name = "amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal amount;

    @Column(name = "charge_time_enum", nullable = false)
    private Integer chargeTimeType;

    @Column(name = "charge_calculation_enum")
    private Integer chargeCalculation;

    @Column(name = "charge_payment_mode_enum", nullable = true)
    private Integer chargePaymentMode;

    @Column(name = "fee_interval", nullable = true)
    private Integer feeInterval;

    @Column(name = "is_penalty", nullable = false)
    private boolean penalty;

    @Column(name = "fee_frequency", nullable = true)
    private Integer feeFrequency;

    @Column(name = "charge_percentage_type")
    private Integer percentageType;

    @Column(name = "charge_percentage_period_type")
    private Integer percentagePeriodType;

    @Column(name = "tax_group_id")
    private Long taxGroupId;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "loanRecurringCharge", optional = true, orphanRemoval = true, fetch = FetchType.EAGER)
    private LoanChargeOverdueDetails chargeOverueDetail;

    protected LoanRecurringCharge() {

    }

    public LoanRecurringCharge(final ChargeData charge) {
        this.chargeId = charge.getId();
        this.amount = charge.getAmount();
        this.chargeTimeType = charge.getChargeTimeType();
        this.chargeCalculation = charge.getChargeCalculationType();
        this.penalty = charge.isPenalty();
        if (charge.getTaxGroup() != null) {
            this.taxGroupId = charge.getTaxGroup().getId();
        }
        this.chargePaymentMode = charge.getChargePaymentMode();
        this.feeInterval = charge.feeInterval();
        this.feeFrequency = charge.feeFrequency();
        this.percentageType = charge.getPercentageType();
        this.percentagePeriodType = charge.getPercentagePeriodType();
        if (ChargeTimeType.fromInt(this.chargeTimeType).isOverdueInstallment()) {
            this.chargeOverueDetail = new LoanChargeOverdueDetails(this, charge.getChargeOverdueData());
        }
    }

    public LoanRecurringCharge(final Long chargeId, final BigDecimal amount, final Integer chargeTimeType, final Integer chargeCalculation,
            Integer chargePaymentMode, Integer feeInterval, boolean penalty, Integer feeFrequency, Integer percentageType,
            Integer percentagePeriodType, Long taxGroupId, LoanChargeOverdueDetails chargeOverueDetail) {
        this.chargeId = chargeId;
        this.amount = amount;
        this.chargeTimeType = chargeTimeType;
        this.chargeCalculation = chargeCalculation;
        this.penalty = penalty;
        this.taxGroupId = taxGroupId;
        this.chargePaymentMode = chargePaymentMode;
        this.feeInterval = feeInterval;
        this.feeFrequency = feeFrequency;
        this.percentageType = percentageType;
        this.percentagePeriodType = percentagePeriodType;
        this.chargeOverueDetail = chargeOverueDetail;
    }

    public Long getChargeId() {
        return this.chargeId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Integer getChargeTimeType() {
        return this.chargeTimeType;
    }
    
    public ChargeTimeType chargeTimeType() {
        return ChargeTimeType.fromInt(this.chargeTimeType);
    }

    public ChargeCalculationType getChargeCalculation() {
        return ChargeCalculationType.fromInt(this.chargeCalculation);
    }

    public Integer getChargePaymentMode() {
        return this.chargePaymentMode;
    }

    public Integer getFeeInterval() {
        return this.feeInterval;
    }

    public boolean isPenalty() {
        return this.penalty;
    }

    public LoanPeriodFrequencyType getFeeFrequency() {
        return LoanPeriodFrequencyType.fromInt(this.feeFrequency);
    }

    public ChargePercentageType getPercentageType() {
        return ChargePercentageType.fromInt(this.percentageType);
    }

    public ChargePercentagePeriodType getPercentagePeriodType() {
        return ChargePercentagePeriodType.fromInt(this.percentagePeriodType);
    }

    public Long getTaxGroupId() {
        return this.taxGroupId;
    }

    public LoanChargeOverdueDetails getChargeOverueDetail() {
        return this.chargeOverueDetail;
    }

}
