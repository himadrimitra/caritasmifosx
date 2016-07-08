package com.finflux.loanapplicationreference.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum LoanApplicationReferenceStatus {

    APPLICATION_INVALID(0, "loanApplication.invalid"), //
    APPLICATION_CREATED(100, "loanApplication.created"), //
    APPLICATION_IN_APPROVE_STAGE(200, "loanApplication.in.approve.stage"), //
    APPLICATION_APPROVED(300, "loanApplication.approved"), //
    APPLICATION_ACTIVE(400, "loanApplication.active"), //
    APPLICATION_REJECTED(500, "loanApplication.rejected");

    private final Integer value;
    private final String code;

    public static LoanApplicationReferenceStatus fromInt(final Integer statusValue) {

        LoanApplicationReferenceStatus loanApplicationStatus = LoanApplicationReferenceStatus.APPLICATION_INVALID;
        switch (statusValue) {
            case 100:
                loanApplicationStatus = LoanApplicationReferenceStatus.APPLICATION_CREATED;
            break;
            case 200:
                loanApplicationStatus = LoanApplicationReferenceStatus.APPLICATION_IN_APPROVE_STAGE;
            break;
            case 300:
                loanApplicationStatus = LoanApplicationReferenceStatus.APPLICATION_APPROVED;
            break;
            case 400:
                loanApplicationStatus = LoanApplicationReferenceStatus.APPLICATION_ACTIVE;
            break;
            case 500:
                loanApplicationStatus = LoanApplicationReferenceStatus.APPLICATION_REJECTED;
            break;
        }
        return loanApplicationStatus;
    }

    private LoanApplicationReferenceStatus(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static EnumOptionData loanApplicationReferenceStatus(final int id) {
        return loanApplicationReferenceStatus(LoanApplicationReferenceStatus.fromInt(id));
    }

    public static EnumOptionData loanApplicationReferenceStatus(final LoanApplicationReferenceStatus type) {
        EnumOptionData optionData = null;
        switch (type) {
            case APPLICATION_CREATED:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "APPLICATION_CREATED");
            break;
            case APPLICATION_IN_APPROVE_STAGE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "APPLICATION_IN_APPROVE_STAGE");
            break;
            case APPLICATION_APPROVED:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "APPLICATION_APPROVED");
            break;
            case APPLICATION_ACTIVE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "APPLICATION_ACTIVE");
            break;
            case APPLICATION_REJECTED:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "APPLICATION_REJECTED");
            break;
            default:
            break;
        }
        return optionData;
    }
}
