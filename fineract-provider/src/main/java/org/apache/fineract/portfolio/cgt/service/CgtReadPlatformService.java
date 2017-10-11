package org.apache.fineract.portfolio.cgt.service;

import java.util.Collection;

import org.apache.fineract.portfolio.cgt.data.CgtData;

public interface CgtReadPlatformService {

    public CgtData retrievetTemplateDataOfEntity(final Long entityId);

    public CgtData retrievetCgtDataById(final Long cgtId);

    public Collection<CgtData> retrievetAllCgtDataByEntityId(final Integer entityId);

}
