package com.finflux.risk.profilerating.service;

import java.util.List;
import java.util.Map;

import com.finflux.risk.profilerating.data.ComputeProfileRatingTemplateData;

public interface ComputeProfileRatingReadPlatformService {

    ComputeProfileRatingTemplateData retrieveTemplate();

    List<Map<String, Object>> getAllClientIdsFromOffice(final Long officeId);

    List<Map<String, Object>> getAllCenterIdsFromOffice(final Long officeId);

    List<Map<String, Object>> getAllGroupIdsFromOffice(final Long officeId);

    List<Map<String, Object>> getAllVillageIdsFromOffice(Long officeId);
}