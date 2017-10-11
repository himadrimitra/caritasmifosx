package com.finflux.pdcm.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PostDatedChequeDetailMappingRepository extends JpaRepository<PostDatedChequeDetailMapping, Long>,
        JpaSpecificationExecutor<PostDatedChequeDetailMapping> {

    List<PostDatedChequeDetailMapping> findByEntityTypeAndEntityId(final Integer entityType, final Long entityId);
}