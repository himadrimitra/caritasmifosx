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

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.joda.time.LocalDate;

/**
 * Immutable object representing a General Ledger Account
 *
 * Note: no getter/setters required as google will produce json from fields of
 * object.
 */
public class JournalEntryData {

    private final Long id;
    private final Long officeId;
    @SuppressWarnings("unused")
    private final String officeName;
    @SuppressWarnings("unused")
    private final LocalDate transactionDate;

    @SuppressWarnings("unused")
    private final LocalDate valueDate;
    @SuppressWarnings("unused")
    private final LocalDate effectiveDate;

    @SuppressWarnings("unused")
    private final CurrencyData currency;
    private final String transactionId;
    @SuppressWarnings("unused")
    private final Boolean manualEntry;
    @SuppressWarnings("unused")
    private final EnumOptionData entityType;
    @SuppressWarnings("unused")
    private final Long entityId;

    @SuppressWarnings("unused")
    private final Long entityTransactionId;
    @SuppressWarnings("unused")
    private final Long createdByUserId;
    @SuppressWarnings("unused")
    private final LocalDate createdDate;
    @SuppressWarnings("unused")
    private final String createdByUserName;
    @SuppressWarnings("unused")
    private final String comments;
    @SuppressWarnings("unused")
    private final Boolean reversed;
    @SuppressWarnings("unused")
    private final String referenceNumber;

    private final Long reversalId;

    @SuppressWarnings("unused")
    private final TransactionDetailData transactionDetails;

    private Collection<JournalEntryDetailData> journalEntryDetails;

    private final Boolean isReversalEntry;

    public JournalEntryData(final Long id, final Long officeId, final String officeName, final LocalDate transactionDate,
            final LocalDate valueDate, final LocalDate effectiveDate, final String transactionId, final Boolean manualEntry,
            final EnumOptionData entityType, final Long entityId, final Long entityTransactionId, final Long createdByUserId,
            final LocalDate createdDate, final String createdByUserName, final String comments, final Boolean reversed,
            final String referenceNumber, final TransactionDetailData transactionDetailData, final CurrencyData currency,
            final Collection<JournalEntryDetailData> journalEntryDetails, final Long reversalId, final Boolean isReversalEntry) {
        this.id = id;
        this.officeId = officeId;
        this.officeName = officeName;
        this.transactionDate = transactionDate;
        this.valueDate = valueDate;
        this.effectiveDate = effectiveDate;
        this.transactionId = transactionId;
        this.manualEntry = manualEntry;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityTransactionId = entityTransactionId;
        this.createdByUserId = createdByUserId;
        this.createdDate = createdDate;
        this.createdByUserName = createdByUserName;
        this.comments = comments;
        this.reversed = reversed;
        this.referenceNumber = referenceNumber;
        this.transactionDetails = transactionDetailData;
        this.currency = currency;
        this.journalEntryDetails = journalEntryDetails;
        this.reversalId = reversalId;
        this.isReversalEntry = isReversalEntry;
    }

    public Long getId() {
        return this.id;
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public Collection<JournalEntryDetailData> getJournalEntryDetails() {
        return this.journalEntryDetails;
    }

    public void setJournalEntryDetails(final Collection<JournalEntryDetailData> journalEntryDetails) {
        this.journalEntryDetails = journalEntryDetails;
    }

    public boolean isReversed() {
        return this.reversed;
    }

    public Long getReversedJournalEntryId() {
        return this.reversalId;
    }
}