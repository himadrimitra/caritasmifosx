package org.apache.fineract.infrastructure.dataqueries.service;

import org.apache.fineract.useradministration.domain.AppUser;

public interface DataScopedSqlService {

    String getDataScopedSql(final AppUser currentUser, final String apptableIdentifier);
}
