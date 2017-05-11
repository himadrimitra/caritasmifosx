package com.finflux.vouchers.data;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.fineract.accounting.journalentry.data.JournalEntryData;
import org.apache.fineract.accounting.journalentry.data.JournalEntryDetailData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class VoucherData {

    private final Long voucherId;

    private final String voucherNumber;

    private final EnumOptionData voucherType;

    private final JournalEntryData journalEntryData;

    private final VoucherTemplateData templateData;

    private final BigDecimal voucherAmount;

    private VoucherData(Long voucherId, String voucherNumber, EnumOptionData voucherType, JournalEntryData journalEntryData,
            final VoucherTemplateData templateData, final BigDecimal voucherAmount) {
        super();
        this.voucherId = voucherId;
        this.voucherNumber = voucherNumber;
        this.voucherType = voucherType;
        this.journalEntryData = journalEntryData;
        this.templateData = templateData;
        this.voucherAmount = voucherAmount;
    }

    public static VoucherData instance(Long voucherId, String voucherNumber, EnumOptionData voucherType, JournalEntryData journalEntryData,
            final BigDecimal voucherAmount) {
        final VoucherTemplateData templateData = null;
        return new VoucherData(voucherId, voucherNumber, voucherType, journalEntryData, templateData, voucherAmount);
    }

    public static VoucherData template(final VoucherTemplateData templateData) {
        final Long voucherId = null;
        final String voucherNumber = null;
        final EnumOptionData voucherType = null;
        final JournalEntryData journalEntryData = null;
        final BigDecimal voucherAmount = null;
        return new VoucherData(voucherId, voucherNumber, voucherType, journalEntryData, templateData, voucherAmount);
    }

    public Long getVoucherId() {
        return this.voucherId;
    }

    public String getVoucherNumber() {
        return this.voucherNumber;
    }

    public EnumOptionData getVoucherType() {
        return this.voucherType;
    }

    public JournalEntryData getJournalEntryData() {
        return this.journalEntryData;
    }

    public VoucherTemplateData getTemplateData() {
        return this.templateData;
    }

    public void setJournalEntryDetails(final Collection<JournalEntryDetailData> journalEntryDetails) {
        this.journalEntryData.setJournalEntryDetails(journalEntryDetails);
    }

    public BigDecimal getVoucherAmount() {
        return this.voucherAmount;
    }
}