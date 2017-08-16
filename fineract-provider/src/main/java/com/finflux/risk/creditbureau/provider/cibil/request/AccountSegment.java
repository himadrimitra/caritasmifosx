package com.finflux.risk.creditbureau.provider.cibil.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Must contain only account number of loans/advances or credit card numbers and
 * not current/savings/deposit account numbers.
 */
public class AccountSegment extends RequestSegment {

    private final static String ACCOUNT_SEGMENT_TAG_NAME = "PI";
    private final static String ACCCOUNT_SEGMENT_TAG_PREFIX = "I";
    private final static String SEGMENT_TAG_LENGTH = "03";
    private List<String> existingAccounts = new ArrayList<>();

    @Override
    public String prepareTuefPacket() {
        StringBuilder builder = new StringBuilder();
        int size = 4; // At max 4 are allowed
        if (existingAccounts.size() < size) size = existingAccounts.size();
        for (int i = 0; i < size; i++) {
            final String accountNumber = existingAccounts.get(i);
            builder.append(ACCOUNT_SEGMENT_TAG_NAME);
            builder.append(SEGMENT_TAG_LENGTH);
            builder.append(ACCCOUNT_SEGMENT_TAG_PREFIX + getFormattedLength((i + 1)));
            builder.append(TAGTYPE_ONE);
            builder.append(getFormattedLength(accountNumber.length()));
            // Only 25 bytes. So validation is required
            builder.append(existingAccounts.get(i));
        }
        return builder.toString();
    }

    public void add(final String accountNumber) {
        if (!StringUtils.isEmpty(accountNumber) && !this.existingAccounts.contains(accountNumber)) {
            this.existingAccounts.add(accountNumber);
        }
    }
}
