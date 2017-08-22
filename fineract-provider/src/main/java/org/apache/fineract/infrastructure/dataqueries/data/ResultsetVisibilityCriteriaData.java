package org.apache.fineract.infrastructure.dataqueries.data;

import java.util.List;

@SuppressWarnings("unused")
public class ResultsetVisibilityCriteriaData {

    private final String columnName;
    private final List<ResultsetColumnValueData> columnValue;
    private final List<ResultsetColumnValueData> columnValuesAvailable;

    public ResultsetVisibilityCriteriaData(String columnName, List<ResultsetColumnValueData> columnValue,
            List<ResultsetColumnValueData> columnValuesAvailable) {
        this.columnName = columnName;
        this.columnValue = columnValue;
        this.columnValuesAvailable = columnValuesAvailable;
    }

}
