package com.finflux.kyc.address.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_address_entity", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "address_type", "entity_id", "entity_type_enum" }, name = "f_entity_address_UNIQUE") })
public class AddressEntity extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ManyToOne
    @JoinColumn(name = "address_type", nullable = true)
    private CodeValue addressType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "entity_type_enum", nullable = false)
    private Integer entityTypeEnum;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "parent_address_type", nullable = true)
    private CodeValue parentAddressType;

    protected AddressEntity() {}

    private AddressEntity(final Address address, final CodeValue addressType, final Long entityId, final Integer entityTypeEnum,
            final boolean isActive, final CodeValue parentAddressType) {
        this.address = address;
        this.addressType = addressType;
        this.entityId = entityId;
        this.entityTypeEnum = entityTypeEnum;
        this.isActive = isActive;
        this.parentAddressType = parentAddressType;
    }

    public static AddressEntity create(final Address address, final CodeValue addressType, final Long entityId,
            final Integer entityTypeEnum, final CodeValue parentAddressType) {
        final boolean isActive = true;
        return new AddressEntity(address, addressType, entityId, entityTypeEnum, isActive, parentAddressType);
    }
    
    public boolean isActive(){
        return this.isActive;
    }
    
    public Address getAddress(){
        return this.address;
    }

    public void assignAddressAndMakeItActive(final Address address, final CodeValue parentAddressType) {
        this.address = address;
        this.isActive = true;
        this.parentAddressType = parentAddressType;
    }
    
    public Integer getEntityTypeEnum() {
        return this.entityTypeEnum;
    }

    public void makeInActive() {
        this.isActive = false;
    }
    
    public CodeValue getParentAddressType() {
        return this.parentAddressType;
    }
}
