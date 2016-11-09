package org.apache.fineract.infrastructure.dataqueries.data;

public class AllowedValueOptions {

    public Long id;
    public String name;

    public AllowedValueOptions(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static AllowedValueOptions createNew(final Long id, final String name) {
        return new AllowedValueOptions(id, name);
    }
}
