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
package org.apache.fineract.accounting.journalentry.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;

@Entity
@Table(name = "f_journal_entry")
public class JournalEntry extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @Column(name = "payment_details_id", nullable = true)
    private Long paymentDetailId;

    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode;

    @Column(name = "transaction_identifier", nullable = false, length = 50)
    private String transactionIdentifier;

    @Column(name = "entity_transaction_id", nullable = false)
    private Long entityTransactionId;

    @Column(name = "reversed", nullable = false)
    private boolean reversed = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reversal_id")
    private JournalEntry reversalJournalEntry;

    @Column(name = "manual_entry", nullable = false)
    private boolean manualEntry = false;

    @Column(name = "entry_date")
    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    @Column(name = "value_date")
    @Temporal(TemporalType.DATE)
    private Date valueDate;

    @Column(name = "effective_date")
    @Temporal(TemporalType.DATE)
    private Date effectiveDate;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "entity_type_enum", length = 50)
    private Integer entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "ref_num")
    private String referenceNumber;


    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "journal_entry_id", referencedColumnName = "id", nullable = false)
    private List<JournalEntryDetail> journalEntryDetails = new ArrayList<>();

    public static JournalEntry createNew(final Long officeId, final Long paymentDetailId, final String currencyCode,
            final String transactionIdentifier, final boolean manualEntry, final Date transactionDate, final Date valueDate,
            final Date effectiveDate, final String description, final Integer entityType, final Long entityId,
            final String referenceNumber, final Long entityTransactionId) {
        return new JournalEntry(officeId, paymentDetailId, currencyCode, transactionIdentifier, manualEntry, transactionDate,
                valueDate, effectiveDate, description, entityType, entityId, referenceNumber, entityTransactionId);
    }

    public static JournalEntry createNewForSystemEntries(final Long officeId, final String currencyCode,
            final String transactionIdentifier, final Date transactionDate, final Date valueDate, final Date effectiveDate,
            final Integer entityType, final Long entityId, final Long entityTransactionId) {
        final Long paymentDetailId = null;
        final boolean manualEntry = false;
        final String description = null;
        final String referenceNumber = null;
        return new JournalEntry(officeId, paymentDetailId, currencyCode, transactionIdentifier, manualEntry, transactionDate,
                valueDate, effectiveDate, description, entityType, entityId, referenceNumber, entityTransactionId);
    }

    protected JournalEntry() {
        //
    }

    private JournalEntry(final Long officeId, final Long paymentDetailId, final String currencyCode,
            final String transactionIdentifier, final boolean manualEntry, final Date transactionDate, final Date valueDate,
            final Date effectiveDate, final String description, final Integer entityType, final Long entityId,
            final String referenceNumber, final Long entityTransactionId) {
        this.officeId = officeId;
        this.paymentDetailId = paymentDetailId;
        this.reversalJournalEntry = null;
        this.transactionIdentifier = transactionIdentifier;
        this.reversed = false;
        this.manualEntry = manualEntry;
        this.transactionDate = transactionDate;
        this.valueDate = valueDate;
        this.effectiveDate = effectiveDate;
        this.description = StringUtils.defaultIfEmpty(description, null);
        this.entityType = entityType;
        this.entityId = entityId;
        this.referenceNumber = referenceNumber;
        this.currencyCode = currencyCode;
        this.entityTransactionId = entityTransactionId;
    }

    public void setReversalJournalEntry(final JournalEntry reversalJournalEntry) {
        this.reversalJournalEntry = reversalJournalEntry;
    }

    public void setReversed(final boolean reversed) {
        this.reversed = reversed;
    }

    public void addJournalEntryDetail(final JournalEntryDetail journalEntryDetail) {
        this.journalEntryDetails.add(journalEntryDetail);
    }

    public void addAllJournalEntryDetail(final List<JournalEntryDetail> journalEntryDetails) {
        this.journalEntryDetails.addAll(journalEntryDetails);
    }
    
    public List<JournalEntryDetail> getJournalEntryDetails() {
        return this.journalEntryDetails;
    }

    
    public Long getOfficeId() {
        return this.officeId;
    }

    
    public String getReferenceNumber() {
        return this.referenceNumber;
    }

    
    public Date getTransactionDate() {
        return this.transactionDate;
    }

    
    public Long getPaymentDetailId() {
        return this.paymentDetailId;
    }

    
    public String getCurrencyCode() {
        return this.currencyCode;
    }

    
    public String getTransactionIdentifier() {
        return this.transactionIdentifier;
    }

    
    public Long getEntityTransactionId() {
        return this.entityTransactionId;
    }

    
    public boolean isReversed() {
        return this.reversed;
    }

    
    public JournalEntry getReversalJournalEntry() {
        return this.reversalJournalEntry;
    }

    
    public boolean isManualEntry() {
        return this.manualEntry;
    }

    
    public Date getValueDate() {
        return this.valueDate;
    }

    
    public Date getEffectiveDate() {
        return this.effectiveDate;
    }
    
    public LocalDate getEffectiveDateAsLocalDate() {
        LocalDate effectiveDate = null;
        if (this.effectiveDate != null) {
            effectiveDate = new LocalDate(this.effectiveDate);
        }
        return effectiveDate;
    }

    
    public String getDescription() {
        return this.description;
    }

    
    public Integer getEntityType() {
        return this.entityType;
    }

    
    public Long getEntityId() {
        return this.entityId;
    }
    
    public void setPaymentDetailId(final Long paymentDetailId) {
    	this.paymentDetailId = paymentDetailId ;
    }

}