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
package org.apache.fineract.infrastructure.sms.domain;

public enum SmsCampaignType {
    DIRECT(1,"smsCampaignStatusType.direct"),
    SCHEDULE(2,"smsCampaignStatusType.schedule");

    private Integer value;
    private String code;

    private SmsCampaignType(Integer value, String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public static SmsCampaignType fromInt(final Integer typeValue) {
        SmsCampaignType type = null;
        switch (typeValue) {
            case 1:
                type = DIRECT;
                break;
            case 2:
                type = SCHEDULE;
                break;
        }
        return type;
    }

    public boolean isDirect(){
        return this.value.equals(SmsCampaignType.DIRECT.getValue());
    }

    public boolean isSchedule(){
        return this.value.equals(SmsCampaignType.SCHEDULE.getValue());
    }
}
