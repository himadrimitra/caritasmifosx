package org.apache.fineract.portfolio.charge.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ChargeSlabRepository extends JpaRepository<ChargeSlab, Long>, JpaSpecificationExecutor<ChargeSlab> {
    
    public static final String FIND_CHARGE_SUB_SLAB_BY_SLAB_ID = "from ChargeSlab slabCharge where "
            + "slabCharge.parent.id = :parentId";

    @Query(FIND_CHARGE_SUB_SLAB_BY_SLAB_ID)
    List<ChargeSlab> getSubSlabsBySlabId(@Param("parentId") Long parentId);
}
