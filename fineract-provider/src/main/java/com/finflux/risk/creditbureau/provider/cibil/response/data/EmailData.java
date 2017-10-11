package com.finflux.risk.creditbureau.provider.cibil.response.data;

import java.util.ArrayList;
import java.util.List;

public class EmailData extends Data {

    private final String EMAIL_ID = "01";
    private List<String> emailList = new ArrayList<>();

    @Override
    public void setValue(String tagType, String value) {
        if (this.EMAIL_ID.equals(tagType)) {
            this.emailList.add(value);
        }
    }

}
