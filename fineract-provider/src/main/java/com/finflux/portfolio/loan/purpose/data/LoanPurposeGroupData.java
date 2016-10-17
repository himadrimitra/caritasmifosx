package com.finflux.portfolio.loan.purpose.data;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;

public class LoanPurposeGroupData {

    private final Long id;
    private final String name;
    private final String shortName;
    private final String description;
    private final CodeValueData loanPurposeGroupType;
    private final Boolean isActive;
    private Collection<LoanPurposeData> loanPurposeDatas;

    private LoanPurposeGroupData(final Long id, final String name, final String shortName, final String description,
            final CodeValueData loanPurposeGroupType, final Boolean isActive) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.loanPurposeGroupType = loanPurposeGroupType;
        this.isActive = isActive;
    }

    public static LoanPurposeGroupData instance(final Long id, final String name, final String shortName, final String description,
            final CodeValueData loanPurposeGroupType, final Boolean isActive) {
        return new LoanPurposeGroupData(id, name, shortName, description, loanPurposeGroupType, isActive);
    }

    @SuppressWarnings("unused")
    public void addLoanPurposeData(final LoanPurposeData loanPurposeData) {
        if (this.loanPurposeDatas == null) {
            this.loanPurposeDatas = new ArrayList<LoanPurposeData>();
        }
        this.loanPurposeDatas.add(loanPurposeData);
    }
}
