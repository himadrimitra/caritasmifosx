package com.finflux.risk.creditbureau.provider.cibil.response.data;

import java.util.ArrayList;
import java.util.List;

public class AccountNumberData extends Data {

    private final String ACCOUNT_NO = "01";

    private final List<String> accountNumbersList = new ArrayList<>();

    @Override
    public void setValue(String tagType, String value) {
        if (this.ACCOUNT_NO.equals(tagType)) {
            this.accountNumbersList.add(value);
        }
    }

}
