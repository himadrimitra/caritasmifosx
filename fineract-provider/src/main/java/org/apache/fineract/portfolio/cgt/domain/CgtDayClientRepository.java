package org.apache.fineract.portfolio.cgt.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CgtDayClientRepository  extends JpaRepository<CgtDay, Long>, JpaSpecificationExecutor<CgtDay>{

}
