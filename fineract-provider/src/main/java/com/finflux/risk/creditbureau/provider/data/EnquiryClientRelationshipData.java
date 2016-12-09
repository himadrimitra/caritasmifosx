package com.finflux.risk.creditbureau.provider.data;

public class EnquiryClientRelationshipData {

    final String relationshipTypeId;
    final String relationshipType;
    final String name;

    public EnquiryClientRelationshipData(final String relationshipTypeId, final String relationshipType, final String name) {
        this.relationshipTypeId = relationshipTypeId;
        this.relationshipType = relationshipType;
        this.name = name;
    }

    public String getRelationshipType() {
        return this.relationshipType;
    }

    public String getName() {
        return this.name;
    }

    public String getRelationshipTypeId() {
        return this.relationshipTypeId;
    }
}