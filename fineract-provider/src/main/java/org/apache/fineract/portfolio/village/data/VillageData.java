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
package org.apache.fineract.portfolio.village.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.portfolio.group.data.CenterData;


public class VillageData {

    private final Long villageId;
    private final String externalId;
    private final Long officeId;
    private final String officeName;
    private final String villageCode;
    private final String villageName;
    private final Long counter;
    private final String taluk;
    private final String district;
    private final Long pincode;
    private final String state;
    private final EnumOptionData status;
    
    // template
    private final Collection<OfficeData> officeOptions;
    
    //associations
    private final Collection<CenterData> setOfCenters;
    
    final VillageTimelineData timeline;
    
    public static VillageData template(final Long officeId, final Collection<OfficeData> officeOptions) {
        
        return new VillageData(null, null, officeId, null, null, null, null, officeOptions, null, null, null, null, null, null, null);
    }
    
    private VillageData(final Long id, final String externalId, final Long officeId, final String officeName, final String villageCode,
            final String villageName, final Long counter, final Collection<OfficeData> officeOptions, final String taluk, final String district, 
            final Long pincode, final String state, final EnumOptionData status, final VillageTimelineData timeline, final Collection<CenterData> setOfCenters){
        
        this.villageId = id;
        this.externalId = externalId;
        this.officeId = officeId;
        this.officeName = officeName;
        this.villageCode = villageCode;
        this.villageName = villageName;
        this.counter = counter;
        this.officeOptions = officeOptions;
        this.taluk = taluk;
        this.district = district;
        this.pincode = pincode;
        this.state = state;
        this.status = status;
        this.timeline = timeline;
        this.setOfCenters = setOfCenters;
    }
    
    public static VillageData instance(final Long id, final String externalId, final Long officeId, final String officeName, final String villageCode, 
            final String villageName, final Long counter, final String taluk, final String district, final Long pincode, final String state, 
            final EnumOptionData status, final VillageTimelineData timeline) {
        
        return new VillageData(id, externalId, officeId, officeName, villageCode, villageName, counter, null, taluk, district, pincode, state, status, 
                 timeline, null);
    }
    
    public static VillageData lookup(final Long id, final String villageName) {
        
        return new VillageData(id, null, null, null, null, villageName, null, null, null, null, null, null, null, null, null);
    }
    
    public static VillageData countValue(final Long counter, final String villageName) {
        
        return new VillageData(null, null, null, null, null, villageName, counter, null, null, null, null, null, null, null, null);
    }

    public static VillageData withAssociations(VillageData village, Collection<CenterData> centers) {
        return new VillageData(village.villageId, village.externalId, village.officeId, village.officeName, village.villageCode, village.villageName, 
                village.counter, null, village.taluk, village.district, village.pincode, village.state, village.status, village.timeline, centers);
    }
    
}
