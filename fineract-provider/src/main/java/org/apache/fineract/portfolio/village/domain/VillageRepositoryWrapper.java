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
package org.apache.fineract.portfolio.village.domain;

import org.apache.fineract.portfolio.village.exception.VillageNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VillageRepositoryWrapper {

    private final VillageRepository repository;

    @Autowired
    public VillageRepositoryWrapper(VillageRepository repository) {
        this.repository = repository;
    }
    
    public Village findOneWithNotFoundDetection(final Long id) {
         final Village entity = this.repository.findOne(id);
        if (entity == null) {
            throw new VillageNotFoundException(id);
        }
        return entity;
    }
    
    
    public Village findOneWithNotFoundDetectionAndLazyInitialize(final Long id) {
        final Village village = this.repository.findOne(id);
        if (village == null) { throw new VillageNotFoundException(id); }
        Hibernate.initialize(village.getVillageStaffHistory());
        return village;
    }
    
    public void saveAndFlush(final Village entity) {
        this.repository.saveAndFlush(entity);
    }
    
    public void save(final Village entity) {
        this.repository.save(entity);
    }
    
    public void delete(final Village village) {
        this.repository.delete(village);
    }
}
