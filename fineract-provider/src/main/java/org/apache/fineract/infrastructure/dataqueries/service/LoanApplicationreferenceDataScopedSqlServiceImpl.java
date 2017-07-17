
/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.infrastructure.dataqueries.service;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
public class LoanApplicationreferenceDataScopedSqlServiceImpl implements DataScopedSqlService {

    @Override
    public String getDataScopedSql(final AppUser currentUser, final String apptableIdentifier) {
        final StringBuilder sqlBuilder = new StringBuilder("select  distinctrow x.* from (");
        sqlBuilder.append(" (select o.id as officeId, l.group_id as groupId, l.client_id as clientId, null as savingsId, ");
        sqlBuilder.append("l.id as loanApplicationReferenceId, null as loanId, null as entityId, null as transactionId from f_loan_application_reference l " + " join m_client c on c.id = l.client_id ");
        sqlBuilder.append(" join m_office o on o.id = c.office_id and o.hierarchy like '" + currentUser.getOffice().getHierarchy() + "%'");
        sqlBuilder.append(" where l.id = " + apptableIdentifier + ")" + " union all ");
        sqlBuilder.append(" (select o.id as officeId, l.group_id as groupId, l.client_id as clientId, null as savingsId, l.id as loanApplicationReferenceId, null as loanId,");
        sqlBuilder.append(" null as entityId, null as transactionId from f_loan_application_reference l " + " join m_group g on g.id = l.group_id ");
        sqlBuilder.append(" join m_office o on o.id = g.office_id and o.hierarchy like '" + currentUser.getOffice().getHierarchy() + "%'");
        sqlBuilder.append(" where l.id = " + apptableIdentifier + ")" +" ) x");
        return sqlBuilder.toString();
    }

}
