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

import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;

public class SavingIdListData {

    private final Long savingId;
    private final LocalDate maxTransactionDate;
    private final LocalDate startFeeChargeDate;
    private final LocalDate activateOnDate;

    public SavingIdListData(final Long savingId, final LocalDate maxTransactionDate, final LocalDate startFeeChargeDate,
            final LocalDate activateOnDate) {
        super();
        this.savingId = savingId;
        this.maxTransactionDate = maxTransactionDate;
        this.startFeeChargeDate = startFeeChargeDate;
        this.activateOnDate = activateOnDate;
    }

    public static SavingIdListData instance(Long savingId, LocalDate maxTransactionDate, LocalDate startFeeChargeDate) {
        return new SavingIdListData(savingId, maxTransactionDate, startFeeChargeDate, null);
    }

    public static SavingIdListData insatanceForAllSavingId(Long savingId, LocalDate activateOnDate, LocalDate startFeeChargDate) {
        return new SavingIdListData(savingId, null, startFeeChargDate, activateOnDate);
    }
    
 
    public LocalDate getMaxTransactionDate() {
        return this.maxTransactionDate;
    }

    public LocalDate getActivateOnDate() {
        return this.activateOnDate;
    }

    public LocalDate getStartFeeChargeDate() {
        return this.startFeeChargeDate;
    }

    public Long getSavingId() {
        return this.savingId;
    }

	public static SavingIdListData instaceForTransactionDate(LocalDate txnDate) {
		return new SavingIdListData(null, txnDate,null, null);
	}
}