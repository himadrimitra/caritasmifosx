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
package org.apache.fineract.accounting.journalentry.data;

public class JournalEntryAssociationParametersData {

    private final boolean transactionDetailsRequired;
    private final boolean runningBalanceRequired;
    private final boolean officeRunningBalancerequired;

    public JournalEntryAssociationParametersData() {
        this.transactionDetailsRequired = false;
        this.runningBalanceRequired = false;
        this.officeRunningBalancerequired = false;
    }

    public JournalEntryAssociationParametersData(final boolean transactionDetailsRequired, final boolean runningBalanceRequired,
            final boolean officeRunningBalancerequired) {
        this.transactionDetailsRequired = transactionDetailsRequired;
        this.runningBalanceRequired = runningBalanceRequired;
        this.officeRunningBalancerequired = officeRunningBalancerequired;
    }

    public boolean isTransactionDetailsRequired() {
        return this.transactionDetailsRequired;
    }

    public boolean isRunningBalanceRequired() {
        return this.runningBalanceRequired;
    }

    public boolean isOfficeRunningBalancerequired() {
        return this.officeRunningBalancerequired;
    }
}
