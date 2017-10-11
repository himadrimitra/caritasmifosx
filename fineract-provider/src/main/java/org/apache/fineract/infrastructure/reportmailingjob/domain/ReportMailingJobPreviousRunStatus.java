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

public enum ReportMailingJobPreviousRunStatus {
    INVALID(-1, "ReportMailingJobPreviousRunStatus.invalid", "invalid"),
    SUCCESS(1, "ReportMailingJobPreviousRunStatus.success", "success"),
    ERROR(0, "ReportMailingJobPreviousRunStatus.error", "error");
    
    private final String code;
    private final String value;
    private final Integer id;
    
    private ReportMailingJobPreviousRunStatus(final Integer id, final String code, final String value) {
        this.value = value;
        this.code = code;
        this.id = id;
    }
    
    public static ReportMailingJobPreviousRunStatus instance(final String code) {
        ReportMailingJobPreviousRunStatus previousRunStatus = INVALID;
        
        switch (code) {
            case "success":
                previousRunStatus = SUCCESS;
                break;
                
            case "error":
                previousRunStatus = ERROR;
                break;
        }
        
        return previousRunStatus;
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
     * @return true/false 
     **/
    public boolean isSuccess() {
        return this.value.equals(SUCCESS.getValue());
    }
    
    /** 
     * @return boolean true/false 
     **/
    public boolean isError() {
        return this.value.equals(ERROR.getValue());
    }
}
