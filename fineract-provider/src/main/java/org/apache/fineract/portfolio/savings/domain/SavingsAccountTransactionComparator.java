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
package org.apache.fineract.portfolio.savings.domain;

import java.util.Comparator;

/**
 * Sort savings account transactions by transaction date and transaction type
 * placing
 */
public class SavingsAccountTransactionComparator implements Comparator<SavingsAccountTransaction> {

    @Override
    public int compare(final SavingsAccountTransaction o1, final SavingsAccountTransaction o2) {
        int compareResult = 0;
        final int comparsion = o1.transactionLocalDate().compareTo(o2.transactionLocalDate());
		if (comparsion == 0) {
			if(o1.isInterestPosting() && !o2.isInterestPosting()){
				compareResult = -1;
	        }
			if(!o1.isInterestPosting() && o2.isInterestPosting()){
				compareResult = 1;
	        }
			if (compareResult == 0) {
				compareResult = o1.createdDate().compareTo(o2.createdDate());
			}
		} else {
            compareResult = comparsion;
        }
        return compareResult;
    }
}