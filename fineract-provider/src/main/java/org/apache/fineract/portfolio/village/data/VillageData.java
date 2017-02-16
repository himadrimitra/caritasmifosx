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

import com.finflux.kyc.address.data.AddressData;


public class VillageData {

    
    public Long getVillageId() {
        return this.villageId;
    }

    
    public String getVillageName() {
        return this.villageName;
    }

    private final Long villageId;
    private final String externalId;
    private final Long officeId;
    private final String officeName;
    private final String villageCode;
    private final String villageName;
    private final Long counter;
    private final EnumOptionData status;
    
    // template
    private final Collection<OfficeData> officeOptions;
    
    //associations
    private final Collection<CenterData> setOfCenters;
    private final Collection<AddressData> addressData;
    private Collection<CenterData> centers;
    
    final VillageTimelineData timeline;
    
    public static VillageData template(final Long officeId, final Collection<OfficeData> officeOptions) {
        final Collection<CenterData> centers = null;
        return new VillageData(null, null, officeId, null, null, null, null, officeOptions, null, null, null,null, centers);
    }
    
    private VillageData(final Long id, final String externalId, final Long officeId, final String officeName, final String villageCode,
            final String villageName, final Long counter, final Collection<OfficeData> officeOptions, final EnumOptionData status, final VillageTimelineData timeline, final Collection<CenterData> setOfCenters,final Collection<AddressData> addressData, final Collection<CenterData> centers){
        
        this.villageId = id;
        this.externalId = externalId;
        this.officeId = officeId;
        this.officeName = officeName;
        this.villageCode = villageCode;
        this.villageName = villageName;
        this.counter = counter;
        this.officeOptions = officeOptions;
        this.status = status;
        this.timeline = timeline;
        this.setOfCenters = setOfCenters;
        this.addressData = addressData;
        this.centers = centers;
    }
    
    public static VillageData instance(final Long id, final String externalId, final Long officeId, final String officeName, final String villageCode, 
            final String villageName, final Long counter, 
            final EnumOptionData status, final VillageTimelineData timeline) {
        final Collection<CenterData> centers = null;

        return new VillageData(id, externalId, officeId, officeName, villageCode, villageName, counter, null, status, 
                 timeline, null,null, centers);
    }
    
    public static VillageData lookup(final Long id, final String villageName) {
        final Collection<CenterData> centers = null;

        return new VillageData(id, null, null, null, null, villageName, null, null, null, null, null,null, centers);
    }
    
    public static VillageData countValue(final Long counter, final String villageName) {
        final Collection<CenterData> centers = null;

        return new VillageData(null, null, null, null, null, villageName, counter, null, null, null, null,null, centers);
    }

    public static VillageData withAssociations(VillageData village, Collection<CenterData> centers, Collection<AddressData> address,Collection<CenterData> hierarchy) {
        return new VillageData(village.villageId, village.externalId, village.officeId, village.officeName, village.villageCode, village.villageName, 
                village.counter, null, village.status, village.timeline, centers,address, hierarchy);
    }
    
}
