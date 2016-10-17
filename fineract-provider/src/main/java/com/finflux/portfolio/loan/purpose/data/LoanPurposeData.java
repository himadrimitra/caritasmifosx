package com.finflux.portfolio.loan.purpose.data;

import java.util.ArrayList;
import java.util.Collection;

public class LoanPurposeData {

    private final Long id;
    private final String name;
    private final String shortName;
    private final String description;
    private Collection<LoanPurposeGroupData> loanPurposeGroupDatas;
    private final Boolean isActive;

    private LoanPurposeData(final Long id, final String name, final String shortName, final String description, final Boolean isActive) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.isActive = isActive;
    }

    private LoanPurposeData(final Long id, final String name, final String shortName, final String description,
            final Collection<LoanPurposeGroupData> loanPurposeGroupDatas, final Boolean isActive) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.loanPurposeGroupDatas = loanPurposeGroupDatas;
        this.isActive = isActive;
    }

    public static LoanPurposeData instance(final Long id, final String name, final String shortName, final String description,
            final Boolean isActive) {
        return new LoanPurposeData(id, name, shortName, description, isActive);
    }

    public static LoanPurposeData instance(final Long id, final String name, final String shortName, final String description,
            final Collection<LoanPurposeGroupData> loanPurposeGroupDatas, final Boolean isActive) {
        return new LoanPurposeData(id, name, shortName, description, loanPurposeGroupDatas, isActive);
    }

    public void addLoanPurposeGroupData(final LoanPurposeGroupData loanPurposeGroupData) {
        if(this.loanPurposeGroupDatas == null){
            this.loanPurposeGroupDatas = new ArrayList<LoanPurposeGroupData>();
        }
        this.loanPurposeGroupDatas.add(loanPurposeGroupData);
    }
}