package com.finflux.integrationtests.client.builder;

import java.util.HashMap;

import org.apache.fineract.integrationtests.common.Utils;

import com.google.gson.Gson;

public class ClientTestBuilder {

    private Integer officeId = 1;
    private String firstname = Utils.randomNameGenerator("Client_FirstName_", 5);
    private String lastname = Utils.randomNameGenerator("Client_LastName_", 4);
    private String externalId = randomIDGenerator("ID_", 7);
    private String active = null;
    private String activationDate = null;
    private Integer legalFormId = null;
    private Integer clientTypeId = null;
    private Integer clientClassificationId = null;

    public String locale = "en";
    public String dateFormat = "dd MMMM yyyy";

    public String build() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("officeId", this.officeId);
        map.put("firstname", this.firstname);
        map.put("lastname", this.lastname);
        map.put("externalId", this.externalId);
        if (this.active != null) {
            map.put("active", this.active);
        }
        if (this.activationDate != null) {
            map.put("activationDate", this.activationDate);
        }
        if (this.legalFormId != null) {
            map.put("legalFormId", this.legalFormId);
        }
        if (this.clientTypeId != null) {
            map.put("clientTypeId", this.clientTypeId);
        }
        if (this.clientClassificationId != null) {
            map.put("clientClassificationId", this.clientClassificationId);
        }
        map.put("locale", this.locale);
        map.put("dateFormat", this.dateFormat);

        return new Gson().toJson(map);
    }

    private static String randomIDGenerator(final String prefix, final int lenOfRandomSuffix) {
        return Utils.randomStringGenerator(prefix, lenOfRandomSuffix, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    public ClientTestBuilder withActivateOn(final String activationDate) {
        this.active = "true";
        this.activationDate = activationDate;
        return this;
    }

    public ClientTestBuilder withLegalForm(final Integer legalFormId) {
        this.legalFormId = legalFormId;
        return this;
    }

    public ClientTestBuilder withClientType(final Integer clientTypeId) {
        this.clientTypeId = clientTypeId;
        return this;
    }

    public ClientTestBuilder withClientClassification(final Integer clientClassificationId) {
        this.clientClassificationId = clientClassificationId;
        return this;
    }

    public ClientTestBuilder withOffice(final Integer officeId) {
        this.officeId = officeId;
        return this;
    }

}