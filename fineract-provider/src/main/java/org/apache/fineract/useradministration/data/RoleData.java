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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.organisation.monetary.data.CurrencyData;

/**
 * Immutable data object for role data.
 */
public class RoleData implements Serializable {

    private final Long id;
    private final String name;
    private final String description;
    private final Boolean disabled;
    private List<RoleBasedLimitData> roleBasedLimits;
    private List<CurrencyData> currencyOptions;

    public RolePermissionsData toRolePermissionData(final Collection<PermissionData> permissionUsageData) {
        return new RolePermissionsData(this.id, this.name, this.description, this.disabled, permissionUsageData);
    }

    public RoleData(final Long id, final String name, final String description, final Boolean disabled,
            final List<RoleBasedLimitData> roleBasedLimits) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.disabled = disabled;
        this.roleBasedLimits = roleBasedLimits;
    }

    @Override
    public boolean equals(final Object obj) {
        final RoleData role = (RoleData) obj;
        return this.id.equals(role.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Boolean getDisabled() {
        return this.disabled;
    }

    public List<RoleBasedLimitData> getRoleBasedLimits() {
        return this.roleBasedLimits;
    }

    public void setRoleBasedLimits(final List<RoleBasedLimitData> roleBasedLimits) {
        this.roleBasedLimits = roleBasedLimits;
    }

    public void setCurrencyOptions(final List<CurrencyData> currencyOptions) {
        this.currencyOptions = currencyOptions;
    }

    public List<CurrencyData> getCurrencyOptions() {
        return this.currencyOptions;
    }

}