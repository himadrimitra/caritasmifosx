package com.finflux.portfolio.investmenttracker.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;


public enum InvestmentAccountStatus {

    INVALID(0, "investmentAccountStatus.invalid"), //
    PENDING_APPROVAL(100, "investmentAccountStatus.pendingapproval"), //
    APPROVED(200, "investmentAccountStatus.approved"), //
    REJECTED(300, "investmentAccountStatus.rejected"), //
    ACTIVE(400, "investmentAccountStatus.active"), //
    MATURED(500, "investmentAccountStatus.matured"),//
    CLOSED(600, "investmentAccountStatus.closed"),//
    REINVESTED(700, "investmentAccountStatus.reinvested");

    private final Integer value;
    private final String code;
    
    private InvestmentAccountStatus(final Integer value, final String code){
        this.value = value;
        this.code = code;
    }
    
    private static final Map<Integer, InvestmentAccountStatus> intToEnumMap = new HashMap<>();
    private static final Map<String, InvestmentAccountStatus> statusNameToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;
    static {
        int i = 0;
        for (final InvestmentAccountStatus status : InvestmentAccountStatus.values()) {
            if (i == 0) {
                minValue = status.value;
            }
            intToEnumMap.put(status.value, status);
            statusNameToEnumMap.put(status.name().toLowerCase(), status);
            if (minValue >= status.value) {
                minValue = status.value;
            }
            if (maxValue < status.value) {
                maxValue = status.value;
            }
            i = i + 1;

        }

    }
    
    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.name());
    }

    public static Collection<EnumOptionData> investmentAccountStatusTypeOptions() {
        final Collection<EnumOptionData> investmetnAccountStatusOptions = new ArrayList<>();
        for (final InvestmentAccountStatus enumType : values()) {
            final EnumOptionData enumOptionData = enumType.getEnumOptionData();
            if (enumOptionData != null) {
                investmetnAccountStatusOptions.add(enumOptionData);
            }
        }
        return investmetnAccountStatusOptions;
    }
    
    public static InvestmentAccountStatus fromInt(final int i) {
        final InvestmentAccountStatus entityType = intToEnumMap.get(Integer.valueOf(i));
        return entityType;
    }

    public static InvestmentAccountStatus fromString(final String str) {
        final InvestmentAccountStatus entityType = statusNameToEnumMap.get(str.toLowerCase());
        return entityType;
    }

    
    public Integer getValue() {
        return this.value;
    }

    
    public String getCode() {
        return this.code;
    }
    
}
