package org.apache.fineract.infrastructure.dataqueries.service;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
public class MSavingsAccountDataScopedSqlServiceImpl implements DataScopedSqlService {

    @Override
    public String getDataScopedSql(final AppUser currentUser, final String apptableIdentifier) {

        final String scopedSQL = "select  distinctrow x.* from ("
                + " (select o.id as officeId, s.group_id as groupId, s.client_id as clientId, s.id as savingsId,"
                + " null as loanId, null as loanApplicationReferenceId, null as entityId, null as transactionId, null as villageId from m_savings_account s "
                + " join m_client c on c.id = s.client_id " + " join m_office o on o.id = c.office_id and o.hierarchy like '"
                + currentUser.getOffice().getHierarchy() + "%'" + " where s.id = " + apptableIdentifier + ")" + " union all "
                + " (select o.id as officeId, s.group_id as groupId, s.client_id as clientId, s.id as savingsId, null as loanId, null as loanApplicationReferenceId, "
                + " null as entityId, null as transactionId, null as villageId from m_savings_account s " + " join m_group g on g.id = s.group_id "
                + " join m_office o on o.id = g.office_id and o.hierarchy like '" + currentUser.getOffice().getHierarchy() + "%'"
                + " where s.id = " + apptableIdentifier + ")" + " ) x";

        return scopedSQL;
    }
}
