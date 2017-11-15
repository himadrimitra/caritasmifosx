/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.hooks.domain;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HookConfigurationRepository extends JpaRepository<HookConfiguration, Long>, JpaSpecificationExecutor<HookConfiguration> {

    @Query("select config.fieldValue from HookConfiguration config where config.hook.id = :hookId and config.fieldName = :fieldName")
    String findOneByHookIdAndFieldName(@Param("hookId") Long hookId, @Param("fieldName") String fieldName);

    @Query(" from HookConfiguration config where config.hook.id in "
            + "(select hr.hook from HookResource hr where hr.entityName='SCHEDULER' and hr.actionName='EXECUTEJOB')")
    ArrayList<HookConfiguration> retriveDetail();

    @Query("select config.fieldName,config.fieldValue from HookConfiguration config where config.hook.id in "
            + "(select hr.hook from HookResource hr where hr.entityName='SCHEDULER' and hr.actionName='EXECUTEJOB')")
    HookConfiguration retriveDetail1();

}