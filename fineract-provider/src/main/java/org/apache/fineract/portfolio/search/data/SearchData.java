/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.search.data;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.reconcilation.bank.data.BankData;

@SuppressWarnings("unused")
public class SearchData {

    private final Long entityId;
    private final String entityAccountNo;
    private final String entityExternalId;
    private final String entityName;
    private final String entityType;
    private final Long parentId;
    private final String parentName;
    private final String entityMobileNo;
    private final EnumOptionData entityStatus;
    private final String entityNationalId;
    private final String parentType;
    private final BigDecimal systemValue;
    private final BigDecimal userValue;
    private final String groupName;
    private final String centerName;
    private final String officeName;
    private final CodeValueData reason;
    private final Boolean isActive;
    private final Boolean isLoanOfficer;
    private final String description;
    private final Long cpifKeyDocumentId;
    private final Long orgStatementKeyDocumentId;
    private final String cpifFileName;
    private final String orgFileName;
    private final String lastModifiedByName;
    private final Date lastModifiedDate;
    private final Boolean isReconciled;
    private final BankData bankData;

    public SearchData(final Long entityId, final String entityAccountNo, final String entityExternalId, final String entityName,
            final String entityType, final Long parentId, final String parentName, final String parentType, final String entityMobileNo,
            final EnumOptionData entityStatus, final BigDecimal systemValue, final BigDecimal userValue, final String groupName,
            final String centerName, final String officeName, final CodeValueData reason, final Boolean isActive,
            final Boolean isLoanOfficer, final String description, final Long cpifKeyDocumentId, final Long orgStatementKeyDocumentId,
            final String lastModifiedByName, final Date lastModifiedDate, final Boolean isReconciled, final BankData bankData,
            final String cpifFileName, final String orgFileName, final String entityNationalId) {

        this.entityId = entityId;
        this.entityAccountNo = entityAccountNo;
        this.entityExternalId = entityExternalId;
        this.entityName = entityName;
        this.entityType = entityType;
        this.parentId = parentId;
        this.parentName = parentName;
        this.entityNationalId = entityNationalId;
        this.parentType = parentType;
        this.entityMobileNo = entityMobileNo;
        this.entityStatus = entityStatus;
        this.systemValue = systemValue;
        this.userValue = userValue;
        this.groupName = groupName;
        this.centerName = centerName;
        this.officeName = officeName;
        this.reason = reason;
        this.isActive = isActive;
        this.isLoanOfficer = isLoanOfficer;
        this.description = description;
        this.cpifKeyDocumentId = cpifKeyDocumentId;
        this.orgStatementKeyDocumentId = orgStatementKeyDocumentId;
        this.lastModifiedByName = lastModifiedByName;
        this.cpifFileName = cpifFileName;
        this.orgFileName = orgFileName;
        this.bankData = bankData;
        this.isReconciled = isReconciled;
        this.lastModifiedDate = lastModifiedDate;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public String getEntityAccountNo() {
        return this.entityAccountNo;
    }

    public String getEntityExternalId() {
        return this.entityExternalId;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public String getParentName() {
        return this.parentName;
    }

    public String getEntityNationalId() {
        return this.entityNationalId;
    }

    public String getParentType() {
        return this.parentType;
    }

    public String getEntityMobileNo() {
        return this.entityMobileNo;
    }

    public EnumOptionData getEntityStatus() {
        return this.entityStatus;
    }

    public BigDecimal getUserValue() {
        return this.userValue;
    }

    public BigDecimal getSystemValue() {
        return this.systemValue;
    }

}
