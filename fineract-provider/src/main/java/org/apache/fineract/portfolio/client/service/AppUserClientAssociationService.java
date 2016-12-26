package org.apache.fineract.portfolio.client.service;

public interface AppUserClientAssociationService {

    Boolean hasAccessToClient(Long clientId, Long appUserId);

}
