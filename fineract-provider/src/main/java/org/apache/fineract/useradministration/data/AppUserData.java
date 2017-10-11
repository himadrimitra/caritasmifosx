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
package org.apache.fineract.useradministration.data;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.client.data.ClientData;

/**
 * Immutable data object for application user data.
 */
public class AppUserData {

    private final Long id;
    private final String username;
    private final Long officeId;
    private final String officeName;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final Boolean passwordNeverExpires;
    private final Boolean accountNonLocked;

    @SuppressWarnings("unused")
    private final Collection<OfficeData> allowedOffices;
    private final Collection<RoleData> availableRoles;
    private final Collection<RoleData> selectedRoles;
    private final StaffData staff;
    private final Boolean isSelfServiceUser;

    @SuppressWarnings("unused")
    private Set<ClientData> clients;

    private final Date lastLoginDate;
    private final Boolean systemUser;

    public static AppUserData template(final AppUserData user, final Collection<OfficeData> officesForDropdown) {
        return new AppUserData(user.id, user.username, user.email, user.officeId, user.officeName, user.firstname, user.lastname,
                user.availableRoles, user.selectedRoles, officesForDropdown, user.staff, user.passwordNeverExpires, user.isSelfServiceUser,
                user.lastLoginDate, user.accountNonLocked, user.systemUser);
    }

    public static AppUserData template(final Collection<OfficeData> offices, final Collection<RoleData> availableRoles) {
        final Date lastLoginDate = null;
        final Boolean accountNonLocked = null;
        final Boolean systemUser = null;
        return new AppUserData(null, null, null, null, null, null, null, availableRoles, null, offices, null, null, null, lastLoginDate,
                accountNonLocked, systemUser);
    }

    public static AppUserData dropdown(final Long id, final String username) {
        final Date lastLoginDate = null;
        final Boolean accountNonLocked = null;
        final Boolean systemUser = null;
        return new AppUserData(id, username, null, null, null, null, null, null, null, null, null, null, null, lastLoginDate,
                accountNonLocked, systemUser);
    }

    public static AppUserData auditdetails(final Long id, final String username, final String firstname, final String lastname) {
        final Date lastLoginDate = null;
        final Boolean accountNonLocked = null;
        final Boolean systemUser = null;
        return new AppUserData(id, username, null, null, null, firstname, lastname, null, null, null, null, null, null, lastLoginDate,
                accountNonLocked, systemUser);
    }

    public static AppUserData instance(final Long id, final String username, final String email, final Long officeId,
            final String officeName, final String firstname, final String lastname, final Collection<RoleData> availableRoles,
            final Collection<RoleData> selectedRoles, final StaffData staff, final Boolean passwordNeverExpire,
            final Boolean isSelfServiceUser, final Date lastLoginDate, final Boolean accountNonLocked, final Boolean systemUser) {
        return new AppUserData(id, username, email, officeId, officeName, firstname, lastname, availableRoles, selectedRoles, null, staff,
                passwordNeverExpire, isSelfServiceUser, lastLoginDate, accountNonLocked, systemUser);
    }

    private AppUserData(final Long id, final String username, final String email, final Long officeId, final String officeName,
            final String firstname, final String lastname, final Collection<RoleData> availableRoles,
            final Collection<RoleData> selectedRoles, final Collection<OfficeData> allowedOffices, final StaffData staff,
            final Boolean passwordNeverExpire, final Boolean isSelfServiceUser, final Date lastLoginDate, final Boolean accountNonLocked,
            final Boolean systemUser) {
        this.id = id;
        this.username = username;
        this.officeId = officeId;
        this.officeName = officeName;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.allowedOffices = allowedOffices;
        this.availableRoles = availableRoles;
        this.selectedRoles = selectedRoles;
        this.staff = staff;
        this.passwordNeverExpires = passwordNeverExpire;
        this.isSelfServiceUser = isSelfServiceUser;
        this.lastLoginDate = lastLoginDate;
        this.accountNonLocked = accountNonLocked;
        this.systemUser = systemUser;
    }

    public boolean hasIdentifyOf(final Long createdById) {
        return this.id.equals(createdById);
    }

    public String username() {
        return this.username;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        final AppUserData that = (AppUserData) o;

        if (this.id != null ? !this.id.equals(that.id) : that.id != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    public void setClients(final Set<ClientData> clients) {
        this.clients = clients;
    }

    public boolean isSelfServiceUser() {
        return this.isSelfServiceUser == null ? false : this.isSelfServiceUser;
    }

}