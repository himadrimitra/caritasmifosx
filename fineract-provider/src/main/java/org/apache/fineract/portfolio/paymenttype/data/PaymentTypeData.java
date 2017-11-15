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

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.portfolio.external.data.ExternalServicesData;

public class PaymentTypeData {

    private final Long id;
    private final String name;
    private final String description;
    private final Boolean isCashPayment;
    private final Long position;

    private final Long externalServiceId;
    private final BankAccountDetailData bankAccountDetails;

    private final Collection<ExternalServicesData> externalServiceOptions;

    private final Collection<EnumOptionData> bankAccountTypeOptions;

    public PaymentTypeData(final Long id, final String name, final String description, final Boolean isCashPayment, final Long position,
            final Long externalServiceId, final BankAccountDetailData bankAccountDetails,
            final Collection<ExternalServicesData> externalServiceOptions, final Collection<EnumOptionData> bankAccountTypeOptions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isCashPayment = isCashPayment;
        this.position = position;
        this.externalServiceId = externalServiceId;
        this.bankAccountDetails = bankAccountDetails;
        this.externalServiceOptions = externalServiceOptions;
        this.bankAccountTypeOptions = bankAccountTypeOptions;
    }

    public static PaymentTypeData instance(final Long id, final String name, final String description, final Boolean isCashPayment,
            final Long position) {
        final Long externalServiceId = null;
        final BankAccountDetailData bankAccountDetails = null;
        final Collection<ExternalServicesData> externalServiceOptions = null;
        final Collection<EnumOptionData> bankAccountTypeOptions = null;
        return new PaymentTypeData(id, name, description, isCashPayment, position, externalServiceId, bankAccountDetails,
                externalServiceOptions, bankAccountTypeOptions);
    }

    public static PaymentTypeData instance(final Long id, final String name, final String description, final Boolean isCashPayment,
            final Long position, final Long externalServiceId) {

        final BankAccountDetailData bankAccountDetails = null;
        final Collection<ExternalServicesData> externalServiceOptions = null;
        final Collection<EnumOptionData> bankAccountTypeOptions = null;
        return new PaymentTypeData(id, name, description, isCashPayment, position, externalServiceId, bankAccountDetails,
                externalServiceOptions, bankAccountTypeOptions);
    }

    public static PaymentTypeData instance(final PaymentTypeData paymentTypeData, final BankAccountDetailData bankAccountDetails) {
        final Collection<EnumOptionData> bankAccountTypeOptions = null;
        return new PaymentTypeData(paymentTypeData.id, paymentTypeData.name, paymentTypeData.description, paymentTypeData.isCashPayment,
                paymentTypeData.position, paymentTypeData.externalServiceId, bankAccountDetails, paymentTypeData.externalServiceOptions,
                bankAccountTypeOptions);
    }

    public static PaymentTypeData instance(final Long id, final String name) {
        final String description = null;
        final Boolean isCashPayment = null;
        final Long position = null;
        final Long externalServiceId = null;
        final BankAccountDetailData bankAccountDetails = null;
        final Collection<ExternalServicesData> externalServiceOptions = null;
        final Collection<EnumOptionData> bankAccountTypeOptions = null;
        return new PaymentTypeData(id, name, description, isCashPayment, position, externalServiceId, bankAccountDetails,
                externalServiceOptions, bankAccountTypeOptions);
    }

    public static PaymentTypeData template(final Collection<ExternalServicesData> externalServiceOptions,
            final Collection<EnumOptionData> bankAccountTypeOptions) {
        final Long id = null;
        final String name = null;
        final String description = null;
        final Boolean isCashPayment = null;
        final Long position = null;
        final Long externalServiceId = null;
        final BankAccountDetailData bankAccountDetails = null;
        return new PaymentTypeData(id, name, description, isCashPayment, position, externalServiceId, bankAccountDetails,
                externalServiceOptions, bankAccountTypeOptions);
    }

    public static PaymentTypeData withTemplate(final PaymentTypeData paymentTypeData,
            final Collection<ExternalServicesData> externalServiceOptions, final Collection<EnumOptionData> bankAccountTypeOptions) {
        return new PaymentTypeData(paymentTypeData.id, paymentTypeData.name, paymentTypeData.description, paymentTypeData.isCashPayment,
                paymentTypeData.position, paymentTypeData.externalServiceId, paymentTypeData.bankAccountDetails, externalServiceOptions,
                bankAccountTypeOptions);
    }

    public Long getId() {
        return this.id;
    }

    public Long getExternalServiceId() {
        return this.externalServiceId;
    }

    public static PaymentTypeData lookUp(final Long paymentTypeId, final String paymentTypeName) {
        final String description = null;
        final Boolean isCashPayment = null;
        final Long position = null;
        final Long externalServiceId = null;
        final BankAccountDetailData bankAccountDetails = null;
        final Collection<ExternalServicesData> externalServiceOptions = null;
        final Collection<EnumOptionData> bankAccountTypeOptions = null;
        return new PaymentTypeData(paymentTypeId, paymentTypeName, description, isCashPayment, position, externalServiceId,
                bankAccountDetails, externalServiceOptions, bankAccountTypeOptions);
    }
}