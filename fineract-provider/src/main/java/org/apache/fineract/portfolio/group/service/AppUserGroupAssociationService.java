package org.apache.fineract.portfolio.group.service;

public interface AppUserGroupAssociationService {

    Boolean hasAccessToGroup(Long groupId, Long appUserId);

}
