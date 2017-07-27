package com.finflux.integrationtests.pdc.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.Utils;

import com.google.gson.Gson;

public class PostDatedChequeDetailBuilder {

    private String locale = "en";
    private String dateFormat = "dd MMMM yyyy";
    private String chequeType = "1";
    private String numberOfPDC = "5";
    private boolean isIncrementChequeNumber = true;
    private String bankName = Utils.randomNameGenerator("SBI", 6);
    private String branchName = Utils.randomNameGenerator("Bangalore", 6);
    private String ifscCode = "SBIN0006538";
    private List<String> chequeNumbers = new ArrayList<>();
    private String chequeNumber = null;
    private List<String> chequeDates = new ArrayList<>();
    private List<String> amounts = new ArrayList<>();
    private String paymentType = null;
    private List<HashMap<String, Object>> pdcDetails = new ArrayList<>();

    public String createBuild() {
        final HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("locale", this.locale);
        map.put("dateFormat", this.dateFormat);
        map.put("pdcDetails", this.pdcDetails);
        return new Gson().toJson(map);
    }

    public PostDatedChequeDetailBuilder withChequeType(final String chequeType) {
        this.chequeType = chequeType;
        return this;
    }

    public PostDatedChequeDetailBuilder withNumberOfPDC(final String numberOfPDC) {
        this.numberOfPDC = numberOfPDC;
        return this;
    }

    public PostDatedChequeDetailBuilder withChequeNumbers(final String chequeNumber) {
        this.chequeNumbers.add(chequeNumber);
        return this;
    }

    public PostDatedChequeDetailBuilder withChequeNumber(final String chequeNumber) {
        this.chequeNumber = chequeNumber;
        return this;
    }

    public PostDatedChequeDetailBuilder withChequeDateAndAmounts(final String date, final String amount) {
        this.chequeDates.add(date);
        this.amounts.add(amount);
        return this;
    }

    public PostDatedChequeDetailBuilder withPaymentType(final String paymentType) {
        this.paymentType = paymentType;
        return this;
    }

    public PostDatedChequeDetailBuilder withPdcDetail() {
        final HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("chequeType", this.chequeType);
        map.put("isIncrementChequeNumber", this.isIncrementChequeNumber);
        map.put("chequeNumbers", this.chequeNumbers.toArray(new String[this.chequeNumbers.size()]));
        map.put("chequeDates", this.chequeDates.toArray(new String[this.chequeDates.size()]));
        map.put("amounts", this.amounts.toArray(new String[this.amounts.size()]));
        map.put("numberOfPDC", this.numberOfPDC);
        map.put("bankName", this.bankName);
        map.put("branchName", this.branchName);
        map.put("ifscCode", this.ifscCode);
        map.put("paymentType", this.paymentType);
        this.pdcDetails.add(map);
        return this;
    }

    public String updateBuild() {
        final HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("locale", this.locale);
        map.put("dateFormat", this.dateFormat);
        if (this.chequeNumber != null) {
            map.put("chequeNumber", this.chequeNumber);
        }
        return new Gson().toJson(map);
    }
}
