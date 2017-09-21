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
package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargePaymentMode;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.exception.LoanChargeCannotBeAddedException;
import org.apache.fineract.portfolio.charge.exception.LoanChargeNotFoundException;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanChargeRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class LoanChargeAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final ChargeRepositoryWrapper chargeRepository;
    private final LoanChargeRepository loanChargeRepository;

    @Autowired
    public LoanChargeAssembler(final FromJsonHelper fromApiJsonHelper, final ChargeRepositoryWrapper chargeRepository,
            final LoanChargeRepository loanChargeRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.chargeRepository = chargeRepository;
        this.loanChargeRepository = loanChargeRepository;
    }

    public Set<LoanCharge> fromParsedJson(final JsonElement element) {

        final Set<LoanCharge> loanCharges = new HashSet<>();

        final BigDecimal principal = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("principal", element);
        final Integer numberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("numberOfRepayments", element);

        if (element.isJsonObject()) {
            final JsonObject topLevelJsonElement = element.getAsJsonObject();
            final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(topLevelJsonElement);
            final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
            if (topLevelJsonElement.has("charges") && topLevelJsonElement.get("charges").isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get("charges").getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {

                    final JsonObject loanChargeElement = array.get(i).getAsJsonObject();

                    final Long id = this.fromApiJsonHelper.extractLongNamed("id", loanChargeElement);
                    final Long chargeId = this.fromApiJsonHelper.extractLongNamed("chargeId", loanChargeElement);
                    final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed("amount", loanChargeElement, locale);
                    final Integer chargeTimeType = this.fromApiJsonHelper.extractIntegerNamed("chargeTimeType", loanChargeElement, locale);
                    final Integer chargeCalculationType = this.fromApiJsonHelper.extractIntegerNamed("chargeCalculationType",
                            loanChargeElement, locale);
                    final LocalDate dueDate = this.fromApiJsonHelper
                            .extractLocalDateNamed("dueDate", loanChargeElement, dateFormat, locale);
                    final Integer chargePaymentMode = this.fromApiJsonHelper.extractIntegerNamed("chargePaymentMode", loanChargeElement,
                            locale);
                    if (id == null) {
                        final Charge chargeDefinition = this.chargeRepository.findOneWithNotFoundDetection(chargeId);

                        if (chargeDefinition.isOverdueInstallment()) {

                            final String defaultUserMessage = "Installment charge cannot be added to the loan.";
                            throw new LoanChargeCannotBeAddedException("loanCharge", "overdue.charge", defaultUserMessage, null,
                                    chargeDefinition.getName());
                        }

                        ChargeTimeType chargeTime = null;
                        if (chargeTimeType != null) {
                            chargeTime = ChargeTimeType.fromInt(chargeTimeType);
                        }
                        ChargeCalculationType chargeCalculation = null;
                        if (chargeCalculationType != null) {
                            chargeCalculation = ChargeCalculationType.fromInt(chargeCalculationType);
                        }
                        ChargePaymentMode chargePaymentModeEnum = null;
                        if (chargePaymentMode != null) {
                            chargePaymentModeEnum = ChargePaymentMode.fromInt(chargePaymentMode);
                        }
                        final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, principal, amount, chargeTime,
                                chargeCalculation, dueDate, chargePaymentModeEnum, numberOfRepayments);
                        loanCharges.add(loanCharge);
                    } else {
                        final Long loanChargeId = id;
                        final LoanCharge loanCharge = this.loanChargeRepository.findOne(loanChargeId);
                        if (loanCharge == null) { throw new LoanChargeNotFoundException(loanChargeId); }

                        loanCharge.update(amount, dueDate, numberOfRepayments);

                        loanCharges.add(loanCharge);
                    }
                }
            }
        }

        return loanCharges;
    }
}