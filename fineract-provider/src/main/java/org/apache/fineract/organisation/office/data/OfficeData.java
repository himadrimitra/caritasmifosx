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
package org.apache.fineract.organisation.office.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.joda.time.LocalDate;

import com.finflux.task.data.WorkFlowSummaryData;

/**
 * Immutable data object for office data.
 */
public class OfficeData implements Serializable {

    private final Long id;
    private final String name;
    private final String nameDecorated;
    private final String externalId;
    private final LocalDate openingDate;
    private final String hierarchy;
    private final Long parentId;
    private final String parentName;
    @SuppressWarnings("unused")
    private final Collection<OfficeData> allowedParents;
    private Collection<WorkFlowSummaryData> workFlowSummaries;
    private final String officeCodeId;
    @SuppressWarnings("unused")
    private final Boolean isWorkflowEnabled;
    private final Long workflowId;
    private final EnumOptionData status;
    private final LocalDate activationDate;
    private final LocalDate rejectedonDate;

    public static OfficeData dropdown(final Long id, final String name, final String nameDecorated) {
        final String officeCodeId = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        final EnumOptionData status = null;
        final LocalDate activationDate = null;
        final LocalDate rejectedonDate = null;
        return new OfficeData(id, name, nameDecorated, null, null, null, null, null, null, officeCodeId, isWorkflowEnabled, workflowId,
                status, activationDate, rejectedonDate);
    }

    public static OfficeData journalEntry(final Long id, final String name, final String externalId) {
        final String officeCodeId = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        final EnumOptionData status = null;
        final LocalDate activationDate = null;
        final LocalDate rejectedonDate = null;
        return new OfficeData(id, name, null, externalId, null, null, null, null, null, officeCodeId, isWorkflowEnabled, workflowId, status,
                activationDate, rejectedonDate);
    }

    public static OfficeData template(final List<OfficeData> parentLookups, final LocalDate defaultOpeningDate) {
        final String officeCodeId = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        final EnumOptionData status = null;
        final LocalDate activationDate = null;
        final LocalDate rejectedonDate = null;

        return new OfficeData(null, null, null, null, defaultOpeningDate, null, null, null, parentLookups, officeCodeId, isWorkflowEnabled,
                workflowId, status, activationDate, rejectedonDate);
    }

    public static OfficeData appendedTemplate(final OfficeData office, final Collection<OfficeData> allowedParents,
            final Boolean isWorkflowEnabled) {
        return new OfficeData(office.id, office.name, office.nameDecorated, office.externalId, office.openingDate, office.hierarchy,
                office.parentId, office.parentName, allowedParents, office.officeCodeId, isWorkflowEnabled, office.workflowId,
                office.status, office.activationDate, office.rejectedonDate);
    }

    public OfficeData(final Long id, final String name, final String nameDecorated, final String externalId, final LocalDate openingDate,
            final String hierarchy, final Long parentId, final String parentName, final Collection<OfficeData> allowedParents,
            final String officeCodeId, final Boolean isWorkflowEnabled, final Long workflowId, final EnumOptionData status,
            final LocalDate activationDate, final LocalDate rejectedonDate) {
        this.id = id;
        this.name = name;
        this.nameDecorated = nameDecorated;
        this.externalId = externalId;
        this.openingDate = openingDate;
        this.hierarchy = hierarchy;
        this.parentName = parentName;
        this.parentId = parentId;
        this.allowedParents = allowedParents;
        this.officeCodeId = officeCodeId;
        this.isWorkflowEnabled = isWorkflowEnabled;
        this.workflowId = workflowId;
        this.status = status;
        this.activationDate = activationDate;
        this.rejectedonDate = rejectedonDate;
    }

    public static OfficeData lookup(final Long id, final String name) {
        final String officeCodeId = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        final EnumOptionData status = null;
        final LocalDate activationDate = null;
        final LocalDate rejectedonDate = null;
        return new OfficeData(id, name, null, null, null, null, null, null, null, officeCodeId, isWorkflowEnabled, workflowId, status,
                activationDate, rejectedonDate);
    }

    public boolean hasIdentifyOf(final Long officeId) {
        return this.id.equals(officeId);
    }

    public String name() {
        return this.name;
    }

    public String getHierarchy() {
        return this.hierarchy;
    }

    public Collection<WorkFlowSummaryData> getWorkFlowSummaries() {
        return this.workFlowSummaries;
    }

    public void setWorkFlowSummaries(final Collection<WorkFlowSummaryData> workFlowSummaries) {
        this.workFlowSummaries = workFlowSummaries;
    }

    public Long getId() {
        return this.id;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public String getOfficeCodeId() {
        return this.officeCodeId;
    }
}