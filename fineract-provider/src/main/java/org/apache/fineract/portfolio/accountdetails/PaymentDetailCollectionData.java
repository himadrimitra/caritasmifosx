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
package org.apache.fineract.portfolio.accountdetails;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentDetailCollectionData {

	private final BigDecimal amount;
	private final String TransactionDate;
	private final String receiptNumber;
	private final String Type;

	

	
	public String getTransactionDate() {
		return this.TransactionDate;
	}

	public PaymentDetailCollectionData(BigDecimal amount,
			String transactionDate, String receiptNumber, String type) {
		super();
		this.amount = amount;
		this.TransactionDate = transactionDate;
		this.receiptNumber = receiptNumber;
		this.Type = type;
	}

	public String getType() {
		return this.Type;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public String getReceiptNumber() {
		return this.receiptNumber;
	}

}
