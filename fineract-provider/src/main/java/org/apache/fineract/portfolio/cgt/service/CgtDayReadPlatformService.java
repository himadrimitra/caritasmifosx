package org.apache.fineract.portfolio.cgt.service;

import java.util.Collection;

import org.apache.fineract.portfolio.cgt.data.CgtDayClientData;
import org.apache.fineract.portfolio.cgt.data.CgtDayData;

public interface CgtDayReadPlatformService {

    public CgtDayData retrievetCgtDayDataById(final Long cgtDayId);
    
    Collection<CgtDayClientData> getCgtDayClient(Long cgtDayId);

}
