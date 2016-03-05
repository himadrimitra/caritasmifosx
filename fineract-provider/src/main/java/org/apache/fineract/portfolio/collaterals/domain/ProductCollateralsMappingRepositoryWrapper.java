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
import org.apache.fineract.portfolio.collaterals.exception.ProductCollateralsMappingNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductCollateralsMappingRepositoryWrapper{
    @SuppressWarnings("unused")
    private final PlatformSecurityContext context;
    private final ProductCollateralsMappingRepository productCollateralsMappingRepository;
    
    @Autowired
    private ProductCollateralsMappingRepositoryWrapper(final PlatformSecurityContext context,
            final ProductCollateralsMappingRepository productCollateralsMappingRepository) {
        this.context = context;
        this.productCollateralsMappingRepository = productCollateralsMappingRepository;
    }
    
    public void save(final ProductCollateralsMapping productCollateralsMapping) {
        this.productCollateralsMappingRepository.save(productCollateralsMapping);
    }

    public void saveAndFlush(final ProductCollateralsMapping productCollateralsMapping) {
        this.productCollateralsMappingRepository.saveAndFlush(productCollateralsMapping);
    }

    public void delete(final ProductCollateralsMapping productCollateralsMapping) {
        this.productCollateralsMappingRepository.delete(productCollateralsMapping);
    }
    
    public ProductCollateralsMapping findOneWithNotFoundDetection(final Long id) {
        final ProductCollateralsMapping productCollateralsMapping = this.productCollateralsMappingRepository.findOne(id);
        if (productCollateralsMapping == null) { throw new ProductCollateralsMappingNotFoundException(id); }
        return productCollateralsMapping;
    }

}
