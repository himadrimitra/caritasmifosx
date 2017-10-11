package org.apache.fineract.portfolio.savings.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.SavingsDpLimitCalculationType;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_savings_account_dp_details")
public class SavingsAccountDpDetails extends AbstractPersistable<Long> {

    @OneToOne
    @JoinColumn(name = "savings_id", nullable = false)
    SavingsAccount savingsAccount;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Column(name = "dp_amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal dpAmount;

    @Column(name = "calculation_type", scale = 6, precision = 19, nullable = false)
    private Integer calculationType;

    @Column(name = "amount_or_percentage", scale = 6, precision = 19, nullable = false)
    private BigDecimal amountOrPercentage;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date", nullable = true)
    protected Date startDate;

    protected SavingsAccountDpDetails() {
        //
    }

    public static SavingsAccountDpDetails createNew(final SavingsAccount savingsAccount, final Integer duration, final BigDecimal dpAmount,
            final Integer calculationType, final BigDecimal amountOrPercentage, final Date startDate) {
        return new SavingsAccountDpDetails(savingsAccount, duration, dpAmount, calculationType, amountOrPercentage, startDate);

    }

    private SavingsAccountDpDetails(final SavingsAccount savingsAccount, final Integer duration, final BigDecimal dpAmount,
            final Integer calculationType, final BigDecimal amountOrPercentage, final Date startDate) {
        this.savingsAccount = savingsAccount;
        this.duration = duration;
        this.dpAmount = dpAmount;
        this.calculationType = calculationType;
        this.amountOrPercentage = amountOrPercentage;
        this.amount = populateDerivedFields(calculationType, amountOrPercentage, dpAmount);
        this.startDate = startDate;
    }

    public BigDecimal populateDerivedFields(Integer calculationType, BigDecimal amountOrPersentage, BigDecimal dpLimitAmount) {
        BigDecimal amount = BigDecimal.ZERO;
        switch (SavingsDpLimitCalculationType.fromInt(calculationType)) {
            case FLAT:
                amount = amountOrPersentage;
            break;
            case PERCENT_OF_AMOUNT:
                amount = percentageOf(dpLimitAmount, amountOrPersentage);
            break;
            default:
            break;
        }
        return amount;
    }

    public static BigDecimal percentageOf(final BigDecimal value, final BigDecimal percentage) {

        BigDecimal percentageOf = BigDecimal.ZERO;

        if (value.compareTo(BigDecimal.ZERO) > 0) {
            final MathContext mc = new MathContext(8, MoneyHelper.getRoundingMode());
            final BigDecimal multiplicand = percentage.divide(BigDecimal.valueOf(100l), mc);
            percentageOf = value.multiply(multiplicand, mc);
        }
        return percentageOf;
    }

    public SavingsAccount getSavingsAccount() {
        return this.savingsAccount;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public BigDecimal getDpAmount() {
        return this.dpAmount;
    }

    public Integer getCalculationType() {
        return this.calculationType;
    }

    public BigDecimal getAmountOrPercentage() {
        return this.amountOrPercentage;
    }

    public void setSavingsAccount(SavingsAccount savingsAccount) {
        this.savingsAccount = savingsAccount;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDpAmount(BigDecimal dpAmount) {
        this.dpAmount = dpAmount;
    }

    public void setCalculationType(Integer calculationType) {
        this.calculationType = calculationType;
    }

    public void setAmountOrPercentage(BigDecimal amountOrPercentage) {
        this.amountOrPercentage = amountOrPercentage;
    }

    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    public LocalDate startDateOnLocalDate() {
        LocalDate startDate = null;
        if (this.startDate != null) {
            startDate = new LocalDate(this.startDate);
        }
        return startDate;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void update(final SavingsAccount savingsAccount, JsonCommand command, final Map<String, Object> actualChanges) {
        final String localeAsInput = command.locale();
        final String dateFormat = command.dateFormat();
        boolean isRecalculateDpReducationAmount = false;
        this.setSavingsAccount(savingsAccount);
        BigDecimal dpAmount = this.getDpAmount();
        if (command.isChangeInBigDecimalParameterNamedDefaultingZeroToNull(SavingsApiConstants.dpLimitAmountParamName, dpAmount)) {
            dpAmount = command.bigDecimalValueOfParameterNamedDefaultToNullIfZero(SavingsApiConstants.dpLimitAmountParamName);
            this.setDpAmount(dpAmount);
            savingsAccount.updateOverDraftLimit(dpAmount);
            isRecalculateDpReducationAmount = true;
        }

        Integer calculationType = this.getCalculationType();
        if (command.isChangeInIntegerParameterNamed(SavingsApiConstants.savingsDpLimitCalculationTypeParamName, calculationType)) {
            calculationType = command.integerValueOfParameterNamed(SavingsApiConstants.savingsDpLimitCalculationTypeParamName);
            this.setCalculationType(calculationType);
            isRecalculateDpReducationAmount = true;
        }

        if (command.isChangeInIntegerParameterNamed(SavingsApiConstants.dpDurationParamName, this.getDuration())) {
            final Integer newValue = command.integerValueOfParameterNamed(SavingsApiConstants.dpDurationParamName);
            this.setDuration(newValue);
        }

        BigDecimal amountOrPercentage = this.getAmountOrPercentage();
        if (command.isChangeInBigDecimalParameterNamedDefaultingZeroToNull(SavingsApiConstants.dpCalculateOnAmountParamName,
                amountOrPercentage)) {
            amountOrPercentage = command
                    .bigDecimalValueOfParameterNamedDefaultToNullIfZero(SavingsApiConstants.dpCalculateOnAmountParamName);
            this.setAmountOrPercentage(amountOrPercentage);
            isRecalculateDpReducationAmount = true;
        }

        if (isRecalculateDpReducationAmount) {
            this.setAmount(this.populateDerivedFields(calculationType, amountOrPercentage, dpAmount));
        }

        if (command.isChangeInLocalDateParameterNamed(SavingsApiConstants.dpStartDateParamName, this.startDateOnLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(SavingsApiConstants.dpStartDateParamName);
            actualChanges.put(SavingsApiConstants.dpStartDateParamName, valueAsInput);
            actualChanges.put(SavingsApiConstants.dateFormatParamName, dateFormat);
            actualChanges.put(SavingsApiConstants.localeParamName, localeAsInput);
            final LocalDate newValue = command.localDateValueOfParameterNamed(SavingsApiConstants.dpStartDateParamName);
            this.setStartDate(newValue.toDate());
        }
    }
}
