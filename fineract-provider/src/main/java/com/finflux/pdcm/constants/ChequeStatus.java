package com.finflux.pdcm.constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum ChequeStatus {

    INVALID(0, "chequeStatus.invalid"), //
    PENDING(1, "chequeStatus.pending"), //
    PRESENTED(2, "chequeStatus.presented"), //
    BOUNCED(3, "chequeStatus.bounced"), //
    CLEARED(4, "chequeStatus.cleared"), //
    CANCELLED(5, "chequeStatus.cancelled"), //
    RETURNED(6, "chequeStatus.returned");

    private final Integer value;
    private final String code;

    private ChequeStatus(final Integer value, final String code) {
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
        for (final ChequeStatus type : values()) {
            final EnumOptionData enumOptionData = type(type.getValue());
            if (enumOptionData != null) {
                options.add(enumOptionData);
            }
        }
        return options;
    }

    public static EnumOptionData type(final int id) {
        return type(ChequeStatus.fromInt(id));
    }

    public static ChequeStatus fromInt(final Integer frequency) {
        ChequeStatus type = ChequeStatus.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    type = ChequeStatus.PENDING;
                break;
                case 2:
                    type = ChequeStatus.PRESENTED;
                break;
                case 3:
                    type = ChequeStatus.BOUNCED;
                break;
                case 4:
                    type = ChequeStatus.CLEARED;
                break;
                case 5:
                    type = ChequeStatus.CANCELLED;
                break;
                case 6:
                    type = ChequeStatus.RETURNED;
                break;
            }
        }
        return type;
    }

    public static EnumOptionData type(final ChequeStatus type) {
        EnumOptionData optionData = null;
        switch (type) {
            case PENDING:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(),
                        PostDatedChequeDetailApiConstants.CHEQUE_STATUS_PENDING);
            break;
            case PRESENTED:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(),
                        PostDatedChequeDetailApiConstants.CHEQUE_STATUS_PRESENTED);
            break;
            case BOUNCED:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(),
                        PostDatedChequeDetailApiConstants.CHEQUE_STATUS_CHEQUE_BOUNCED);
            break;
            case CLEARED:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(),
                        PostDatedChequeDetailApiConstants.CHEQUE_STATUS_CLEARED);
            break;
            case CANCELLED:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(),
                        PostDatedChequeDetailApiConstants.CHEQUE_STATUS_CANCELLED);
            break;
            case RETURNED:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(),
                        PostDatedChequeDetailApiConstants.CHEQUE_STATUS_RETURNED);
            break;
            default:
            break;
        }
        return optionData;
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final ChequeStatus type : values()) {
            values.add(type.getValue());
        }
        return values.toArray();
    }

    public boolean isPending() {
        return this.value.equals(ChequeStatus.PENDING.getValue());
    }

    public boolean isPresented() {
        return this.value.equals(ChequeStatus.PRESENTED.getValue());
    }

    public boolean isBounced() {
        return this.value.equals(ChequeStatus.BOUNCED.getValue());
    }

    public boolean isCleared() {
        return this.value.equals(ChequeStatus.CLEARED.getValue());
    }

    public boolean isCancelled() {
        return this.value.equals(ChequeStatus.CANCELLED.getValue());
    }

    public boolean isReturned() {
        return this.value.equals(ChequeStatus.RETURNED.getValue());
    }
}
