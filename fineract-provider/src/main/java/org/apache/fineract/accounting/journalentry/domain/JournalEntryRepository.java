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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long>, JpaSpecificationExecutor<JournalEntry> {

    @Query("from JournalEntry journalEntry where journalEntry.transactionIdentifier= :transactionIdentifier and journalEntry.reversed is false and journalEntry.manualEntry is true")
    List<JournalEntry> findUnReversedManualJournalEntriesByTransactionId(@Param("transactionIdentifier") String transactionIdentifier);
    
    @Query("from JournalEntry journalEntry where journalEntry.entityId= :entityId and journalEntry.entityType = :entityType")    
    List<JournalEntry> findProvisioningJournalEntriesByEntityId(@Param("entityId") Long entityId, @Param("entityType") Integer entityType) ;

    @Query("from JournalEntry journalEntry where journalEntry.transactionIdentifier= :transactionId and journalEntry.reversed is false and journalEntry.entityType = :entityType")
    JournalEntry findJournalEntries(@Param("transactionId") String transactionId, @Param("entityType") Integer entityType);
    
}
