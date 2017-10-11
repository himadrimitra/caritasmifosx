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
package org.apache.fineract.infrastructure.sms.data;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.Map;

@SuppressWarnings("unused")
public class SmsCampaignData {

    
    private Long id;
    private final String campaignName;
    private final Integer campaignType;
    private final Long runReportId;
    private final String paramValue;
    private final EnumOptionData campaignStatus;
    private final String message;
    private final DateTime nextTriggerDate;
    private final LocalDate lastTriggerDate;
    private final SmsCampaignTimeLine smsCampaignTimeLine;
    private final DateTime recurrenceStartDate;
    private final String recurrence;

    private SmsCampaignData(final Long id,final String campaignName, final Integer campaignType, final Long runReportId,
                           final String paramValue,final EnumOptionData campaignStatus,
                           final String message,final DateTime nextTriggerDate,final LocalDate lastTriggerDate,final SmsCampaignTimeLine smsCampaignTimeLine,
                           final DateTime recurrenceStartDate, final String recurrence) {
        this.id = id;
        this.campaignName = campaignName;
        this.campaignType = campaignType;
        this.runReportId = runReportId;
        this.paramValue = paramValue;
        this.campaignStatus =campaignStatus;
        this.message = message;
        if(nextTriggerDate !=null){
            this.nextTriggerDate = nextTriggerDate;
        }else{
            this.nextTriggerDate = null;
        }
        if(lastTriggerDate !=null){
            this.lastTriggerDate = lastTriggerDate;
        }else{
            this.lastTriggerDate = null;
        }
        this.smsCampaignTimeLine =smsCampaignTimeLine;
        this.recurrenceStartDate = recurrenceStartDate;
        this.recurrence  = recurrence;
    }

    public static SmsCampaignData instance(final Long id,final String campaignName, final Integer campaignType, final Long runReportId,
                                           final String paramValue,final EnumOptionData campaignStatus,final String message,
                                           final DateTime nextTriggerDate, final LocalDate lastTriggerDate,final SmsCampaignTimeLine smsCampaignTimeLine,
                                           final DateTime recurrenceStartDate, final String recurrence){
        return new SmsCampaignData(id,campaignName,campaignType,runReportId,paramValue,
                campaignStatus,message,nextTriggerDate,lastTriggerDate,smsCampaignTimeLine,recurrenceStartDate,recurrence);
    }


    public Long getId() {
        return id;
    }

    public String getCampaignName() {
        return this.campaignName;
    }

    public Integer getCampaignType() {
        return this.campaignType;
    }

    public Long getRunReportId() {
        return this.runReportId;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public EnumOptionData getCampaignStatus() {
        return this.campaignStatus;
    }

    public String getMessage() {
        return this.message;
    }


    public DateTime getNextTriggerDate() {
        return this.nextTriggerDate;
    }

    public LocalDate getLastTriggerDate() {
        return this.lastTriggerDate;
    }

    public String getRecurrence() {return this.recurrence;}

    public DateTime getRecurrenceStartDate() {return this.recurrenceStartDate;}
}
