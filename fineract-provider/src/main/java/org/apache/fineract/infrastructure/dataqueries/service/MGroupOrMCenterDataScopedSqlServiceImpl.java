package org.apache.fineract.infrastructure.dataqueries.service;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
public class MGroupOrMCenterDataScopedSqlServiceImpl implements DataScopedSqlService {

    @Override
    public String getDataScopedSql(final AppUser currentUser, final String apptableIdentifier) {

        final String scopedSQL = "select o.id as officeId, g.id as groupId, null as clientId, null as savingsId, null as loanId,"
                + " null as entityId, null as transactionId , null as loanApplicationReferenceId, null as villageId from m_group g "
                + " join m_office o on o.id = g.office_id and o.hierarchy like '" + currentUser.getOffice().getHierarchy() + "%'"
                + " where g.id = " + apptableIdentifier;

        return scopedSQL;
    }
}
