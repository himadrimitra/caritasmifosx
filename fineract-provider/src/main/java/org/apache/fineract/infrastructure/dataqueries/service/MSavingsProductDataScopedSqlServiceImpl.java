package org.apache.fineract.infrastructure.dataqueries.service;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
public class MSavingsProductDataScopedSqlServiceImpl implements DataScopedSqlService {

    @Override
    public String getDataScopedSql(final AppUser currentUser, final String apptableIdentifier) {

        final String scopedSQL = "select null as officeId, null as groupId, null as clientId, null as savingsId,"
                + " null as loanId, null as loanApplicationReferenceId, p.id as entityId, null as transactionId, null as villageId from m_savings_product as p WHERE p.id = " + apptableIdentifier;

        return scopedSQL;
    }
}
