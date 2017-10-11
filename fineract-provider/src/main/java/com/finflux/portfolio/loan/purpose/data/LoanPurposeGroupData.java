package com.finflux.portfolio.loan.purpose.data;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;

public class LoanPurposeGroupData {

    private final Long id;
    private final String name;
    private final String systemCode;
    private final String description;
    private final CodeValueData loanPurposeGroupType;
    private final Boolean isActive;
    private final Boolean isSystemDefined;
    private Collection<LoanPurposeData> loanPurposeDatas;

    private LoanPurposeGroupData(final Long id, final String name, final String systemCode, final String description,
            final CodeValueData loanPurposeGroupType, final Boolean isActive, final Boolean isSystemDefined) {
        this.id = id;
        this.name = name;
        this.systemCode = systemCode;
        this.description = description;
        this.loanPurposeGroupType = loanPurposeGroupType;
        this.isActive = isActive;
        this.isSystemDefined = isSystemDefined;
    }

    public static LoanPurposeGroupData instance(final Long id, final String name, final String systemCode, final String description,
            final CodeValueData loanPurposeGroupType, final Boolean isActive, final Boolean isSystemDefined) {
        return new LoanPurposeGroupData(id, name, systemCode, description, loanPurposeGroupType, isActive, isSystemDefined);
    }

    @SuppressWarnings("unused")
    public void addLoanPurposeData(final LoanPurposeData loanPurposeData) {
        if (this.loanPurposeDatas == null) {
            this.loanPurposeDatas = new ArrayList<LoanPurposeData>();
        }
        this.loanPurposeDatas.add(loanPurposeData);
    }
}
