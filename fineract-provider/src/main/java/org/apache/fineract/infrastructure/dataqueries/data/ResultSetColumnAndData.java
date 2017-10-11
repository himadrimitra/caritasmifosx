/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.infrastructure.dataqueries.data;

import java.util.List;

public class ResultSetColumnAndData {

    @SuppressWarnings("unused")
    private String columnName;
    @SuppressWarnings("unused")
    private String value;

    @SuppressWarnings("unused")
    private List<ResultSetColumnAndData> row;

    public static ResultSetColumnAndData create(final List<ResultSetColumnAndData> rowValues) {
        return new ResultSetColumnAndData(rowValues);
    }

    private ResultSetColumnAndData(final List<ResultSetColumnAndData> rowValues) {
        this.row = rowValues;
    }

    public ResultSetColumnAndData(final String columnName, final String value) {
        this.columnName = columnName;
        this.value = value;
    }
}
