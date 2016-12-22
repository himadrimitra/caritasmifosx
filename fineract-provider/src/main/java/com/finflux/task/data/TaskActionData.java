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
package com.finflux.task.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.useradministration.data.RoleData;

import java.util.List;

/**
 * <p>
 * Immutable data object representing generic enumeration value.
 * </p>
 */
public class TaskActionData extends EnumOptionData{

    private final Boolean hasAccess;
    private final List<RoleData> accessRoles;

    public TaskActionData(TaskActionType action, Boolean hasAccess, List<RoleData> accessRoles) {
        super(action.getValue().longValue(), action.getCode(), action.toString());
        this.hasAccess = hasAccess;
        this.accessRoles = accessRoles;
    }
}