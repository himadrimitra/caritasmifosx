package com.finflux.pdcm.constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum ChequeType {

    INVALID(0, "chequeType.invalid"), //
    REPAYMENT_PDC(1, "chequeType.repayment.pdc"), //
    SECURITY_PDC(2, "chequeType.security.pdc");

    private final Integer value;
    private final String code;

    private ChequeType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static Collection<EnumOptionData> options() {
        final Collection<EnumOptionData> options = new ArrayList<>();
        for (final ChequeType type : values()) {
            final EnumOptionData enumOptionData = type(type.getValue());
            if (enumOptionData != null) {
                options.add(enumOptionData);
            }
        }
        return options;
    }

    public static EnumOptionData type(final int id) {
        return type(ChequeType.fromInt(id));
    }

    public static ChequeType fromInt(final Integer frequency) {
        ChequeType type = ChequeType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    type = ChequeType.REPAYMENT_PDC;
                break;
                case 2:
                    type = ChequeType.SECURITY_PDC;
                break;
            }
        }
        return type;
    }

    public static EnumOptionData type(final ChequeType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case REPAYMENT_PDC:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(),
                        PostDatedChequeDetailApiConstants.CHEQUE_TYPE_REPAYMENT_PDC);
            break;
            case SECURITY_PDC:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(),
                        PostDatedChequeDetailApiConstants.CHEQUE_TYPE_SECURITY_PDC);
            break;
            default:
            break;
        }
        return optionData;
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final ChequeType type : values()) {
            values.add(type.getValue());
        }
        return values.toArray();
    }

    public boolean isRepaymentPDC() {
        return this.value.equals(ChequeType.REPAYMENT_PDC.getValue());
    }

    public boolean isSecurityPDC() {
        return this.value.equals(ChequeType.SECURITY_PDC.getValue());
    }
}