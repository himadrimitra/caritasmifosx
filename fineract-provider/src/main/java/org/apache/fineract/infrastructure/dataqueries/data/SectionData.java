/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.infrastructure.dataqueries.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SectionData {

    private Long id;
    @SuppressWarnings("unused")
    private String displayName;
    @SuppressWarnings("unused")
    private Integer displayPosition;
    private List<ResultsetColumnHeaderData> columns = new ArrayList<>();

    public static SectionData instance(final Long id, final String displayName, final Integer displayPosition) {
        return new SectionData(id, displayName, displayPosition);

    }

    private SectionData(final Long id, final String displayName, final Integer displayPosition) {

        this.id = id;
        this.displayName = displayName;
        this.displayPosition = displayPosition;

    }

    public static List<SectionData> organizeList(List<ResultsetColumnHeaderData> columnList, List<SectionData> sectionList) {

        if (columnList != null && columnList.size() > 0) {
            for (SectionData section : sectionList) {
                for (ResultsetColumnHeaderData column : columnList) {
                    if (column.getSectionId() != null && section.getId().equals(column.getSectionId())) {
                        section.columns.add(column);
                    }
                }
            }
        }
        return sectionList;
    }

    public Long getId() {
        return this.id;
    }
}
