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
package org.apache.fineract.portfolio.investment.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.investment.api.InvestmentConstants;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class InvestmentDataValidator {

	 private final FromJsonHelper fromApiJsonHelper;
	 
	 @Autowired
	 public InvestmentDataValidator(final FromJsonHelper fromApiJsonHelper){
		 this.fromApiJsonHelper = fromApiJsonHelper;
	 }
	 
	 public void validateForCreateSavingInvestment(final String json){
		 if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
		 
		 final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		  this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, InvestmentConstants.CREATE_SAVING_INVESTMENT_REQUEST_PARAMETERS);

		  final JsonElement element = this.fromApiJsonHelper.parse(json);
		  
		  final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		  
		  final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
           .resource(InvestmentConstants.INVESTMET_RESOURCE_NAME);

		   
		  final Long savingId = this.fromApiJsonHelper.extractLongNamed(InvestmentConstants.savingIdParamName, element);
		  baseDataValidator.reset().parameter(InvestmentConstants.savingIdParamName).value(savingId).notNull();
		  
		  
		  final String[] loanId = this.fromApiJsonHelper.extractArrayNamed(InvestmentConstants.loanIdParamName, element);
		  baseDataValidator.reset().parameter(InvestmentConstants.loanIdParamName).value(loanId).arrayNotEmpty();
		  
		  
		  if(this.fromApiJsonHelper.parameterExists(InvestmentConstants.startInvestmentDateParamName, element)){
			  final String[] investmentStartDate = this.fromApiJsonHelper.extractArrayNamed(InvestmentConstants.startInvestmentDateParamName, element);
			  baseDataValidator.reset().parameter(InvestmentConstants.startInvestmentDateParamName).value(investmentStartDate).arrayNotEmpty();
		  }
		  
		  if(this.fromApiJsonHelper.parameterExists(InvestmentConstants.invesetedAmountParamName, element)){
			  final String[] investedAmount = this.fromApiJsonHelper.extractArrayNamed(InvestmentConstants.invesetedAmountParamName, element);
			  baseDataValidator.reset().parameter(InvestmentConstants.invesetedAmountParamName).value(investedAmount).arrayNotEmpty();
		  }
		  
		  throwExceptionIfValidationWarningsExist(dataValidationErrors);
		  
	 }
	 
	 
	 public void validateForCreateLoanInvestment(final String json){
		 
		 final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		  this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, InvestmentConstants.CREATE_LOAN_INVESTMENT_REQUEST_PARAMETERS);

		  final JsonElement element = this.fromApiJsonHelper.parse(json);
		  
		  final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		  
		  final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
          .resource(InvestmentConstants.INVESTMET_RESOURCE_NAME);
		  
		  final Long loanId = this.fromApiJsonHelper.extractLongNamed(InvestmentConstants.loanIdParamName, element);
		  baseDataValidator.reset().parameter(InvestmentConstants.loanIdParamName).value(loanId).notNull();
		  
		  
		  final String[] savingId = this.fromApiJsonHelper.extractArrayNamed(InvestmentConstants.savingIdParamName, element);
		  baseDataValidator.reset().parameter(InvestmentConstants.savingIdParamName).value(savingId).arrayNotEmpty();
		  
		  
		  if(this.fromApiJsonHelper.parameterExists(InvestmentConstants.startInvestmentDateParamName, element)){
			  final String[] investmentStartDate = this.fromApiJsonHelper.extractArrayNamed(InvestmentConstants.startInvestmentDateParamName, element);
			  baseDataValidator.reset().parameter(InvestmentConstants.startInvestmentDateParamName).value(investmentStartDate).arrayNotEmpty();
		  }
		  
		  if(this.fromApiJsonHelper.parameterExists(InvestmentConstants.invesetedAmountParamName, element)){
			  final String[] investedAmount = this.fromApiJsonHelper.extractArrayNamed(InvestmentConstants.invesetedAmountParamName, element);
			  baseDataValidator.reset().parameter(InvestmentConstants.invesetedAmountParamName).value(investedAmount).arrayNotEmpty();
		  }
		  
		  throwExceptionIfValidationWarningsExist(dataValidationErrors);
		  
	 }
	 
	   private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	        if (!dataValidationErrors.isEmpty()) {
	            //
	            throw new PlatformApiDataValidationException(dataValidationErrors);
	        }
	    }
}
