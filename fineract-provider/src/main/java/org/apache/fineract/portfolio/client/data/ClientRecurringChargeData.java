package org.apache.fineract.portfolio.client.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.joda.time.LocalDate;

public class ClientRecurringChargeData {
	
    private final Long recurringChargeId;

    private final Long clientId;
    
    private OfficeData officeData;

    private final Long chargeId;

    private final String chargename;

    private final LocalDate chargeDueDate;

    private final CurrencyData currency;

    private final EnumOptionData chargeAppliesTo;

    private final EnumOptionData chargeTimeType;

    private final EnumOptionData chargeCalculationType;

    private final EnumOptionData chargePaymentMode;

    private final BigDecimal amount;

    private final Integer feeOnDay;

    private final Integer feeInterval;

    private final Integer feeOnMonth;

    private final boolean penalty;

    private final Boolean active;

    private final Boolean deleted;

    private final Boolean synchMeeting;

    private final BigDecimal minCap;

    private final BigDecimal maxCap;

    private final Integer feeFrequency;

    private final LocalDate inactivatedOnDate;
    
    private final Integer countOfExistingFutureInstallments;

    public ClientRecurringChargeData(Long id, Long clientId, Long chargeId, String chargename, LocalDate chargeDueDate,
            CurrencyData currencyCode, EnumOptionData chargeAppliesTo, EnumOptionData chargeTime, EnumOptionData chargeCalculation,
            EnumOptionData chargePaymentMode, BigDecimal amount, Integer feeOnDay, Integer feeInterval,
            Integer feeOnMonth, boolean penalty, Boolean isActive, Boolean isDeleted, Boolean issynchMeeting, BigDecimal minCap,
            BigDecimal maxCap, Integer feeFrequency, LocalDate inactivatedOnDate,final Integer countOfExistingFutureInstallments) {
        this.recurringChargeId = id;
        this.clientId = clientId;
        this.chargeId = chargeId;
        this.chargename = chargename;
        this.chargeDueDate = chargeDueDate;
        this.currency = currencyCode;
        this.chargeAppliesTo = chargeAppliesTo;
        this.chargeTimeType = chargeTime;
        this.chargeCalculationType = chargeCalculation;
        this.chargePaymentMode = chargePaymentMode;
        this.amount = amount;
        this.feeOnDay = feeOnDay;
        this.feeInterval = feeInterval;
        this.feeOnMonth = feeOnMonth;
        this.penalty = penalty;
        this.active = isActive;
        this.deleted = isDeleted;
        this.synchMeeting = issynchMeeting;
        this.minCap = minCap;
        this.maxCap = maxCap;
        this.feeFrequency = feeFrequency;
        this.inactivatedOnDate = inactivatedOnDate;
        this.countOfExistingFutureInstallments = countOfExistingFutureInstallments;
    }
    
    public ClientRecurringChargeData(final Long id, final OfficeData officeData, final LocalDate chargeDueDate,
            final EnumOptionData chargeTimeType, final Integer feeInterval, Boolean issynchMeeting, final Integer countOfExistingFutureInstallments) {
        this.recurringChargeId = id;
        this.clientId = null;
        this.officeData = officeData;
        this.chargeId = null;
        this.chargename = null;
        this.chargeDueDate = chargeDueDate;
        this.currency = null;
        this.chargeAppliesTo = null;
        this.chargeTimeType = chargeTimeType;
        this.chargeCalculationType = null;
        this.chargePaymentMode = null;
        this.amount = null;
        this.feeOnDay = null;
        this.feeInterval = feeInterval;
        this.feeOnMonth = null;
        this.penalty = false;
        this.active = null;
        this.deleted = null;
        this.synchMeeting = issynchMeeting;
        this.minCap = null;
        this.maxCap = null;
        this.feeFrequency = null;
        this.inactivatedOnDate = null;
        this.countOfExistingFutureInstallments = countOfExistingFutureInstallments;
    }

    
    public Long getRecurringChargeId() {
        return this.recurringChargeId;
    }

    
    public Long getClientId() {
        return this.clientId;
    }

    
    public Long getChargeId() {
        return this.chargeId;
    }

    
    public String getChargename() {
        return this.chargename;
    }

    
    public LocalDate getChargeDueDate() {
        return this.chargeDueDate;
    }

    
    public CurrencyData getCurrency() {
        return this.currency;
    }

    
    public EnumOptionData getChargeAppliesTo() {
        return this.chargeAppliesTo;
    }

    
    public EnumOptionData getChargeTimeType() {
        return this.chargeTimeType;
    }

    
    public EnumOptionData getChargeCalculationType() {
        return this.chargeCalculationType;
    }

    
    public EnumOptionData getChargePaymentMode() {
        return this.chargePaymentMode;
    }

    
    public BigDecimal getAmount() {
        return this.amount;
    }

    
    public Integer getFeeOnDay() {
        return this.feeOnDay;
    }

    
    public Integer getFeeInterval() {
        return this.feeInterval;
    }

    
    public Integer getFeeOnMonth() {
        return this.feeOnMonth;
    }

    
    public boolean isPenalty() {
        return this.penalty;
    }

    
    public Boolean getActive() {
        return this.active;
    }

    
    public Boolean getDeleted() {
        return this.deleted;
    }

    
    public Boolean getSynchMeeting() {
        return this.synchMeeting;
    }

    
    public BigDecimal getMinCap() {
        return this.minCap;
    }

    
    public BigDecimal getMaxCap() {
        return this.maxCap;
    }

    
    public Integer getFeeFrequency() {
        return this.feeFrequency;
    }

    
    public LocalDate getInactivatedOnDate() {
        return this.inactivatedOnDate;
    }

    public Integer getCountOfExistingFutureInstallments() {
        return this.countOfExistingFutureInstallments;
    }

    public OfficeData getOfficeData() {
        return this.officeData;
    }
    
}
