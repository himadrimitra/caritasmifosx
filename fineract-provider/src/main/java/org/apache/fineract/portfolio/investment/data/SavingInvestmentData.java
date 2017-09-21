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
import java.util.List;

import org.joda.time.LocalDate;

public class SavingInvestmentData implements Comparable<SavingInvestmentData> {

    final Long loan_id;
    final Long client_id;
    final String name;
    final String accountno;
    final Long loanammount;
    final String productname;
    final Long savigId;
    final List<Long> loanId;
    final Long investedAmount;
    final LocalDate startDate;
    final LocalDate closeDate;

    
    public SavingInvestmentData(Long loan_id,Long client_id, String name, String accountno, Long loanammount, String productname, Long savigId,
            List<Long> loanId,  Long investedAmount, LocalDate startDate, LocalDate closeDate) {
        super();
        this.loan_id = loan_id;
        this.client_id = client_id;
        this.name = name;
        this.accountno = accountno;
        this.loanammount = loanammount;
        this.productname = productname;
        this.savigId = savigId;
        this.loanId = loanId;
        this.investedAmount = investedAmount;
        this.startDate = startDate;
        this.closeDate = closeDate;
    }

    
    public Long getClient_id() {
		return this.client_id;
	}


	public Long getSavigId() {
        return this.savigId;
    }

    
    public List<Long> getLoanId() {
        return this.loanId;
    }

    public String getAccountno() {
        return this.accountno;
    }

    public Long getLoan_id() {
        return this.loan_id;
    }
    
    
    public String getProductname() {
        return this.productname;
    }

    public String getName() {
        return this.name;
    }

    public LocalDate getStartDate() {
		return this.startDate;
	}


	public LocalDate getCloseDate() {
		return this.closeDate;
	}


	public Long getLoanammount() {
        return this.loanammount;
    }

    public Long getInvestedAmount() {
		return this.investedAmount;
	}
 

    @Override
    public int compareTo(SavingInvestmentData o) {
       
        return 0;
    }
    public static SavingInvestmentData instance(Long loan_id,Long client_id, String name, String accountno, Long loanammount, String productname, Long savingId,
            List<Long> loanId,  Long investedAmount, LocalDate startDate, LocalDate closeDate) {
       
        return new SavingInvestmentData(loan_id,client_id, accountno, name, loanammount, productname, savingId, loanId, investedAmount, startDate, closeDate);
    }


	

}
