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
package org.apache.fineract.portfolio.investment.domain;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestmentRepositoryWrapper {

    private final InvestmentRepository repository;

    @Autowired
    public InvestmentRepositoryWrapper(InvestmentRepository repository) {
        super();
        this.repository = repository;
    }
    
    public void save(final Investment saving) {
        this.repository.save(saving);
    }

    public void saveAndFlush(final Investment saving) {
        this.repository.saveAndFlush(saving);
    }

    public void delete(final Investment saving) {
        this.repository.delete(saving);
    }

   public Investment findWithNotFoundDetection(final Long id){
       final Investment investment = this.repository.findOne(id);
       return investment;
       
   }
}
