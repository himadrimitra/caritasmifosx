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
package org.apache.fineract.portfolio.charge.service;

import static org.apache.fineract.portfolio.charge.service.ChargeEnumerations.chargeCalculationType;
import static org.apache.fineract.portfolio.charge.service.ChargeEnumerations.chargePaymentMode;
import static org.apache.fineract.portfolio.charge.service.ChargeEnumerations.chargeTimeType;
import static org.apache.fineract.portfolio.charge.service.ChargeEnumerations.glimChargeCalculationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.charge.domain.ChargeAppliesTo;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargePaymentMode;
import org.apache.fineract.portfolio.charge.domain.ChargePercentagePeriodType;
import org.apache.fineract.portfolio.charge.domain.ChargePercentageType;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.domain.GlimChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.PenaltyGraceType;
import org.apache.fineract.portfolio.charge.domain.SlabChargeType;
import org.springframework.stereotype.Service;

@Service
public class ChargeDropdownReadPlatformServiceImpl implements ChargeDropdownReadPlatformService {

    @Override
    public List<EnumOptionData> retrieveCalculationTypes() {

        return Arrays.asList(chargeCalculationType(ChargeCalculationType.FLAT),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_AMOUNT),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_AMOUNT_AND_INTEREST),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_INTEREST),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_DISBURSEMENT_AMOUNT),
                chargeCalculationType(ChargeCalculationType.SLAB_BASED),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_AMOUNT_INTEREST_AND_FEES));
    }

    @Override
    public List<EnumOptionData> retrieveApplicableToTypes() {
        final List<EnumOptionData> chargeAppliesToTypes = new ArrayList<>();
        for (final ChargeAppliesTo chargeAppliesTo : ChargeAppliesTo.values()) {
            if (ChargeAppliesTo.INVALID.equals(chargeAppliesTo)) {
                continue;
            }
            chargeAppliesToTypes.add(ChargeEnumerations.chargeAppliesTo(chargeAppliesTo));
        }
        return chargeAppliesToTypes;
    }

    @Override
    public List<EnumOptionData> retrieveCollectionTimeTypes() {
        final List<EnumOptionData> chargeTimeTypes = new ArrayList<>();
        for (final ChargeTimeType chargeTimeType : ChargeTimeType.values()) {
            if (ChargeTimeType.INVALID.equals(chargeTimeType) || ChargeTimeType.SAVINGS_CLOSURE.equals(chargeTimeType)) {
                continue;
            }
            chargeTimeTypes.add(ChargeEnumerations.chargeTimeType(chargeTimeType));
        }
        return chargeTimeTypes;
    }

    @Override
    public List<EnumOptionData> retrivePaymentModes() {
        return Arrays.asList(chargePaymentMode(ChargePaymentMode.REGULAR), chargePaymentMode(ChargePaymentMode.ACCOUNT_TRANSFER));
    }

    @Override
    public List<EnumOptionData> retrieveLoanCalculationTypes() {
        return Arrays.asList(chargeCalculationType(ChargeCalculationType.FLAT),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_AMOUNT),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_AMOUNT_AND_INTEREST),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_INTEREST),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_DISBURSEMENT_AMOUNT),
                chargeCalculationType(ChargeCalculationType.SLAB_BASED),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_AMOUNT_INTEREST_AND_FEES));
    }

    @Override
    public List<EnumOptionData> retrieveLoanCollectionTimeTypes() {
        return Arrays.asList(chargeTimeType(ChargeTimeType.DISBURSEMENT), chargeTimeType(ChargeTimeType.SPECIFIED_DUE_DATE),
                chargeTimeType(ChargeTimeType.INSTALMENT_FEE), chargeTimeType(ChargeTimeType.OVERDUE_INSTALLMENT),
                chargeTimeType(ChargeTimeType.TRANCHE_DISBURSEMENT), chargeTimeType(ChargeTimeType.UPFRONT_FEE));
    }

    @Override
    public List<EnumOptionData> retrieveSavingsCalculationTypes() {
        return Arrays.asList(chargeCalculationType(ChargeCalculationType.FLAT),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_AMOUNT));
    }

    @Override
    public List<EnumOptionData> retrieveSavingsCollectionTimeTypes() {
        return Arrays.asList(chargeTimeType(ChargeTimeType.SPECIFIED_DUE_DATE), chargeTimeType(ChargeTimeType.SAVINGS_ACTIVATION),
                // chargeTimeType(ChargeTimeType.SAVINGS_CLOSURE),
                chargeTimeType(ChargeTimeType.WITHDRAWAL_FEE), chargeTimeType(ChargeTimeType.ANNUAL_FEE),
                chargeTimeType(ChargeTimeType.MONTHLY_FEE), chargeTimeType(ChargeTimeType.WEEKLY_FEE),
                chargeTimeType(ChargeTimeType.OVERDRAFT_FEE), chargeTimeType(ChargeTimeType.SAVINGS_NOACTIVITY_FEE),
                chargeTimeType(ChargeTimeType.LATE_DEPOSITE_FEE));
    }

    @Override
    public List<EnumOptionData> retrieveClientCalculationTypes() {
        return Arrays.asList(chargeCalculationType(ChargeCalculationType.FLAT));
    }

    @Override
    public List<EnumOptionData> retrieveClientCollectionTimeTypes() {
        return Arrays.asList(chargeTimeType(ChargeTimeType.SPECIFIED_DUE_DATE),
				chargeTimeType(ChargeTimeType.ANNUAL_FEE),
				chargeTimeType(ChargeTimeType.MONTHLY_FEE),
				chargeTimeType(ChargeTimeType.WEEKLY_FEE),
				chargeTimeType(ChargeTimeType.DAILY_FEE));
    }

    @Override
    public List<EnumOptionData> retrieveSharesCalculationTypes() {
        return Arrays.asList(chargeCalculationType(ChargeCalculationType.FLAT),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_AMOUNT));
    }

    @Override
    public List<EnumOptionData> retrieveSharesCollectionTimeTypes() {
        return Arrays.asList(chargeTimeType(ChargeTimeType.SHAREACCOUNT_ACTIVATION), chargeTimeType(ChargeTimeType.SHARE_PURCHASE),
                chargeTimeType(ChargeTimeType.SHARE_REDEEM));
    }

    @Override
    public List<EnumOptionData> retrieveGlimChargeCalculationTypes() {

        return Arrays.asList(glimChargeCalculationType(GlimChargeCalculationType.ROUND),
                glimChargeCalculationType(GlimChargeCalculationType.ROUND_WITH_MAX_CHARGE),
                glimChargeCalculationType(GlimChargeCalculationType.ROUND_WITHOUT_MAX_CHARGE));
    }

    @Override
    public List<EnumOptionData> retrieveSlabChargeTypes() {
        return Arrays.asList(SlabChargeType.fromInt(SlabChargeType.INSTALLMENT_AMOUNT.getValue()),
                SlabChargeType.fromInt(SlabChargeType.INSTALLMENT_NUMBER.getValue()));
    }

    @Override
    public Collection<EnumOptionData> retriveChargePercentageTypes() {
        return ChargePercentageType.chargePercentageTypeOptions();
    }

    @Override
    public Collection<EnumOptionData> retriveChargePercentagePeriodTypes() {
        return ChargePercentagePeriodType.chargePercentagePeriodTypeOptions();
    }

    @Override
    public Collection<EnumOptionData> retrivePenaltyGraceTypes() {
        return PenaltyGraceType.penaltyGraceTypeOptions();
    }

	@Override
	public List<EnumOptionData> retrieveInvestmentChargeCalculationTypes() {
		 return Arrays.asList(chargeCalculationType(ChargeCalculationType.FLAT),
	                chargeCalculationType(ChargeCalculationType.PERCENT_OF_INTEREST));
	}

	@Override
	public List<EnumOptionData> retrieveInvestmentChargeTimeTypes() {
		return Arrays.asList(chargeTimeType(ChargeTimeType.MATURITY));
	}
}