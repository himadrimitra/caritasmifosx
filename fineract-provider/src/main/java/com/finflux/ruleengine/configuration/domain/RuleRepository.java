package com.finflux.ruleengine.configuration.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

public interface RuleRepository extends JpaRepository<RuleModel, Long>, JpaSpecificationExecutor<RuleModel> {

    RuleModel findOneByIdAndEntityType(@Param("id") Long id, @Param("entityType") Integer entityType);

    RuleModel findOneByUname(@Param("uname") String uname);

}