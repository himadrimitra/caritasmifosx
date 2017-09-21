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

import java.util.Date;

import org.joda.time.LocalDate;


public class LoanInvestmentData {
    
    final Long saving_id;
    final Long group_id;
    final String name;
    final String accountno;
    final Long savingamount;
    final String productname;
    final Long investedAmount;
    final LocalDate startDate;
    final LocalDate closeDate;
    public LoanInvestmentData(Long saving_id,Long group_id, String name, String accountno, 
    		Long savingamount, String productname, Long investedAmount, LocalDate startDate, LocalDate closeDate) {
        super();
        this.saving_id = saving_id;
        this.name = name;
        this.accountno = accountno;
        this.savingamount = savingamount;
        this.productname = productname;
        this.investedAmount = investedAmount;
        this.group_id = group_id;
        this.startDate = startDate;
        this.closeDate = closeDate;
    }
    
    public Long getGroup_id() {
		return this.group_id;
	}

	public Long getSaving_id() {
        return this.saving_id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getAccountno() {
        return this.accountno;
    }
    
    public Long getSavingamount() {
        return this.savingamount;
    }
    
    public String getProductname() {
        return this.productname;
    }
   
    public Long getInvestedAmount() {
		return this.investedAmount;
	}

	public static LoanInvestmentData intance (Long saving_id,Long group_id, String name, String accountno, Long savingamount,
            String productname, Long investedAmount, LocalDate startDate, LocalDate closeDate){
        return new LoanInvestmentData(saving_id,group_id, name, accountno, savingamount, productname, investedAmount, startDate, closeDate);
    }
}
