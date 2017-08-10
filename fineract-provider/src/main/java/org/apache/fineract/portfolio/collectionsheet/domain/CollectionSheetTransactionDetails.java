/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.collectionsheet.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_collection_sheet_transaction_details")
public class CollectionSheetTransactionDetails extends AbstractPersistable<Long> {

    @Column(name = "entity_id", nullable = true)
    private Long entityId;

    @Column(name = "entity_type_enum", nullable = true)
    private Integer entityType;

    @Column(name = "transaction_id", nullable = true)
    private Long transactioId;

    @Column(name = "transaction_status", nullable = true)
    private Boolean transactionStatus;

    @Column(name = "error_message", nullable = true)
    private String errorMessage;

    public CollectionSheetTransactionDetails(Long entityId, Long transactioId, Boolean transactionStatus, String errorMessage,
            Integer entityType) {
        this.entityId = entityId;
        this.transactioId = transactioId;
        this.transactionStatus = transactionStatus;
        this.errorMessage = errorMessage;
        this.entityType = entityType;
    }

    public static CollectionSheetTransactionDetails formCollectionSheetTransactionDetails(Long entityId, Long transactioId,
            Boolean transactionStatus, String errorMessage, Integer entityType) {
        return new CollectionSheetTransactionDetails(entityId, transactioId, transactionStatus, errorMessage, entityType);
    }
}
