package org.apache.fineract.infrastructure.dataqueries.data;

import java.util.List;

public class ScopeCriteriaData {

    private Long id;
    private String code;
    private String name;
    private List<AllowedValueOptions> allowedValueOptions;

    public static ScopeCriteriaData createNew(final Long id, final String code, final String name,
            final List<AllowedValueOptions> allowedValueOptions) {
        return new ScopeCriteriaData(id, code, name, allowedValueOptions);
    }

    public ScopeCriteriaData(final Long id, final String code, final String name, final List<AllowedValueOptions> allowedValueOptions) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.allowedValueOptions = allowedValueOptions;
    }

}
