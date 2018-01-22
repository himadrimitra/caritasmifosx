package com.finflux.portfolio.investmenttracker.service;

import java.util.Arrays;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.data.InvestmentCompoundingInterestPeriodType;

@Service
public class InvestmentTrackerDropDownReadServiceImpl implements InvestmentTrackerDropDownReadService {

    public InvestmentTrackerDropDownReadServiceImpl() {

    }

    @Override
    public List<EnumOptionData> retrieveInterestRateFrequencyTypeOptions() {
        // support for monthly and annual percentage rate (MPR) and (APR)
        final List<EnumOptionData> interestRateFrequencyTypeOptions = Arrays.asList(
                InvestmentTrackerEnumerations.interestRateFrequencyType(PeriodFrequencyType.YEARS));
        return interestRateFrequencyTypeOptions;
    }

    @Override
    public List<EnumOptionData> retrieveCompoundingInterestPeriodTypeOptions() {
        return Arrays.asList(
        		InvestmentTrackerEnumerations.compoundingInterestPeriodType(InvestmentCompoundingInterestPeriodType.NONE),
                InvestmentTrackerEnumerations.compoundingInterestPeriodType(InvestmentCompoundingInterestPeriodType.DAILY), //
                InvestmentTrackerEnumerations.compoundingInterestPeriodType(InvestmentCompoundingInterestPeriodType.MONTHLY),
                InvestmentTrackerEnumerations.compoundingInterestPeriodType(InvestmentCompoundingInterestPeriodType.QUATERLY),
                InvestmentTrackerEnumerations.compoundingInterestPeriodType(InvestmentCompoundingInterestPeriodType.BI_ANNUAL),
                InvestmentTrackerEnumerations.compoundingInterestPeriodType(InvestmentCompoundingInterestPeriodType.ANNUAL));
    }

    @Override
    public List<EnumOptionData> retrieveInvestmentTermFrequencyTypeOptions() {

        final List<EnumOptionData> repaymentFrequencyOptions = Arrays.asList(
                InvestmentTrackerEnumerations.investmentTermFrequencyType(PeriodFrequencyType.DAYS));
        return repaymentFrequencyOptions;
    }

}
