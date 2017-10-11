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

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.collaterals.exception.QualityStandardsNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Wrapper for {@link QualityStandardsRepository} that adds NULL checking and Error
 * handling capabilities
 * </p>
 */

@Service
public class QualityStandardsRepositoryWrapper {
	
    private final QualityStandardsRepository repository;
    @SuppressWarnings("unused")
    private final PlatformSecurityContext context;
    
    @Autowired
    public QualityStandardsRepositoryWrapper(final QualityStandardsRepository repository, final PlatformSecurityContext context) {
        this.repository = repository;
        this.context = context;
    }

    public void save(final QualityStandards qualityStandards) {
        this.repository.save(qualityStandards);
    }

    public void saveAndFlush(final QualityStandards qualityStandards) {
        this.repository.saveAndFlush(qualityStandards);
    }

    public void delete(final QualityStandards qualityStandards) {
        this.repository.delete(qualityStandards);
    }
    
    public QualityStandards findOneWithNotFoundDetection(final Long id) {
        final QualityStandards qualityStandards = this.repository.findOne(id);
        if (qualityStandards == null) { throw new QualityStandardsNotFoundException(id); }
        return qualityStandards;
    }
}
