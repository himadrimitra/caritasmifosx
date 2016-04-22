package com.finflux.familydetail.service;

import java.util.Collection;

import com.finflux.familydetail.data.FamilyDetailData;
import com.finflux.familydetail.data.FamilyDetailTemplateData;

public interface FamilyDetailsReadPlatformService {

    Collection<FamilyDetailData> retrieveAllFamilyDetails(Long clientId);

    FamilyDetailTemplateData retrieveTemplate();

}
