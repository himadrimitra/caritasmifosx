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
package org.apache.fineract.portfolio.investment.service;

import java.util.List;

import org.apache.fineract.portfolio.investment.data.InvestmentBatchJobData;

public interface InvestmentBatchJobReadPlatformService {
	
	List<InvestmentBatchJobData> validateForInvestmentSplit(String[] productId, String Date, String investmentId);
	List<InvestmentBatchJobData> getAllInvestementDataWithMaturedStatus(Long investmentId);
	List<InvestmentBatchJobData> getInvestmentIdsWithMaturedStatus(Long investmentId);
	List<InvestmentBatchJobData> getAllInvestmentIdsWithMaturedStatus();
	
	InvestmentBatchJobData getInterestDetails();
	InvestmentBatchJobData getLoanClosedDate(Long loanId);
    
	InvestmentBatchJobData getPaymentType();
	
	InvestmentBatchJobData getInvestmentStatus(Long investmentId);
	InvestmentBatchJobData getLoanIdStatus(Long loanId);
	
	InvestmentBatchJobData getTotalInvestedAmount(Long loanId);
	
	InvestmentBatchJobData getTotalLoanChargeAmount(Long loanId);
	Long getOfficeIdOfSavingAccount(Long savingId);
}
