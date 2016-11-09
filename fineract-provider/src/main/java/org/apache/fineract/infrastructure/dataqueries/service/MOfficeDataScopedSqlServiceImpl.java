package org.apache.fineract.infrastructure.dataqueries.service;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
public class MOfficeDataScopedSqlServiceImpl implements DataScopedSqlService {

    @Override
    public String getDataScopedSql(final AppUser currentUser, final String apptableIdentifier) {

        final String scopedSQL = "select o.id as officeId, null as groupId, null as clientId, null as savingsId, "
                + "null as loanId, null as entityId, null as transactionId from m_office o " + " where o.hierarchy like '"
                + currentUser.getOffice().getHierarchy() + "%'" + " and o.id = " + apptableIdentifier;

        return scopedSQL;
    }
}