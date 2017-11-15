package com.finflux.portfolio.investmenttracker.service;

import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public interface InvestmentTrackerDropDownReadService {

    List<EnumOptionData> retrieveInterestRateFrequencyTypeOptions();

    List<EnumOptionData> retrieveCompoundingInterestPeriodTypeOptions();

    List<EnumOptionData> retrieveInvestmentTermFrequencyTypeOptions();
}
