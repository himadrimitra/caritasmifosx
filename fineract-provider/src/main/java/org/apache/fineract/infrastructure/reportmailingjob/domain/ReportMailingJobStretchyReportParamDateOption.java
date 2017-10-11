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
package org.apache.fineract.infrastructure.reportmailingjob.domain;

public enum ReportMailingJobStretchyReportParamDateOption {
    INVALID(0, "reportMailingJobStretchyReportParamDateOption.invalid", "invalid"),
    TODAY(1, "reportMailingJobStretchyReportParamDateOption.today", "today"),
    YESTERDAY(2, "reportMailingJobStretchyReportParamDateOption.yesterday", "yesterday"),
    TOMORROW(3, "reportMailingJobStretchyReportParamDateOption.tomorrow", "tomorrow");
    
    private String code;
    private String value;
    private Integer id;
    
    /**
     * @param id
     * @param code
     * @param value
     */
    private ReportMailingJobStretchyReportParamDateOption(final Integer id, final String code, final String value) {
        this.value = value;
        this.code = code;
        this.id = id;
    }
    
    /**
     * @param value
     * @return
     */
    public static ReportMailingJobStretchyReportParamDateOption instance(final String value) {
        ReportMailingJobStretchyReportParamDateOption reportMailingJobStretchyReportParamDateOption = INVALID;
        
        switch (value) {
            case "today":
                reportMailingJobStretchyReportParamDateOption = TODAY;
                break;
                
            case "yesterday":
                reportMailingJobStretchyReportParamDateOption = YESTERDAY;
                break;
                
            case "tomorrow":
                reportMailingJobStretchyReportParamDateOption = TOMORROW;
                break;
        }
        
        return reportMailingJobStretchyReportParamDateOption;
    }
    
    /**
     * @param id
     * @return
     */
    public static ReportMailingJobStretchyReportParamDateOption instance(final Integer id) {
        ReportMailingJobStretchyReportParamDateOption reportMailingJobStretchyReportParamDateOption = INVALID;
        
        switch (id) {
            case 1:
                reportMailingJobStretchyReportParamDateOption = TODAY;
                break;
                
            case 2:
                reportMailingJobStretchyReportParamDateOption = YESTERDAY;
                break;
                
            case 3:
                reportMailingJobStretchyReportParamDateOption = TOMORROW;
                break;
        }
        
        return reportMailingJobStretchyReportParamDateOption;
    }
    
    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }
    
    /** 
     * @return list of valid ReportMailingJobEmailAttachmentFileFormat values
     **/
    public static Object[] validValues() {
        return new Object[] { TODAY.value, YESTERDAY.value, TOMORROW.value };
    }
}
