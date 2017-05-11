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
package org.apache.fineract.accounting.glaccount.service;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.data.GLAccountDataForLookup;
import org.apache.fineract.accounting.glaccount.domain.GLAccountType;
import org.apache.fineract.accounting.journalentry.data.JournalEntryAssociationParametersData;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;

public interface GLAccountReadPlatformService {

    List<GLAccountData> retrieveAllGLAccounts(Integer accountClassification, String searchParam, Integer usage,
            Boolean manualTransactionsAllowed, Boolean disabled, JournalEntryAssociationParametersData associationParametersData,
            final Integer glClassificationType);

    GLAccountData retrieveGLAccountById(long glAccountId, JournalEntryAssociationParametersData associationParametersData);

    List<GLAccountData> retrieveAllEnabledDetailGLAccounts();

    List<GLAccountData> retrieveAllEnabledDetailGLAccounts(GLAccountType accountType);

    List<GLAccountData> retrieveAllEnabledHeaderGLAccounts(GLAccountType accountType);

    GLAccountData retrieveNewGLAccountDetails(final Integer type);

    List<GLAccountDataForLookup> retrieveAccountsByTagId(final Long ruleId, final Integer transactionType);

    List<GLAccountDataForLookup> retrieveAccountsByTags(Collection<Long> tagIds);
    
    Page<GLAccountData> retrieveAllGLAccounts(JournalEntryAssociationParametersData associationParametersData,
            SearchParameters searchParameters, final Integer usage, final Integer accountType, final Integer glAccounytClassificatioType);
}