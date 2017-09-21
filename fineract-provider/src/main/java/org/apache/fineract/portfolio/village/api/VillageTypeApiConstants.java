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
package org.apache.fineract.portfolio.village.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class VillageTypeApiConstants {
    
    public static String VILLAGE_RESOURCE_NAME = "village";

    //Supported command parameters
    public final static String ACTIVATE_COMMAND = "activate" ;
    public final static String INITIATE_WORKFLOW_COMMAND = "initiateWorkflow" ;
    public final static String ASSIGN_STAFF_COMMAND = "assignstaff" ;
    public final static String UNASSIGN_STAFF_COMMAND = "unassignstaff" ;
    public static final String staffIdParamName = "staffId";
    public final static String REJECT_COMMAND = "reject" ;
    
    public static final String idParamName = "id";
    public static final String externalIdParamName = "externalId";
    public static final String officeIdParamName = "officeId";
    public static final String officeNameParamName = "officeName";
    public static final String officeOptionsParamName = "officeOptions";
    public static final String villageCodeParamName = "villageCode";
    public static final String villageNameParamName = "villageName";
    public static final String statusParamName = "status";
    public static final String talukParamName = "taluk";
    public static final String districtParamName = "district";
    public static final String pincodeParamName = "pincode";
    public static final String stateParamName = "state";
    public static final String countParamName = "count";
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    public static final String submittedOnDateParamName = "submittedOnDate";
    public static final String activationDateParamName = "activatedOnDate";
    public static final String activeParamName = "active";
    public static final String centerIdParamName = "centerId";
    public static final String centersOfVillageParamName = "setOfCenters";
    public static final String addressesParamName = "addresses";
    public static final String pathParamName = "villages";
    
    public static final String timelineParamName = "timeline";
       
    public static final Set<String> VILLAGE_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, externalIdParamName, officeIdParamName, 
            officeNameParamName, villageCodeParamName, villageNameParamName, statusParamName, countParamName, talukParamName, districtParamName, 
            pincodeParamName, stateParamName, localeParamName,dateFormatParamName, submittedOnDateParamName, activationDateParamName, 
             officeOptionsParamName, centersOfVillageParamName, centersOfVillageParamName,addressesParamName));

    public static final Set<String> VILLAGE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(externalIdParamName, officeIdParamName, 
            villageCodeParamName, villageNameParamName, countParamName, talukParamName, districtParamName, pincodeParamName, stateParamName,
            activeParamName, localeParamName,dateFormatParamName, activationDateParamName , submittedOnDateParamName, timelineParamName,addressesParamName));
    
    public static final Set<String> ACTIVATION_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, activationDateParamName));

    public static final String villagesParamName = "villages";
    public static final Set<String> BULK_REJECT_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName, villagesParamName));
}
