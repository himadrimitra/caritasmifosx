package com.finflux.familydetail.service;

import com.finflux.familydetail.data.FamilyDetailsSummaryData;

public interface FamilyDetailsSummaryReadPlatformService {

    FamilyDetailsSummaryData retrieve(final Long clientId);
}