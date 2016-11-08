package org.apache.fineract.infrastructure.dataqueries.data;

import java.util.List;

public class ScopeOptionsData {

    public List<ScopeCriteriaData> loan;
    public List<ScopeCriteriaData> savings;
    public List<ScopeCriteriaData> client;

    public static ScopeOptionsData createNew(final List<ScopeCriteriaData> loan, final List<ScopeCriteriaData> savings,
            final List<ScopeCriteriaData> client) {
        return new ScopeOptionsData(loan, savings, client);
    }

    public ScopeOptionsData(final List<ScopeCriteriaData> loan, final List<ScopeCriteriaData> savings, final List<ScopeCriteriaData> client) {
        this.loan = loan;
        this.savings = savings;
        this.client = client;
    }
}
