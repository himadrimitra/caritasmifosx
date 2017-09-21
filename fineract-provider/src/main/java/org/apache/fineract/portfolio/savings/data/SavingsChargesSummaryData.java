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
package org.apache.fineract.portfolio.savings.data;

import java.math.BigDecimal;

public class SavingsChargesSummaryData {
    
	private final Long id;
    private final String accountNo;	
	private final String chargeName;
	private final BigDecimal chargeDue;
	private final Long chargeId;
	private final String  date; 
	
	
       
	public SavingsChargesSummaryData(Long id, String accountNo,
			String chargeName, BigDecimal chargeDue, Long chargeId, String date) {
		super();
		this.id = id;
		this.accountNo = accountNo;
		this.chargeName = chargeName;
		this.chargeDue = chargeDue;
		this.chargeId = chargeId;
		this.date = date;
	}

	public String getDate() {
		return this.date;
	}

	public Long getId() {
		return this.id;
	}

	public String getAccountNo() {
		return this.accountNo;
	}

	public String getChargeName() {
		return this.chargeName;
	}

	public BigDecimal getChargeDue() {
		return this.chargeDue;
	}

	public Long getChargeId() {
		return this.chargeId;
	}
	

}
