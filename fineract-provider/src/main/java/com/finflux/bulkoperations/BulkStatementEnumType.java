package com.finflux.bulkoperations;


public enum BulkStatementEnumType {
    INVALID(0, "invalid"),
    BANKTRANSACTIONS(1, "banktransactions"),//
    BULKTRANSACTIONS(2, "bulktransactions"); //

    private final Integer value;
    private final String code;

    private BulkStatementEnumType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
    public static BulkStatementEnumType fromInt(final Integer value) {

        BulkStatementEnumType eenum = BulkStatementEnumType.INVALID;
        switch (value) {
            case 1:
                eenum= BulkStatementEnumType.BANKTRANSACTIONS;
            break;
            case 2:
                eenum = BulkStatementEnumType.BULKTRANSACTIONS;
            break;
            
        }
        return eenum;
    }
}
