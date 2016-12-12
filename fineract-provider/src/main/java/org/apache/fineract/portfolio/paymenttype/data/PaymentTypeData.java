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
package org.apache.fineract.portfolio.paymenttype.data;

import java.util.Collection;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.portfolio.external.data.ExternalServicesData;

public class PaymentTypeData {

    private Long id;
    private String name;
    private String description;
    private Boolean isCashPayment;
    private Long position;

    private Long externalServiceId;
    private BankAccountDetailData bankAccountDetails;

    private Collection<ExternalServicesData> externalServiceOptions;

    public PaymentTypeData(final Long id, final String name, final String description, final Boolean isCashPayment, final Long position,
            final Long externalServiceId, final BankAccountDetailData bankAccountDetails, final Collection<ExternalServicesData> externalServiceOptions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isCashPayment = isCashPayment;
        this.position = position;
        this.externalServiceId = externalServiceId;
        this.bankAccountDetails = bankAccountDetails;
        this.externalServiceOptions = externalServiceOptions;
    }

    public static PaymentTypeData instance(final Long id, final String name, final String description, final Boolean isCashPayment,
            final Long position) {
        Long externalServiceId = null;
        BankAccountDetailData bankAccountDetails = null;
        final Collection<ExternalServicesData> externalServiceOptions = null;
        return new PaymentTypeData(id, name, description, isCashPayment, position, externalServiceId, bankAccountDetails,
                externalServiceOptions);
    }

    public static PaymentTypeData instance(final Long id, final String name, final String description, final Boolean isCashPayment,
            final Long position, final Long externalServiceId) {

        BankAccountDetailData bankAccountDetails = null;
        final Collection<ExternalServicesData> externalServiceOptions = null;
        return new PaymentTypeData(id, name, description, isCashPayment, position, externalServiceId, bankAccountDetails,
                externalServiceOptions);
    }

    public static PaymentTypeData instance(final PaymentTypeData paymentTypeData, final BankAccountDetailData bankAccountDetails) {
        return new PaymentTypeData(paymentTypeData.id, paymentTypeData.name, paymentTypeData.description, paymentTypeData.isCashPayment,
                paymentTypeData.position, paymentTypeData.externalServiceId, bankAccountDetails, paymentTypeData.externalServiceOptions);
    }

    public static PaymentTypeData instance(final Long id, final String name) {
        String description = null;
        Boolean isCashPayment = null;
        Long position = null;
        Long externalServiceId = null;
        BankAccountDetailData bankAccountDetails = null;
        final Collection<ExternalServicesData> externalServiceOptions = null;
        return new PaymentTypeData(id, name, description, isCashPayment, position, externalServiceId, bankAccountDetails,
                externalServiceOptions);
    }

    public static PaymentTypeData template(final Collection<ExternalServicesData> externalServiceOptions) {
        final Long id = null;
        final String name = null;
        String description = null;
        Boolean isCashPayment = null;
        Long position = null;
        Long externalServiceId = null;
        BankAccountDetailData bankAccountDetails = null;
        return new PaymentTypeData(id, name, description, isCashPayment, position, externalServiceId, bankAccountDetails,
                externalServiceOptions);
    }

    public static PaymentTypeData withTemplate(final PaymentTypeData paymentTypeData,
            final Collection<ExternalServicesData> externalServiceOptions) {
        return new PaymentTypeData(paymentTypeData.id, paymentTypeData.name, paymentTypeData.description, paymentTypeData.isCashPayment,
                paymentTypeData.position, paymentTypeData.externalServiceId, paymentTypeData.bankAccountDetails,
                externalServiceOptions);
    }

    public Long getId() {
        return this.id;
    }

    
    public Long getExternalServiceId() {
        return this.externalServiceId;
    }

    public static PaymentTypeData lookUp(Long paymentTypeId, String paymentTypeName) {
        String description = null;
        Boolean isCashPayment = null;
        Long position = null;
        Long externalServiceId = null;
        BankAccountDetailData bankAccountDetails = null;
        final Collection<ExternalServicesData> externalServiceOptions = null;
        return new PaymentTypeData(paymentTypeId, paymentTypeName, description, isCashPayment, position, externalServiceId,
                bankAccountDetails, externalServiceOptions);
    }
}
