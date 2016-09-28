package org.apache.fineract.portfolio.client.service;

import java.util.List;

import org.apache.fineract.portfolio.client.data.ClientRecurringChargeData;

public interface ClientRecurringChargeReadPlatformService {
    
    List<ClientRecurringChargeData> retrieveRecurringClientCharges(Long clientId);
    
    ClientRecurringChargeData retriveRecurringClientCharge(Long clientId,Long recurringChargeId);
    
    List<ClientRecurringChargeData> retriveActiveRecurringChargesForJob();
}
