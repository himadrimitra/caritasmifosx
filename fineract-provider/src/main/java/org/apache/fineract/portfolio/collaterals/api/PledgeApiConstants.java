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
package org.apache.fineract.portfolio.collaterals.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;


public class PledgeApiConstants {
    
    public static final String COLLATERAL_PLEDGE_RESOURCE_NAME = "pledge";
    public static final String COLLATERAL_DETAILS_RESOURCE_NAME = "CollateralDetails";
    public static final String clientIdParamName = "clientId";
    public static final String loanIdParamName = "loanId";
    public static final String idParamName = "id";
    public static final String pledgeNumberParamName = "pledgeNumber";
    public static final String sealNumberParamName = "sealNumber";
    public static final String statusParamName = "status";
    public static final String systemValueParamName = "systemValue";
    public static final String userValueParamName = "userValue";
    public static final String localeParamName = "locale";
    public static final String collateralDetailsParamName = "collateralDetails";
    public static final String collateralIdParamName = "collateralId";
    public static final String qualityStandardIdParamName = "qualityStandardId";
    public static final String descriptionParamName = "description";
    public static final String grossWeightParamName = "grossWeight";
    public static final String netWeightParamName = "netWeight";
    public static final String systemPriceParamName = "systemPrice";
    public static final String userPriceParamName = "userPrice";
    public static final String dateFormatParamName = "dateFormat";
    public static final String closureDateParamName = "closureDate";
    public static final String createdByParamName = "createdBy";
    public static final String createdDateParamName = "createdDate";
    public static final String updatedByParamName = "updatedBy";
    public static final String updatedDateParamName = "updatedDate";
    
    public static final Set<String> PLEDGE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(clientIdParamName, loanIdParamName, descriptionParamName, 
            sealNumberParamName, statusParamName, systemValueParamName, userValueParamName, collateralDetailsParamName, collateralIdParamName, qualityStandardIdParamName ,
            descriptionParamName, grossWeightParamName, netWeightParamName, systemPriceParamName, userPriceParamName, localeParamName));
    
    public static final Set<String> PLEDGE_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(clientIdParamName, loanIdParamName, sealNumberParamName, 
            systemValueParamName, statusParamName, userValueParamName, collateralDetailsParamName, localeParamName));
    
    public static final Set<String> COLLATERAL_DETAILS_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(collateralIdParamName, qualityStandardIdParamName ,
            descriptionParamName, grossWeightParamName, netWeightParamName, systemPriceParamName, userPriceParamName, localeParamName));
    
    public static final Set<String> PLEDGE_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(clientIdParamName, loanIdParamName, sealNumberParamName, statusParamName, 
            systemValueParamName, userValueParamName, localeParamName, createdByParamName, createdDateParamName, updatedByParamName, updatedDateParamName));
    
    public static final Set<String> PLEDGE_CLOSE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, dateFormatParamName, localeParamName,
            closureDateParamName));
    
    public static final Set<String> COLLATERAL_DETAILS_UPDAE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, dateFormatParamName, localeParamName,
            closureDateParamName));
    
    public static final String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    public static enum PLEDGE_STATUS_PARAMS {
        INVALID(0, "pledgeStatusType.invalid"), 
        INITIATE_PLEDGE(1, "pledgeStatusType.initiated"), 
        ACTIVE_PLEDGE(2, "pledgeStatusType.active"), 
        CLOSE_PLEDGE(3, "pledgeStatusType.closed");
       
        private final Integer value;
        private final String code;

        private PLEDGE_STATUS_PARAMS(final Integer value, final String code) {
            this.value = value;
            this.code = code;
        }
        
        public Integer getValue() {
            return this.value;
        }
        
        public String getCode() {
            return this.code;
        }

        public static EnumOptionData status(final Integer status) {
            EnumOptionData optionData = new EnumOptionData(PLEDGE_STATUS_PARAMS.INVALID.getValue().longValue(), PLEDGE_STATUS_PARAMS.INVALID.getCode(),
                    "Invalid");
            switch (status) {
                case 0:
                    optionData = new EnumOptionData(PLEDGE_STATUS_PARAMS.INVALID.getValue().longValue(), PLEDGE_STATUS_PARAMS.INVALID.getCode(),
                            "Invalid");
                break;
                case 1:
                    optionData = new EnumOptionData(PLEDGE_STATUS_PARAMS.INITIATE_PLEDGE.getValue().longValue(), PLEDGE_STATUS_PARAMS.INITIATE_PLEDGE.getCode(),
                            "Initiated");
                break;
                case 2:
                    optionData = new EnumOptionData(PLEDGE_STATUS_PARAMS.ACTIVE_PLEDGE.getValue().longValue(), PLEDGE_STATUS_PARAMS.ACTIVE_PLEDGE.getCode(),
                            "Active");
                break;
                case 3:
                    optionData = new EnumOptionData(PLEDGE_STATUS_PARAMS.CLOSE_PLEDGE.getValue().longValue(), PLEDGE_STATUS_PARAMS.CLOSE_PLEDGE.getCode(),
                            "Close");
                break;
                default:
                break;
            }

            return optionData;
        }
        
    }
    
    
    

}
