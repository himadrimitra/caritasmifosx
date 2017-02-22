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
package org.apache.fineract.infrastructure.dataqueries.data;

import java.util.List;

/**
 * Immutable data object representing datatable data.
 */
public class DatatableData {

    private final Integer id;
    @SuppressWarnings("unused")
    private final String applicationTableName;
    @SuppressWarnings("unused")
    private final String registeredTableName;
    private final List<ResultsetColumnHeaderData> columnHeaderData;
    private final Long scopingCriteriaEnum;
    private final List<ScopeCriteriaData> scopeCriteriaData;
    private final String registeredTableDisplayName;


    public static DatatableData create(final Integer id, final String applicationTableName, final String registeredTableName,
            final List<ResultsetColumnHeaderData> columnHeaderData, Long scopingCriteriaEnum, List<ScopeCriteriaData> scopeCriteriaData, String registeredTableDisplayName) {
        return new DatatableData(id, applicationTableName, registeredTableName, columnHeaderData, scopingCriteriaEnum, scopeCriteriaData, registeredTableDisplayName);
    }

    private DatatableData(final Integer id, final String applicationTableName, final String registeredTableName,
            final List<ResultsetColumnHeaderData> columnHeaderData, final Long scopingCriteriaEnum,
            List<ScopeCriteriaData> scopeCriteriaData, final String registeredTableDisplayName) {
        this.id = id;
        this.applicationTableName = applicationTableName;
        this.registeredTableName = registeredTableName;
        this.columnHeaderData = columnHeaderData;
        this.scopingCriteriaEnum = scopingCriteriaEnum;
        this.scopeCriteriaData = scopeCriteriaData;
        this.registeredTableDisplayName = registeredTableDisplayName;
    }
    
    public Integer getId() {
        return this.id;
    }
}