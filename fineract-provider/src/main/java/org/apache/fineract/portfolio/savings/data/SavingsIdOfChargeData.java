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

import org.joda.time.LocalDate;


public class SavingsIdOfChargeData {

    final Long savingId;
    final LocalDate dueDate;

       public SavingsIdOfChargeData(Long savingId, LocalDate dueDate) {
        super();
        this.savingId = savingId;
        this.dueDate = dueDate;
    }

    
    public Long getSavingId() {
        return this.savingId;
    }
    

    public LocalDate getDueDate() {
               return this.dueDate;
       }
    
    public static SavingsIdOfChargeData instance(Long savingId){
        return new SavingsIdOfChargeData(savingId,null);
    }
    
    public static SavingsIdOfChargeData instanceForDueDate(LocalDate dueDate){
       return new SavingsIdOfChargeData(null, dueDate);
    }
    
}