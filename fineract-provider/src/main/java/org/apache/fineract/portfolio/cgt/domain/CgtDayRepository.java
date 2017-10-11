package org.apache.fineract.portfolio.cgt.domain;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.portfolio.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CgtDayRepository extends JpaRepository<CgtDay, Long>, JpaSpecificationExecutor<CgtDay>{

	public static final String FIND_NEW_CGTS_FOR_CGT_ID = "from CgtDay cgtDay where "
            + "cgtDay.cgtDayStatus = :newCgtDay AND cgtDay.cgt = :cgtId";
	
	Collection<CgtDay> findByCgtId(Long parentId);
	
	@Query(FIND_NEW_CGTS_FOR_CGT_ID)
	Collection<CgtDay> findNewCgtDaysForCgtId(@Param("newCgtDay") Integer newCgtDay, @Param("cgtId") Cgt cgtId);
	
}
