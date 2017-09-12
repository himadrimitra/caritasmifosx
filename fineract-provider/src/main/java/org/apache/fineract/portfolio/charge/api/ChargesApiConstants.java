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
package org.apache.fineract.portfolio.charge.api;

public class ChargesApiConstants {

    public static final String glAccountIdParamName = "incomeAccountId";
    public static final String taxGroupIdParamName = "taxGroupId";
    public static final int applyUpfrontFeeOnFirstInstallment = 1;
    public static final String emiRoundingGoalSeekParamName = "emiRoundingGoalSeek";
    public static final String isGlimChargeParamName = "isGlimCharge";
    public static final String glimChargeCalculation = "glimChargeCalculation";
    public static final String slabsParamName = "slabs";
    public static final String minValueParamName = "minValue";
    public static final String maxValueParamName = "maxValue";
    public static final String amountParamName = "amount";
    public static final String typeParamName = "type";
    public static final String isCapitalizedParamName = "isCapitalized";
    public static final String subSlabsParamName = "subSlabs";
    
    public static final String percentageTypeParamName = "percentageType";
    public static final String percentagePeriodTypeParamName = "percentagePeriodType";	
    
    //overdue charge related parameters 
    
    public static final String overdueChargeDetailParamName = "overdueChargeDetail";
    public static final String gracePeriodParamName = "gracePeriod";
    public static final String penaltyFreePeriodParamName = "penaltyFreePeriod";
    public static final String graceTypeParamName = "graceType";
    public static final String applyChargeForBrokenPeriodParamName = "applyChargeForBrokenPeriod";
    public static final String isBasedOnOriginalScheduleParamName = "isBasedOnOriginalSchedule";
    public static final String considerOnlyPostedInterestParamName = "considerOnlyPostedInterest";
    public static final String calculateChargeOnCurrentOverdueParamName = "calculateChargeOnCurrentOverdue";    
    public static final String minOverdueAmountRequiredParamName = "minOverdueAmountRequired";    

}
