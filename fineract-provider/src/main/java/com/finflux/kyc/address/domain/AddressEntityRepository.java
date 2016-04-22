package com.finflux.kyc.address.domain;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AddressEntityRepository extends JpaRepository<AddressEntity, Long>, JpaSpecificationExecutor<AddressEntity> {

    AddressEntity findByAddressTypeAndEntityIdAndEntityTypeEnum(final CodeValue addressType, final Long entityId,
            final Integer entityTypeEnum);

}
