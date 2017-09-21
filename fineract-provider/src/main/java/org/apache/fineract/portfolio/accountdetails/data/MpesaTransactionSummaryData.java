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
package org.apache.fineract.portfolio.accountdetails.data;

import java.math.BigDecimal;
public class MpesaTransactionSummaryData {

	private final Long id;
	private final String accountNo;
	private final BigDecimal chargeAmount;
	private final String TxnDate;
	private final BigDecimal Amount;
	private final String clientName;
	
	public MpesaTransactionSummaryData(Long id, String accountNo,
			BigDecimal chargeAmount, String txnDate, BigDecimal amount,
			String clientName) {
		super();
		this.id = id;
		this.accountNo = accountNo;
		this.chargeAmount = chargeAmount;
		this.TxnDate = txnDate;
		this.Amount = amount;
		this.clientName = clientName;
	}

	public Long getId() {
		return this.id;
	}

	public String getAccountNo() {
		return this.accountNo;
	}

	public BigDecimal getChargeAmount() {
		return this.chargeAmount;
	}

	public String getTxnDate() {
		return this.TxnDate;
	}

	public BigDecimal getAmount() {
		return this.Amount;
	}

	public String getClientName() {
		return this.clientName;
	} 
	
	
	
		
}
