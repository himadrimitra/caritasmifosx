package org.apache.fineract.portfolio.cgt.domain;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CgtRepository extends JpaRepository<Cgt, Long>, JpaSpecificationExecutor<Cgt> {

    public static final String FIND_ACTIVE_OR_IN_PROGRESS_CGTS = "from Cgt cgt where "
            + " (cgt.cgtStatus = :newCgt or cgt.cgtStatus = :inProgressCgt) and cgt.entityType =:entityId and cgt.entityTypeId =:entityTypeId";

    Collection<Cgt> findByEntityTypeId(final Integer entityId);

    @Query(FIND_ACTIVE_OR_IN_PROGRESS_CGTS)
    List<Cgt> findActiveOrInProgressCgts(@Param("newCgt") Integer newCgt, @Param("inProgressCgt") Integer inProgressCgt, @Param("entityId") Integer entityId, 
            @Param("entityTypeId") Integer entityTypeId);

}
