package com.finflux.kyc.address.data;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class AddressEntityData {

    private final Long id;
    private final Long addressId;
    private final CodeValueData addressType;
    private final Long entityId;
    private final EnumOptionData entityType;
    private final boolean isActive;
    private final CodeValueData parentAddressType;

    private AddressEntityData(final Long id, final Long addressId, final CodeValueData addressType, final Long entityId,
            final EnumOptionData entityType, final boolean isActive, final CodeValueData parentAddressType) {
        this.id = id;
        this.addressId = addressId;
        this.addressType = addressType;
        this.entityId = entityId;
        this.entityType = entityType;
        this.isActive = isActive;
        this.parentAddressType = parentAddressType;
    }

    public static AddressEntityData instance(final Long id, final Long addressId, final CodeValueData addressType, final Long entityId,
            final EnumOptionData entityType, final boolean isActive, final CodeValueData parentAddressType) {
        return new AddressEntityData(id, addressId, addressType, entityId, entityType, isActive, parentAddressType);
    }

    public Long getId() {
        return this.id;
    }

    public Long getAddressId() {
        return this.addressId;
    }

    public CodeValueData getAddressType() {
        return this.addressType;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public EnumOptionData getEntityType() {
        return this.entityType;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public CodeValueData getParentAddressType() {
        return this.parentAddressType;
    }

}
