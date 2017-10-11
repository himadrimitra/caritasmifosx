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
package org.apache.fineract.portfolio.collaterals.domain;

import org.apache.fineract.portfolio.collaterals.exception.CollateralNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollateralsRepositoryWrapper {
    
    private final CollateralsRepository collateralsRepository;
    
    @Autowired
    public CollateralsRepositoryWrapper(final CollateralsRepository collateralsRepository) {
        this.collateralsRepository = collateralsRepository;
    }
    
    public void save(final Collaterals collateral) {
        this.collateralsRepository.save(collateral);
    }

    public void saveAndFlush(final Collaterals collateral) {
        this.collateralsRepository.saveAndFlush(collateral);
    }

    public void delete(final Collaterals collateral) {
        this.collateralsRepository.delete(collateral);
    }
    
    public Collaterals findOneWithNotFoundDetection(final Long id) {
        final Collaterals collateral = this.collateralsRepository.findOne(id);
        if (collateral == null) { throw new CollateralNotFoundException(id); }
        return collateral;
    }
}
