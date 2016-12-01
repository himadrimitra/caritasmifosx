package org.apache.fineract.portfolio.cgt.domain;

public enum CgtTypes {

    CENTER(1, "cgtTypes.center"), //
    GROUP(2, "cgtTypes.group"); //

    private final Integer value;
    private final String code;

    private CgtTypes(final Integer value, final String code) {
        this.code = code;
        this.value = value;
    }

    public static CgtTypes fromInt(final Integer cgtTypes) {

    	CgtTypes enumeration = CgtTypes.CENTER;
        switch (cgtTypes) {
            case 1:
                enumeration = CgtTypes.GROUP;
            break;
        }
        return enumeration;
    }

    public String getCode() {
        return this.code;
    }

    public Integer getValue() {
        return this.value;
    }
    
}
