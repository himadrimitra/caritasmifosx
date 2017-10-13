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


public enum  VillageTypeStatus {

    INVALID(0, "VillageStatusType.invalid"), //
    PENDING(100, "VillageStatusType.pending"), //
    ACTIVE(300, "VillageStatusType.active"), //
    REJECT(400, "VillageStatusType.reject"), //
    CLOSED(600, "VillageStatusType.closed");
    
    private final Integer value;
    private final String code;
    
    public static VillageTypeStatus fromInt(final Integer statusValue){
        
        VillageTypeStatus enumeration = VillageTypeStatus.INVALID;
        switch(statusValue){
            
            case 100:
                enumeration = VillageTypeStatus.PENDING;
            break;
            case 300:
                enumeration = VillageTypeStatus.ACTIVE;
            break;
            case 400:
                enumeration = VillageTypeStatus.REJECT;
            break;
            case 600:
                enumeration = VillageTypeStatus.CLOSED;
            break;    
        }
        return enumeration;
    }
    
    private VillageTypeStatus(final Integer value, final String code){
        this.value = value;
        this.code = code;
    }
    
    public boolean hasStateOf(final VillageTypeStatus state){
        return this.value.equals(state.getValue());
    }

    
    public Integer getValue() {
        return this.value;
    }

    
    public String getCode() {
        return this.code;
    }
    
    public boolean isPending(){
        return this.value.equals(VillageTypeStatus.PENDING.getValue());
    }
    
    public boolean isActive(){
        return this.value.equals(VillageTypeStatus.ACTIVE.getValue());
    }
    
    public boolean isClosed(){
        return this.value.equals(VillageTypeStatus.CLOSED.getValue());
    }

    public boolean isReject() {
        return this.value.equals(VillageTypeStatus.REJECT.getValue());
    }

}
