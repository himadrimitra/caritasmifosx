package com.finflux.portfolio.investmenttracker.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.savings.SavingsCompoundingInterestPeriodType;

import com.finflux.portfolio.investmenttracker.data.InvestmentCompoundingInterestPeriodType;

public class InvestmentTrackerEnumerations {

    public static EnumOptionData interestRateFrequencyType(final Integer type) {
        return interestRateFrequencyType(PeriodFrequencyType.fromInt(type));
    }

    public static EnumOptionData interestRateFrequencyType(final PeriodFrequencyType type) {
        final String codePrefix = "interestRateFrequency.";
        EnumOptionData optionData = null;
        switch (type) {
            case MONTHS:
                optionData = new EnumOptionData(PeriodFrequencyType.MONTHS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.MONTHS.getCode(), "Per month");
            break;
            case YEARS:
                optionData = new EnumOptionData(PeriodFrequencyType.YEARS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.YEARS.getCode(), "Per year");
            break;
            default:
                optionData = new EnumOptionData(PeriodFrequencyType.INVALID.getValue().longValue(), PeriodFrequencyType.INVALID.getCode(),
                        "Invalid");
            break;
        }
        return optionData;
    }

    public static EnumOptionData compoundingInterestPeriodType(final Integer type) {
        return compoundingInterestPeriodType(InvestmentCompoundingInterestPeriodType.fromInt(type));
    }

    public static EnumOptionData compoundingInterestPeriodType(final InvestmentCompoundingInterestPeriodType type) {

        final String codePrefix = "investment.compounding.period.";
        EnumOptionData optionData = new EnumOptionData(InvestmentCompoundingInterestPeriodType.INVALID.getValue().longValue(),
                InvestmentCompoundingInterestPeriodType.INVALID.getCode(), "Invalid");

        switch (type) {
            case INVALID:
            break;
            case DAILY:
                optionData = new EnumOptionData(InvestmentCompoundingInterestPeriodType.DAILY.getValue().longValue(), codePrefix
                        + InvestmentCompoundingInterestPeriodType.DAILY.getCode(), "Daily");
            break;
            case MONTHLY:
                optionData = new EnumOptionData(InvestmentCompoundingInterestPeriodType.MONTHLY.getValue().longValue(), codePrefix
                        + InvestmentCompoundingInterestPeriodType.MONTHLY.getCode(), "Monthly");
            break;
            case QUATERLY:
                optionData = new EnumOptionData(SavingsCompoundingInterestPeriodType.QUATERLY.getValue().longValue(), codePrefix
                        + InvestmentCompoundingInterestPeriodType.QUATERLY.getCode(), "Quarterly");
            break;
            case BI_ANNUAL:
                optionData = new EnumOptionData(InvestmentCompoundingInterestPeriodType.BI_ANNUAL.getValue().longValue(), codePrefix
                        + InvestmentCompoundingInterestPeriodType.BI_ANNUAL.getCode(), "Semi-Annual");
            break;
            case ANNUAL:
                optionData = new EnumOptionData(InvestmentCompoundingInterestPeriodType.ANNUAL.getValue().longValue(), codePrefix
                        + InvestmentCompoundingInterestPeriodType.ANNUAL.getCode(), "Annually");
            break;
        }

        return optionData;
    }

    public static EnumOptionData investmentTermFrequencyType(final Integer type) {
        return investmentTermFrequencyType(PeriodFrequencyType.fromInt(type));
    }

    public static EnumOptionData investmentTermFrequencyType(final PeriodFrequencyType type) {
        final String codePrefix = "investmentTermFrequency.";
        EnumOptionData optionData = null;
        switch (type) {
            case DAYS:
                optionData = new EnumOptionData(PeriodFrequencyType.DAYS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.DAYS.getCode(), "Days");
            break;
            case WEEKS:
                optionData = new EnumOptionData(PeriodFrequencyType.WEEKS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.WEEKS.getCode(), "Weeks");
            break;
            case MONTHS:
                optionData = new EnumOptionData(PeriodFrequencyType.MONTHS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.MONTHS.getCode(), "Months");
            break;
            case YEARS:
                optionData = new EnumOptionData(PeriodFrequencyType.YEARS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.YEARS.getCode(), "Years");
            break;
            default:
                optionData = new EnumOptionData(PeriodFrequencyType.INVALID.getValue().longValue(), PeriodFrequencyType.INVALID.getCode(),
                        "Invalid");
            break;
        }
        return optionData;
    }

}
