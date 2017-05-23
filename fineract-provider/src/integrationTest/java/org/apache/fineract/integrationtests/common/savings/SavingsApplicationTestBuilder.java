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
package org.apache.fineract.integrationtests.common.savings;

import java.util.HashMap;

import com.google.gson.Gson;

public class SavingsApplicationTestBuilder {

    private static final String LOCALE = "en_GB";

    private String submittedOnDate = "";
    
    private HashMap<String, String> addParams = null;
    
    private String allowOverdraft = null;
    private String allowDpLimit = null;
    private String overdraftLimit = null;
    private String savingsDpLimitCalculationType = null;
    private String dpLimitAmount = null;
    private String dpCalculateOnAmount = null;
    private String dpStartDate = null;
    private String dpDuration = null;

    public String build(final String ID, final String savingsProductId, final String accountType) {

        final HashMap<String, String> map = new HashMap<>();
        map.put("dateFormat", "dd MMMM yyyy");
        if (accountType == "GROUP") {
            map.put("groupId", ID);
        } else {
            map.put("clientId", ID);
        }        
        map.put("productId", savingsProductId);
        if (this.allowDpLimit != null && this.allowDpLimit.equalsIgnoreCase("true")) {
            map.put("allowOverdraft", this.allowOverdraft);
            map.put("allowDpLimit", this.allowDpLimit);
            map.put("overdraftLimit", this.overdraftLimit);
            map.put("savingsDpLimitCalculationType", this.savingsDpLimitCalculationType);
            map.put("dpLimitAmount", this.dpLimitAmount);
            map.put("dpCalculateOnAmount", this.dpCalculateOnAmount);
            map.put("dpStartDate", this.dpStartDate);
            map.put("dpDuration", this.dpDuration);
        }
        map.put("locale", LOCALE);
        map.put("submittedOnDate", this.submittedOnDate);
        if(addParams!=null && addParams.size() > 0){
        	map.putAll(addParams);
        }
        String savingsApplicationJSON = new Gson().toJson(map);
        System.out.println(savingsApplicationJSON);
        return savingsApplicationJSON;
    }

    public SavingsApplicationTestBuilder withSubmittedOnDate(final String savingsApplicationSubmittedDate) {
        this.submittedOnDate = savingsApplicationSubmittedDate;
        return this;
    }

    public SavingsApplicationTestBuilder withParams(HashMap<String, String> params) {
        this.addParams = params;
        return this;
    }

    public void withAllowDpLimit() {
        this.allowOverdraft = "true";
        this.allowDpLimit = "true";
        this.overdraftLimit = "100000.00";
        this.savingsDpLimitCalculationType = "1";
        this.dpLimitAmount = "10000.00";
        this.dpCalculateOnAmount = "1000";
        this.dpStartDate = "02 January 2017";
        this.dpDuration = "10";
    }
}