package com.finflux.vouchers.constants;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum VoucherType {

    INVALID(0, "invalid"), //
    CASH_PAYMENT(1, "cashpayment"), //
    CASH_RECEIPT(2, "cashreceipt"), //
    BANK_PAYMENT(3, "bankpayment"), //
    BANK_RECEIPT(4, "bankreceipt"), //
    JV_ENTRY(5, "jventry"), //
    CONTRA_ENTRY(6, "contraentry"), //
    INTER_BRANCH_CASH_TRANSFER(7, "interbranchcashtransfer"), //
    INTER_BRANCH_BANK_TRANSFER(8, "interbranchbanktransfer");

    private final Integer value;
    private final String code;

    private VoucherType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static VoucherType fromInt(final Integer voucherTypeId) {

        if (voucherTypeId == null) { return VoucherType.INVALID; }

        VoucherType voucherType = null;
        switch (voucherTypeId) {
            case 1:
                voucherType = VoucherType.CASH_PAYMENT;
            break;
            case 2:
                voucherType = VoucherType.CASH_RECEIPT;
            break;
            case 3:
                voucherType = VoucherType.BANK_PAYMENT;
            break;
            case 4:
                voucherType = VoucherType.BANK_RECEIPT;
            break;
            case 5:
                voucherType = VoucherType.JV_ENTRY;
            break;
            case 6:
                voucherType = VoucherType.CONTRA_ENTRY;
            break;
            case 7:
                voucherType = VoucherType.INTER_BRANCH_CASH_TRANSFER;
            break;
            case 8:
                voucherType = VoucherType.INTER_BRANCH_BANK_TRANSFER;
            break;
            default:
                voucherType = VoucherType.INVALID;
            break;
        }
        return voucherType;
    }

    public static VoucherType fromCode(final String code) {
        if (code == null) { return VoucherType.INVALID; }
        VoucherType voucherType = null;
        switch (code) {
            case "cashpayment":
                voucherType = VoucherType.CASH_PAYMENT;
            break;
            case "cashreceipt":
                voucherType = VoucherType.CASH_RECEIPT;
            break;
            case "bankpayment":
                voucherType = VoucherType.BANK_PAYMENT;
            break;
            case "bankreceipt":
                voucherType = VoucherType.BANK_RECEIPT;
            break;
            case "jventry":
                voucherType = VoucherType.JV_ENTRY;
            break;
            case "contraentry":
                voucherType = VoucherType.CONTRA_ENTRY;
            break;
            case "interbranchcashtransfer":
                voucherType = VoucherType.INTER_BRANCH_CASH_TRANSFER;
            break;
            case "interbranchbanktransfer":
                voucherType = VoucherType.INTER_BRANCH_BANK_TRANSFER;
            break;
            default:
                voucherType = VoucherType.INVALID;
            break;
        }
        return voucherType;
    }

    public static Collection<EnumOptionData> voucherTypeOptions() {
        final Collection<EnumOptionData> voucherTypeOptions = new ArrayList<>();
        for (final VoucherType enumType : values()) {
            final EnumOptionData enumOptionData = voucherType(enumType.getValue());
            if (enumOptionData != null) {
                voucherTypeOptions.add(enumOptionData);
            }
        }
        return voucherTypeOptions;
    }

    public static EnumOptionData voucherType(final int id) {
        return voucherType(VoucherType.fromInt(id));
    }

    public static EnumOptionData voucherType(final VoucherType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case CASH_PAYMENT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), VouchersApiConstants.CASH_PAYMENT);
            break;
            case CASH_RECEIPT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), VouchersApiConstants.CASH_RECEIPT);
            break;
            case BANK_PAYMENT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), VouchersApiConstants.BANK_PAYMMENT);
            break;
            case BANK_RECEIPT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), VouchersApiConstants.BANK_RECEIPT);
            break;
            case CONTRA_ENTRY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), VouchersApiConstants.CONTRA_ENTRY);
            break;
            case JV_ENTRY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), VouchersApiConstants.JV_ENTRY);
            break;
            case INTER_BRANCH_CASH_TRANSFER:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(),
                        VouchersApiConstants.INTER_BRANCH_CASH_TRANSFER);
            break;
            case INTER_BRANCH_BANK_TRANSFER:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(),
                        VouchersApiConstants.INTER_BRANCH_BANK_TRANSFER);
            break;
            default:
            break;
        }
        return optionData;
    }

    public boolean isCashPayment() {
        return this.value.equals(VoucherType.CASH_PAYMENT.getValue());
    }

    public boolean isCashReceipt() {
        return this.value.equals(VoucherType.CASH_RECEIPT.getValue());
    }

    public boolean isBankPayment() {
        return this.value.equals(VoucherType.BANK_PAYMENT.getValue());
    }

    public boolean isBankRecepit() {
        return this.value.equals(VoucherType.BANK_RECEIPT.getValue());
    }

    public boolean isJournalVoucher() {
        return this.value.equals(VoucherType.JV_ENTRY.getValue());
    }

    public boolean isContra() {
        return this.value.equals(VoucherType.CONTRA_ENTRY.getValue());
    }

    public boolean isInterBranchCashTransfer() {
        return this.value.equals(VoucherType.INTER_BRANCH_CASH_TRANSFER.getValue());
    }

    public boolean isInterBranchBankTransfer() {
        return this.value.equals(VoucherType.INTER_BRANCH_BANK_TRANSFER.getValue());
    }
}