package org.apache.fineract.accounting.journalentry.data;

import java.math.BigDecimal;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class JournalEntryDetailData {

    private final GLAccountData glAccountData;
    private final BigDecimal amount;
    private final EnumOptionData entryType;

    public JournalEntryDetailData(final GLAccountData glAccountData, final BigDecimal amount, final EnumOptionData entryType) {
        this.glAccountData = glAccountData;
        this.amount = amount;
        this.entryType = entryType;
    }

    public static JournalEntryDetailData createWithGlAccountData(final GLAccountData glAccountData) {
        final BigDecimal amount = null;
        final EnumOptionData entryType = null;
        return new JournalEntryDetailData(glAccountData, amount, entryType);
    }

    public GLAccountData getGlAccountData() {
        return this.glAccountData;
    }

}
