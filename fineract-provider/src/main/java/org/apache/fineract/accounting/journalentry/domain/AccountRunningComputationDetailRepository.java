package org.apache.fineract.accounting.journalentry.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRunningComputationDetailRepository extends JpaRepository<AccountRunningComputationDetail, Long>,
        JpaSpecificationExecutor<AccountRunningComputationDetail> {
    
    @Query("from AccountRunningComputationDetail computationDetail where computationDetail.officeId= :officeId and computationDetail.glAccountId in (:accountIds) and computationDetail.currencyCode = :currencyCode")
    List<AccountRunningComputationDetail> fetchAccountRunningComputationDetail(@Param("officeId") Long officeId, @Param("accountIds") Long[] accountIds, @Param("currencyCode") String currencyCode);

}
