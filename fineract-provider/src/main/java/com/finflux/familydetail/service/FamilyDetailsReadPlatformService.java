package com.finflux.familydetail.service;

import java.util.Collection;

import com.finflux.familydetail.data.FamilyDetailData;
import com.finflux.familydetail.data.FamilyDetailTemplateData;

public interface FamilyDetailsReadPlatformService {

    FamilyDetailTemplateData retrieveTemplate();

    Collection<FamilyDetailData> retrieveAllFamilyDetails(final Long clientId);

    FamilyDetailData retrieveOneFamilyDetail(final Long familyDetailsId);
}