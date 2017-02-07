package org.apache.fineract.portfolio.deduplication.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeduplicationWeightageRepository extends JpaRepository<DeduplicationWeightage, Long>, JpaSpecificationExecutor<DeduplicationWeightage> {

        @Query("select d from DeduplicationWeightage d where d.legalForm = :legalForm")
        DeduplicationWeightage findByLegalForm(@Param("legalForm") Integer legalForm);
}
