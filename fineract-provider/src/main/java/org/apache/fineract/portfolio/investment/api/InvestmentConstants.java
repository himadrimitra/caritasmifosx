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
package org.apache.fineract.portfolio.investment.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;



public class InvestmentConstants {
    
	public static final String INVESTMET_RESOURCE_NAME = "investment";
    public static final String savingIdParamName = "savingId";
    public static final String loanIdParamName = "loanId";
    public static final String SAVINGINVESTMENT_RESOURCE_NAME = "investedAmounts";
    public static final String LOANINVESTMENT_RESOURCE_NAME = "investedAmounts";
    
    public static final String invesetedAmountParamName = "investedAmounts";
    public static final String startInvestmentDateParamName = "startDate";
    public static final String closeInvestmentDateParamName = "closeDate";
    
    
    
    
    //Saving Investment data request parameter
    
    public static final Set<String> CREATE_SAVING_INVESTMENT_REQUEST_PARAMETERS = new HashSet<>(Arrays.asList(savingIdParamName,
    		loanIdParamName,startInvestmentDateParamName,invesetedAmountParamName));
    
    public static final Set<String> CREATE_LOAN_INVESTMENT_REQUEST_PARAMETERS = new HashSet<>(Arrays.asList(loanIdParamName, savingIdParamName,
    		startInvestmentDateParamName,invesetedAmountParamName));
   
}