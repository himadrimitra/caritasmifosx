package org.apache.fineract.infrastructure.dataqueries.service;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
public class MClientDataScopedSqlServiceImpl implements DataScopedSqlService {

    @Override
    public String getDataScopedSql(final AppUser currentUser, final String apptableIdentifier) {

        final String scopedSQL = "select o.id as officeId, null as groupId, c.id as clientId, null as savingsId,"
                + " null as loanId, null as loanApplicationReferenceId, null as entityId, null as transactionId from m_client c "
                + " join m_office o on o.id = c.office_id and o.hierarchy like '" + currentUser.getOffice().getHierarchy() + "%'"
                + " where c.id = " + apptableIdentifier;

        return scopedSQL;
    }
}
