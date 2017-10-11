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

import org.apache.fineract.portfolio.collaterals.exception.PledgeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PledgeRepositoryWrapper {
    
    private final PledgeRepository pledgeRepository;

    @Autowired
    public PledgeRepositoryWrapper(final PledgeRepository pledgeRepository) {
        this.pledgeRepository = pledgeRepository;
    }
    
    public void save(final Pledges pledge) {
        this.pledgeRepository.save(pledge);
    }

    public void saveAndFlush(final Pledges pledge) {
        this.pledgeRepository.saveAndFlush(pledge);
    }

    public void delete(final Pledges pledge) {
        this.pledgeRepository.delete(pledge);
    }
    
    public Pledges findOneWithNotFoundDetection(final Long id) {
        final Pledges pledge = this.pledgeRepository.findOne(id);
        if (pledge == null) { throw new PledgeNotFoundException(id); }
        return pledge;
    }
    

}
