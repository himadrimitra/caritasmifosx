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
package org.apache.fineract.organisation.staff.data;

import java.util.Collection;

import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.group.data.CenterData;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;

public class StaffAccountSummaryCollectionData {

    @SuppressWarnings("unused")
    private final Collection<ClientData> clients;
    @SuppressWarnings("unused")
    private final Collection<CenterData> centerDataList;
    @SuppressWarnings("unused")
    private final Collection<GroupGeneralData> groups;

    public StaffAccountSummaryCollectionData(final Collection<ClientData> clients, final Collection<GroupGeneralData> groups,
            final Collection<CenterData> centerDataList) {
        this.clients = clients;
        this.centerDataList = centerDataList;
        this.groups = groups;
    }

}