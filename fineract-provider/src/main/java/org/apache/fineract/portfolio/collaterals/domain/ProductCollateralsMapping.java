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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.collaterals.api.CollateralsApiConstants;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_product_to_collateral_mappings", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "product_id", "collateral_id" }, name = "unique_product_collateral_mapping")})
public class ProductCollateralsMapping  extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct product;

    @OneToOne
    @JoinColumn(name = "collateral_id", nullable = false)
    private Collaterals collateral;

    protected ProductCollateralsMapping() {
        //
    }

    public static ProductCollateralsMapping createProductCollateralsMapping(final LoanProduct product, final Collaterals collateral) {
        return new ProductCollateralsMapping(product, collateral);

    }

    private ProductCollateralsMapping(final LoanProduct product, final Collaterals collateral) {
        this.collateral = collateral;
        this.product = product;
    }

    public Map<String, Object> update(JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);
        if (command.isChangeInLongParameterNamed(CollateralsApiConstants.productIdParamName, productId())) {
            final Long newProduct = command.longValueOfParameterNamed(CollateralsApiConstants.productIdParamName);
            actualChanges.put(CollateralsApiConstants.productIdParamName, newProduct);
        }

        if (command.isChangeInLongParameterNamed(CollateralsApiConstants.collateralIdParamName, collateralId())) {
            final Long newCollateral = command.longValueOfParameterNamed(CollateralsApiConstants.collateralIdParamName);
            actualChanges.put(CollateralsApiConstants.collateralIdParamName, newCollateral);
        }

        return actualChanges;
    }

    private Long productId() {
        Long productId = null;
        if (this.product != null) {
            productId = this.product.getId();
        }
        return productId;
    }
    
    private Long collateralId() {
        Long collateralId = null;
        if (this.collateral != null) {
            collateralId = this.collateral.getId();
        }
        return collateralId;
    }

    
    public void setProduct(LoanProduct product) {
        this.product = product;
    }

    
    public void setCollateral(Collaterals collateral) {
        this.collateral = collateral;
    }
    
    

}
