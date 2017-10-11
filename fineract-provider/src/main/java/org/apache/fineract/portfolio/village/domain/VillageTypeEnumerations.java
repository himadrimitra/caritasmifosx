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
package org.apache.fineract.portfolio.village.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;


public class VillageTypeEnumerations {

public static EnumOptionData status(final Integer statusId){
        
        return status(VillageTypeStatus.fromInt(statusId));
    }
    
    public static EnumOptionData status(final VillageTypeStatus status){
        
        EnumOptionData optionData = new EnumOptionData(VillageTypeStatus.INVALID.getValue().longValue(), VillageTypeStatus.INVALID.getCode(), "Invalid");
        
        switch(status){
            case INVALID:
                optionData = new EnumOptionData(VillageTypeStatus.INVALID.getValue().longValue(), VillageTypeStatus.INVALID.getCode(), "Invalid");
            break;
            case PENDING: 
                optionData = new EnumOptionData(VillageTypeStatus.PENDING.getValue().longValue(), VillageTypeStatus.PENDING.getCode(), "Pending");
            break;  
            case ACTIVE:
                optionData = new EnumOptionData(VillageTypeStatus.ACTIVE.getValue().longValue(), VillageTypeStatus.ACTIVE.getCode(), "Active");
            break;
            case CLOSED: 
                optionData = new EnumOptionData(VillageTypeStatus.CLOSED.getValue().longValue(), VillageTypeStatus.CLOSED.getCode(), "Closed");
            break;
            case REJECT: 
                optionData = new EnumOptionData(VillageTypeStatus.REJECT.getValue().longValue(), VillageTypeStatus.REJECT.getCode(), "Reject");
            break;
        }
        return optionData;
    }

}
