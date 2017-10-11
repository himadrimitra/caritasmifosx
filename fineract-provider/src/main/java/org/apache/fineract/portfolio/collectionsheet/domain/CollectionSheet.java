/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.portfolio.collectionsheet.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_collection_sheet")
public class CollectionSheet extends AbstractPersistable<Long> {

    @Column(name = "office_id", nullable = true)
    private Long officeId;

    @Column(name = "staff_id", nullable = true)
    private Long staffId;

    @Column(name = "group_id", nullable = true)
    private Long groupId;

    @Column(name = "center_id", nullable = true)
    private Long centerId;

    @Column(name = "meeting_date", nullable = true)
    private Date meetingDate;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "collection_sheet_id", referencedColumnName = "id", nullable = false)
    private List<CollectionSheetTransactionDetails> collectionSheetTransactionDetails = new ArrayList<>();

    public CollectionSheet(Long officeId, Long staffId, Long groupId, Long centerId,
            List<CollectionSheetTransactionDetails> collectionSheetTransactionDetails, Date meetingDate) {
        this.centerId = centerId;
        this.groupId = groupId;
        this.officeId = officeId;
        this.officeId = officeId;
        this.staffId = staffId;
        this.collectionSheetTransactionDetails = collectionSheetTransactionDetails;
        this.meetingDate = meetingDate;
    }

    public static CollectionSheet formCollectionSheet(Long officeId, Long staffId, Long groupId, Long centerId,
            List<CollectionSheetTransactionDetails> collectionSheetTransactionDetails, Date meetingDate) {

        return new CollectionSheet(officeId, staffId, groupId, centerId, collectionSheetTransactionDetails, meetingDate);
    }

}
