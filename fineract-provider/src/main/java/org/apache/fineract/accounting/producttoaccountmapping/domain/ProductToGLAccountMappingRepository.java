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
package org.apache.fineract.accounting.producttoaccountmapping.domain;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface ProductToGLAccountMappingRepository
        extends JpaRepository<ProductToGLAccountMapping, Long>, JpaSpecificationExecutor<ProductToGLAccountMapping> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    ProductToGLAccountMapping findByProductIdAndProductTypeAndFinancialAccountTypeAndPaymentTypeId(Long productId, int productType,
            int financialAccountType, Long paymentType);

    ProductToGLAccountMapping findByProductIdAndProductTypeAndFinancialAccountTypeAndCodeValueId(Long productId, int productType,
            int financialAccountType, Long codeValue);

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    ProductToGLAccountMapping findByProductIdAndProductTypeAndFinancialAccountTypeAndChargeId(Long productId, int productType,
            int financialAccountType, Long chargeId);

    @Query("from ProductToGLAccountMapping mapping where mapping.productId =:productId and mapping.productType =:productType and mapping.financialAccountType=:financialAccountType and mapping.paymentType is NULL and mapping.charge is NULL and mapping.codeValue is NULL")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    ProductToGLAccountMapping findCoreProductToFinAccountMapping(@Param("productId") Long productId, @Param("productType") int productType,
            @Param("financialAccountType") int financialAccountType);

    /***
     * The financial Account Type for a loss write off will always be an expense
     * (1)
     ***/
    @Query("from ProductToGLAccountMapping mapping where mapping.productId =:productId and mapping.productType =:productType and mapping.financialAccountType=6 and mapping.codeValue is NOT NULL ")
    List<ProductToGLAccountMapping> findAllCodeValueToExpenseMappings(@Param("productId") Long productId,
            @Param("productType") int productType);

    /***
     * The financial Account Type for a fund source will always be an asset (1)
     ***/
    @Query("from ProductToGLAccountMapping mapping where mapping.productId =:productId and mapping.productType =:productType and mapping.financialAccountType=1 and mapping.paymentType is not NULL")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<ProductToGLAccountMapping> findAllPaymentTypeToFundSourceMappings(@Param("productId") Long productId,
            @Param("productType") int productType);

    /***
     * The financial Account Type for income from interest will always be 4
     ***/
    @Query("from ProductToGLAccountMapping mapping where mapping.productId =:productId and mapping.productType =:productType and mapping.financialAccountType=4 and mapping.charge is not NULL")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<ProductToGLAccountMapping> findAllFeeToIncomeAccountMappings(@Param("productId") Long productId,
            @Param("productType") int productType);

    /***
     * The financial Account Type for income from interest will always be 5
     ***/
    @Query("from ProductToGLAccountMapping mapping where mapping.productId =:productId and mapping.productType =:productType and mapping.financialAccountType=5 and mapping.charge is not NULL")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<ProductToGLAccountMapping> findAllPenaltyToIncomeAccountMappings(@Param("productId") Long productId,
            @Param("productType") int productType);

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<ProductToGLAccountMapping> findByProductIdAndProductType(Long productId, int productType);

    @Query("from ProductToGLAccountMapping mapping where mapping.productId =:productId and mapping.productType =:productType and mapping.financialAccountType in (:accountTypes) and mapping.charge is NULL")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<ProductToGLAccountMapping> findByProductIdProductTypeAndFinancialAccountType(@Param("productId") Long productId,
            @Param("productType") int productType, @Param("accountTypes") List accountTypes);

}
