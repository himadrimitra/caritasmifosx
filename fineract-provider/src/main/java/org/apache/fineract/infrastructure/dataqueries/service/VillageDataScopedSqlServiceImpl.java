/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.infrastructure.dataqueries.service;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
public class VillageDataScopedSqlServiceImpl implements DataScopedSqlService {

    @Override
    public String getDataScopedSql(final AppUser currentUser, final String apptableIdentifier) {

        final String scopedSQL = "select o.id as officeId, null as groupId, v.id as villageId, null as clientId, null as savingsId, null as loanId,"
                + " v.id as entityId, null as transactionId , null as loanApplicationReferenceId from chai_villages v  "
                + " join m_office o on o.id = v.office_id and o.hierarchy like '" + currentUser.getOffice().getHierarchy() + "%'"
                + " where v.id = " + apptableIdentifier;

        return scopedSQL;
    }
}
