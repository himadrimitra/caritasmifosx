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
package org.apache.fineract.infrastructure.dataqueries.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Cieyou on 2/26/14.
 */
public class DataTableApiConstant {

    public static final Integer CATEGORY_PPI = 200;
    public static final Integer CATEGORY_DEFAULT = 100;

    public static final String categoryParamName ="category";
    public static final String localParamName = "locale";
    public static final Set<String> REGISTER_PARAMS = new HashSet<>(Arrays.asList(categoryParamName,localParamName));

    public static final String DATATABLE_RESOURCE_NAME ="dataTables";
    public static final String displayNameParamName = "displayName";
    public static final String displayPositionParamName = "displayPosition";
    public static final String visibleParamName = "visible";
    public static final String dependsOnWithParamName = "dependsOn";
    public static final String visibilityCriteriaParamName = "visibilityCriteria";
    public static final String columnNameParamName = "columnName";
    public static final String valueParamName = "value";
    public static final String mandatoryIfVisibleParamName = "mandatoryIfVisible";
    public static final String columnValuesParamName = "columnValues";
    public static final String scopeParamName = "scope";
    public static final String idParamName = "id";
    public static final String allowedValuesParamName = "allowedValues";
    public static final String scopingCriteriaEnumParamName = "scopingCriteriaEnum";
    public static final String dataTableDisplayNameParam = "dataTableDisplayName";
    public static final String JOURNAL_ENTRY_TABLE_NAME = "f_journal_entry";
    public static final String LOAN_APPLICATION_REFERENCE = "f_loan_application_reference";
    public static final String sectionsParamName = "sections";
    public static final String columnsParamName = "columns";
    public static final String changeColumnsParamName = "changeColumns";
    public static final String addColumnsParamName = "addColumns";
    public static final String sectionIdParamName = "sectionId";
    public static final String dropColumnsParamName = "dropColumns";
    public static final String addSectionsParamName = "addSections";
    public static final String dropSectionsParamName = "dropSections";
    public static final String changeSectionsParamName = "changeSections";
    public static final String VILLAGE = "chai_villages";
    public static final String DISTRICT = "f_district";
}