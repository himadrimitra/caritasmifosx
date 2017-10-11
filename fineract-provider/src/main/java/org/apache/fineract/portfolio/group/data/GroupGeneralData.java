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
package org.apache.fineract.portfolio.group.data;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.village.data.VillageData;
import org.joda.time.LocalDate;

/**
 * Immutable data object representing a general group (so may or may not have a
 * parent).
 */
public class GroupGeneralData {

    private final Long id;
    private final String accountNo;
    private final String name;
    private final String externalId;

    private final EnumOptionData status;
    @SuppressWarnings("unused")
    private final Boolean active;
    private final LocalDate activationDate;

    private final Long officeId;
    private final String officeName;
    private final Long centerId;
    private final String centerName;
    private final Long staffId;
    private final String staffName;
    private final String hierarchy;
    private final String groupLevel;

    // associations
    private Collection<ClientData> clientMembers;
    private Collection<ClientData> activeClientMembers;
    private final Collection<GroupRoleData> groupRoles;
    private final Collection<CalendarData> calendarsData;
    private final CalendarData collectionMeetingCalendar;

    // template
    private final Collection<CenterData> centerOptions;
    private final Collection<OfficeData> officeOptions;
    private final Collection<StaffData> staffOptions;
    private final Collection<ClientData> clientOptions;
    private final Collection<CodeValueData> availableRoles;
    private final GroupRoleData selectedRole;
    private final Collection<CodeValueData> closureReasons;
    private final GroupTimelineData timeline;
    private final VillageData villageData;

    // global configuration
    private final boolean isShowLoanDetailsInCenterPageEnabled;
    private Collection<LoanAccountSummaryData> loanAccountSummaryDatas;

    // Work flow configuration
    private final Boolean isWorkflowEnabled;
    private final Long workflowId;

    public static GroupGeneralData lookup(final Long groupId, final String accountNo, final String groupName, final String groupLevel) {
        final Collection<ClientData> clientMembers = null;
        final Collection<GroupRoleData> groupRoles = null;
        final Collection<CodeValueData> closureReasons = null;
        final boolean isShowLoanDetailsInCenterPageEnabled = false;
        final VillageData villageData = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        return new GroupGeneralData(groupId, accountNo, groupName, null, null, null, null, null, null, null, null, null, null, groupLevel,
                clientMembers, null, null, null, null, null, groupRoles, null, null, null, null, closureReasons, null,
                isShowLoanDetailsInCenterPageEnabled, villageData, isWorkflowEnabled, workflowId);
    }

    public static GroupGeneralData template(final Long officeId, final Long centerId, final String accountNo, final String centerName,
            final Long staffId, final String staffName, final Collection<CenterData> centerOptions,
            final Collection<OfficeData> officeOptions, final Collection<StaffData> staffOptions,
            final Collection<ClientData> clientOptions, final Collection<CodeValueData> availableRoles, final Boolean isWorkflowEnabled) {

        final Collection<ClientData> clientMembers = null;
        final Collection<GroupRoleData> groupRoles = null;
        final Collection<CodeValueData> closureReasons = null;
        final boolean isShowLoanDetailsInCenterPageEnabled = false;
        final VillageData villageData = null;
        final Long workflowId = null;

        return new GroupGeneralData(null, accountNo, null, null, null, null, officeId, null, centerId, centerName, staffId, staffName, null,
                null, clientMembers, null, centerOptions, officeOptions, staffOptions, clientOptions, groupRoles, availableRoles, null,
                null, null, closureReasons, null, isShowLoanDetailsInCenterPageEnabled, villageData, isWorkflowEnabled, workflowId);
    }

    public static GroupGeneralData withTemplate(final GroupGeneralData templatedGrouping, final GroupGeneralData grouping) {
        return new GroupGeneralData(grouping.id, grouping.accountNo, grouping.name, grouping.externalId, grouping.status,
                grouping.activationDate, grouping.officeId, grouping.officeName, grouping.centerId, grouping.centerName, grouping.staffId,
                grouping.staffName, grouping.hierarchy, grouping.groupLevel, grouping.clientMembers, grouping.activeClientMembers,
                templatedGrouping.centerOptions, templatedGrouping.officeOptions, templatedGrouping.staffOptions,
                templatedGrouping.clientOptions, grouping.groupRoles, templatedGrouping.availableRoles, grouping.selectedRole,
                grouping.calendarsData, grouping.collectionMeetingCalendar, grouping.closureReasons, templatedGrouping.timeline,
                grouping.isShowLoanDetailsInCenterPageEnabled, grouping.villageData, templatedGrouping.isWorkflowEnabled,
                grouping.workflowId);
    }

    public static GroupGeneralData withAssocations(final GroupGeneralData grouping, final Collection<ClientData> membersOfGroup,
            final Collection<ClientData> activeClientMembers, final Collection<GroupRoleData> groupRoles,
            final Collection<CalendarData> calendarsData, final CalendarData collectionMeetingCalendar) {
        return new GroupGeneralData(grouping.id, grouping.accountNo, grouping.name, grouping.externalId, grouping.status,
                grouping.activationDate, grouping.officeId, grouping.officeName, grouping.centerId, grouping.centerName, grouping.staffId,
                grouping.staffName, grouping.hierarchy, grouping.groupLevel, membersOfGroup, activeClientMembers, grouping.centerOptions,
                grouping.officeOptions, grouping.staffOptions, grouping.clientOptions, groupRoles, grouping.availableRoles,
                grouping.selectedRole, calendarsData, collectionMeetingCalendar, grouping.closureReasons, grouping.timeline,
                grouping.isShowLoanDetailsInCenterPageEnabled, grouping.villageData, grouping.isWorkflowEnabled, grouping.workflowId);
    }

    public static GroupGeneralData instance(final Long id, final String accountNo, final String name, final String externalId,
            final EnumOptionData status, final LocalDate activationDate, final Long officeId, final String officeName, final Long centerId,
            final String centerName, final Long staffId, final String staffName, final String hierarchy, final String groupLevel,
            final GroupTimelineData timeline, final Boolean isWorkflowEnabled, final Long workflowId) {

        final Collection<ClientData> clientMembers = null;
        final Collection<ClientData> activeClientMembers = null;
        final Collection<CenterData> centerOptions = null;
        final Collection<OfficeData> officeOptions = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<ClientData> clientOptions = null;
        final Collection<GroupRoleData> groupRoles = null;
        final Collection<CodeValueData> availableRoles = null;
        final GroupRoleData role = null;
        final Collection<CalendarData> calendarsData = null;
        final CalendarData collectionMeetingCalendar = null;
        final Collection<CodeValueData> closureReasons = null;
        final boolean isShowLoanDetailsInCenterPageEnabled = false;
        final VillageData villageData = null;

        return new GroupGeneralData(id, accountNo, name, externalId, status, activationDate, officeId, officeName, centerId, centerName,
                staffId, staffName, hierarchy, groupLevel, clientMembers, activeClientMembers, centerOptions, officeOptions, staffOptions,
                clientOptions, groupRoles, availableRoles, role, calendarsData, collectionMeetingCalendar, closureReasons, timeline,
                isShowLoanDetailsInCenterPageEnabled, villageData, isWorkflowEnabled, workflowId);
    }

    public static GroupGeneralData formGroupData(final Long id, final String name) {
        final Collection<ClientData> activeClientMembers = null;
        final Collection<CenterData> centerOptions = null;
        final Collection<OfficeData> officeOptions = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<ClientData> clientOptions = null;
        final Collection<GroupRoleData> groupRoles = null;
        final Collection<CodeValueData> availableRoles = null;
        final GroupRoleData role = null;
        final Collection<CalendarData> calendarsData = null;
        final CalendarData collectionMeetingCalendar = null;
        final Collection<CodeValueData> closureReasons = null;
        final boolean isShowLoanDetailsInCenterPageEnabled = false;
        final String accountNo = null;
        final String externalId = null;
        final EnumOptionData status = null;
        final LocalDate activationDate = null;
        final Long officeId = null;
        final String officeName = null;
        final Long centerId = null;
        final String centerName = null;
        final Long staffId = null;
        final String staffName = null;
        final String hierarchy = null;
        final String groupLevel = null;
        final GroupTimelineData timeline = null;
        final Collection<ClientData> clientMembers = new ArrayList<>();
        final VillageData villageData = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;

        return new GroupGeneralData(id, accountNo, name, externalId, status, activationDate, officeId, officeName, centerId, centerName,
                staffId, staffName, hierarchy, groupLevel, clientMembers, activeClientMembers, centerOptions, officeOptions, staffOptions,
                clientOptions, groupRoles, availableRoles, role, calendarsData, collectionMeetingCalendar, closureReasons, timeline,
                isShowLoanDetailsInCenterPageEnabled, villageData, isWorkflowEnabled, workflowId);
    }

    private GroupGeneralData(final Long id, final String accountNo, final String name, final String externalId, final EnumOptionData status,
            final LocalDate activationDate, final Long officeId, final String officeName, final Long centerId, final String centerName,
            final Long staffId, final String staffName, final String hierarchy, final String groupLevel,
            final Collection<ClientData> clientMembers, final Collection<ClientData> activeClientMembers,
            final Collection<CenterData> centerOptions, final Collection<OfficeData> officeOptions,
            final Collection<StaffData> staffOptions, final Collection<ClientData> clientOptions,
            final Collection<GroupRoleData> groupRoles, final Collection<CodeValueData> availableRoles, final GroupRoleData role,
            final Collection<CalendarData> calendarsData, final CalendarData collectionMeetingCalendar,
            final Collection<CodeValueData> closureReasons, final GroupTimelineData timeline,
            final boolean isShowLoanDetailsInCenterPageEnabled, final VillageData villageData, final Boolean isWorkflowEnabled,
            final Long workflowId) {
        this.id = id;
        this.accountNo = accountNo;
        this.name = name;
        this.externalId = externalId;
        this.status = status;
        if (status != null) {
            this.active = status.getId().equals(300l);
        } else {
            this.active = null;
        }
        this.activationDate = activationDate;

        this.officeId = officeId;
        this.officeName = officeName;
        this.centerId = centerId;
        this.centerName = centerName;
        this.staffId = staffId;
        this.staffName = staffName;
        this.hierarchy = hierarchy;
        this.groupLevel = groupLevel;

        // associations
        this.clientMembers = clientMembers;
        this.activeClientMembers = activeClientMembers;

        // template
        this.centerOptions = centerOptions;
        this.officeOptions = officeOptions;
        this.staffOptions = staffOptions;

        if (clientMembers != null && clientOptions != null) {
            clientOptions.removeAll(clientMembers);
        }
        this.clientOptions = clientOptions;
        this.groupRoles = groupRoles;
        this.availableRoles = availableRoles;
        this.selectedRole = role;
        this.calendarsData = calendarsData;
        this.collectionMeetingCalendar = collectionMeetingCalendar;
        this.closureReasons = closureReasons;
        this.timeline = timeline;
        this.isShowLoanDetailsInCenterPageEnabled = isShowLoanDetailsInCenterPageEnabled;
        this.villageData = villageData;
        this.isWorkflowEnabled = isWorkflowEnabled;
        this.workflowId = workflowId;
    }

    public Long getId() {
        return this.id;
    }

    public String getAccountNo() {
        return this.accountNo;
    }

    public String getName() {
        return this.name;
    }

    public Long officeId() {
        return this.officeId;
    }

    public String getHierarchy() {
        return this.hierarchy;
    }

    public boolean isChildGroup() {
        return this.centerId == null ? false : true;
    }

    public Long getParentId() {
        return this.centerId;
    }

    public static GroupGeneralData updateSelectedRole(final GroupGeneralData grouping, final GroupRoleData selectedRole) {
        return new GroupGeneralData(grouping.id, grouping.accountNo, grouping.name, grouping.externalId, grouping.status,
                grouping.activationDate, grouping.officeId, grouping.officeName, grouping.centerId, grouping.centerName, grouping.staffId,
                grouping.staffName, grouping.hierarchy, grouping.groupLevel, grouping.clientMembers, grouping.activeClientMembers,
                grouping.centerOptions, grouping.officeOptions, grouping.staffOptions, grouping.clientOptions, grouping.groupRoles,
                grouping.availableRoles, selectedRole, grouping.calendarsData, grouping.collectionMeetingCalendar, grouping.closureReasons,
                null, grouping.isShowLoanDetailsInCenterPageEnabled, grouping.villageData, grouping.isWorkflowEnabled, grouping.workflowId);
    }

    public static GroupGeneralData withClosureReasons(final Collection<CodeValueData> closureReasons) {
        final Long id = null;
        final String accountNo = null;
        final String name = null;
        final String externalId = null;
        final EnumOptionData status = null;
        final LocalDate activationDate = null;
        final Long officeId = null;
        final String officeName = null;
        final Long centerId = null;
        final String centerName = null;
        final Long staffId = null;
        final String staffName = null;
        final String hierarchy = null;
        final String groupLevel = null;
        final Collection<ClientData> clientMembers = null;
        final Collection<ClientData> activeClientMembers = null;
        final Collection<CenterData> centerOptions = null;
        final Collection<OfficeData> officeOptions = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<ClientData> clientOptions = null;
        final Collection<GroupRoleData> groupRoles = null;
        final Collection<CodeValueData> availableRoles = null;
        final GroupRoleData role = null;
        final Collection<CalendarData> calendarsData = null;
        final CalendarData collectionMeetingCalendar = null;
        final boolean isShowLoanDetailsInCenterPageEnabled = false;
        final VillageData villageData = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;

        return new GroupGeneralData(id, accountNo, name, externalId, status, activationDate, officeId, officeName, centerId, centerName,
                staffId, staffName, hierarchy, groupLevel, clientMembers, activeClientMembers, centerOptions, officeOptions, staffOptions,
                clientOptions, groupRoles, availableRoles, role, calendarsData, collectionMeetingCalendar, closureReasons, null,
                isShowLoanDetailsInCenterPageEnabled, villageData, isWorkflowEnabled, workflowId);
    }

    public Collection<ClientData> clientMembers() {
        return this.clientMembers;
    }

    public void update(final Collection<ClientData> activeClientMembers1) {
        this.activeClientMembers = activeClientMembers1;
    }

    public void addActiveClientMember(final ClientData clientData) {

        if (this.activeClientMembers == null) {
            this.activeClientMembers = new ArrayList<>();
        }
        this.activeClientMembers.add(clientData);
    }

    public static GroupGeneralData lookup(final Long groupId, final String accountNo, final String groupName, final EnumOptionData status,
            final String externalId) {
        final Collection<ClientData> clientMembers = null;
        final Collection<GroupRoleData> groupRoles = null;
        final Collection<CodeValueData> closureReasons = null;
        final boolean isShowLoanDetailsInCenterPageEnabled = false;
        final VillageData villageData = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        return new GroupGeneralData(groupId, accountNo, groupName, externalId, status, null, null, null, null, null, null, null, null, null,
                clientMembers, null, null, null, null, null, groupRoles, null, null, null, null, closureReasons, null,
                isShowLoanDetailsInCenterPageEnabled, villageData, isWorkflowEnabled, workflowId);
    }

    public static GroupGeneralData withConfig(final GroupGeneralData generalData, final boolean isShowLoanDetailsInCenterPageEnabled) {
        final Collection<ClientData> clientMembers = null;
        final Collection<GroupRoleData> groupRoles = null;
        final Collection<CodeValueData> closureReasons = null;
        final VillageData villageData = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        return new GroupGeneralData(generalData.id, generalData.accountNo, generalData.name, generalData.externalId, generalData.status,
                null, null, null, null, null, null, null, null, null, clientMembers, null, null, null, null, null, groupRoles, null, null,
                null, null, closureReasons, null, isShowLoanDetailsInCenterPageEnabled, villageData, isWorkflowEnabled, workflowId);
    }

    public void updateClientMembers(final Collection<ClientData> clientMembers) {
        this.clientMembers = clientMembers;
    }

    public void addClients(final ClientData clientData) {
        if (this.clientMembers == null) {
            this.clientMembers = new ArrayList<>();
        }
        this.clientMembers.add(clientData);
    }

    public void addLoanAccountSummaryData(final LoanAccountSummaryData loanAccountSummaryData) {
        if (this.loanAccountSummaryDatas == null) {
            this.loanAccountSummaryDatas = new ArrayList<>();
        }
        this.loanAccountSummaryDatas.add(loanAccountSummaryData);
    }

    public static GroupGeneralData withVillageData(final GroupGeneralData group, final VillageData villageData) {
        return new GroupGeneralData(group.id, group.accountNo, group.name, group.externalId, group.status, group.activationDate,
                group.officeId, group.officeName, group.centerId, group.centerName, group.staffId, group.staffName, group.hierarchy,
                group.groupLevel, group.clientMembers, group.activeClientMembers, group.centerOptions, group.officeOptions,
                group.staffOptions, group.clientOptions, group.groupRoles, group.availableRoles, group.selectedRole, group.calendarsData,
                group.collectionMeetingCalendar, group.closureReasons, group.timeline, group.isShowLoanDetailsInCenterPageEnabled,
                villageData, group.isWorkflowEnabled, group.workflowId);
    }

    public static GroupGeneralData lookupforhierarchy(final Long centerId, final String centerName, final VillageData villageData,
            final GroupGeneralData group, final Long officeId, final String officeName) {
        return new GroupGeneralData(group.id, group.accountNo, group.name, group.externalId, group.status, group.activationDate, officeId,
                officeName, centerId, centerName, group.staffId, group.staffName, group.hierarchy, group.groupLevel, group.clientMembers,
                group.activeClientMembers, group.centerOptions, group.officeOptions, group.staffOptions, group.clientOptions,
                group.groupRoles, group.availableRoles, group.selectedRole, group.calendarsData, group.collectionMeetingCalendar,
                group.closureReasons, group.timeline, group.isShowLoanDetailsInCenterPageEnabled, villageData, group.isWorkflowEnabled,
                group.workflowId);
    }

    public static GroupGeneralData lookupforhierarchy(final Long id, final String name, final Long centerId, final String centerName,
            final VillageData villageData, final Long officeId, final String officeName) {
        final Collection<ClientData> clientMembers = null;
        final Collection<ClientData> activeClientMembers = null;
        final Collection<CenterData> centerOptions = null;
        final Collection<OfficeData> officeOptions = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<ClientData> clientOptions = null;
        final Collection<GroupRoleData> groupRoles = null;
        final Collection<CodeValueData> availableRoles = null;
        final Collection<CalendarData> calendarsData = null;
        final CalendarData collectionMeetingCalendar = null;
        final Collection<CodeValueData> closureReasons = null;
        final boolean isShowLoanDetailsInCenterPageEnabled = false;
        final String accountNo = null;
        final String externalId = null;
        final EnumOptionData status = null;
        final LocalDate activationDate = null;
        final Long staffId = null;
        final String staffName = null;
        final String hierarchy = null;
        final String groupLevel = null;
        final GroupRoleData selectedRole = null;
        final GroupTimelineData timeline = null;
        final Boolean isWorkflowEnabled = null;
        final Long workflowId = null;
        return new GroupGeneralData(id, accountNo, name, externalId, status, activationDate, officeId, officeName, centerId, centerName,
                staffId, staffName, hierarchy, groupLevel, clientMembers, activeClientMembers, centerOptions, officeOptions, staffOptions,
                clientOptions, groupRoles, availableRoles, selectedRole, calendarsData, collectionMeetingCalendar, closureReasons, timeline,
                isShowLoanDetailsInCenterPageEnabled, villageData, isWorkflowEnabled, workflowId);
    }
}