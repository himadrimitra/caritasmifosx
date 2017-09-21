/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.infrastructure.dataqueries.service;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
public class DistrictDataScopedSqlServiceImpl implements DataScopedSqlService {

    @Override
    public String getDataScopedSql(final AppUser currentUser, final String apptableIdentifier) {

        final String scopedSQL = "select o.id as officeId, null as groupId, null as villageId, null as clientId, null as savingsId, null as loanId,d.id as districtId,"
                + " d.id as entityId, null as transactionId , null as loanApplicationReferenceId from f_district d  "
                + " join m_office o on o.id ="+ currentUser.getOffice().getId() +" and o.hierarchy like '" + currentUser.getOffice().getHierarchy() + "%'"
                + " where d.id = " + apptableIdentifier;

        return scopedSQL;
    }
}
