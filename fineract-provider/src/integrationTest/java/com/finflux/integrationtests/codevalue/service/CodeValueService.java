package com.finflux.integrationtests.codevalue.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.system.CodeHelper;
import org.junit.Assert;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class CodeValueService {

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;

    public CodeValueService(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public List<Integer> retrieveOrCreateCodeValue(final String codeName) {
        final HashMap<String, Object> code = CodeHelper.getCodeByName(this.requestSpec, this.responseSpec, codeName);
        final Integer codeId = (Integer) code.get("id");
        Assert.assertNotNull(codeId);

        final HashMap<String, Object> codeValues = CodeHelper.retrieveOrCreateCodeValue(codeId, this.requestSpec, this.responseSpec);
        Assert.assertNotNull(codeValues);

        final List<Integer> codeValueIds = new ArrayList<>();
        codeValues.forEach((k, v) -> {
            if ("id".equals(k)) {
                codeValueIds.add(new Integer((int) v));
            }
        });
        Assert.assertFalse(codeValueIds.isEmpty());

        return codeValueIds;
    }
}