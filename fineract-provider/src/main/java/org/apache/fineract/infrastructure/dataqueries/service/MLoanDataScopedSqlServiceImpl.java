package org.apache.fineract.infrastructure.dataqueries.service;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
public class MLoanDataScopedSqlServiceImpl implements DataScopedSqlService {

    @Override
    public String getDataScopedSql(final AppUser currentUser, final String apptableIdentifier) {

        final String scopedSQL = "select  distinctrow x.* from ("
                + " (select o.id as officeId, l.group_id as groupId, l.client_id as clientId, null as savingsId, "
                + "l.id as loanId, null as loanApplicationReferenceId, null as entityId, null as transactionId, null as villageId from m_loan l " + " join m_client c on c.id = l.client_id "
                + " join m_office o on o.id = c.office_id and o.hierarchy like '" + currentUser.getOffice().getHierarchy() + "%'"
                + " where l.id = " + apptableIdentifier + ")" + " union all "
                + " (select o.id as officeId, l.group_id as groupId, l.client_id as clientId, null as savingsId, l.id as loanId, null as loanApplicationReferenceId, "
                + " null as entityId, null as transactionId from m_loan l " + " join m_group g on g.id = l.group_id "
                + " join m_office o on o.id = g.office_id and o.hierarchy like '" + currentUser.getOffice().getHierarchy() + "%'"
                + " where l.id = " + apptableIdentifier + ")" + " ) x";

        return scopedSQL;
    }

}
