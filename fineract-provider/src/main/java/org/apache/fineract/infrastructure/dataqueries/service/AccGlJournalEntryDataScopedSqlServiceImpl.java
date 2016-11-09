package org.apache.fineract.infrastructure.dataqueries.service;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
public class AccGlJournalEntryDataScopedSqlServiceImpl implements DataScopedSqlService {

    @Override
    public String getDataScopedSql(final AppUser currentUser, final String apptableIdentifier) {

        final String scopedSQL = "select o.id as officeId, null as groupId, null as clientId, null as savingsId,"
                + " null as loanId, null as entityId, j.transaction_id as transactionId from acc_gl_journal_entry j "
                + " join m_office o on o.id = j.office_id and o.hierarchy like '" + currentUser.getOffice().getHierarchy() + "%'"
                + " where j.transaction_id = '" + apptableIdentifier + "'";

        return scopedSQL;
    }
}
