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
package org.apache.fineract.infrastructure.accountnumberformat.domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum EntityAccountType {

    INVALID(0, "accountType.invalid"), //
    CLIENT(1, "accountType.client"), //
    LOAN(2, "accountType.loan"), //
    SAVINGS(3, "accountType.savings"), //
    CENTER(4, "accountType.center"), //
    GROUP(5, "accountType.group"), //
    SHARES(6, "accountType.shares");

    private final Integer value;
    private final String code;

    private EntityAccountType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, EntityAccountType> intToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;
    static {
        int i = 0;
        for (final EntityAccountType type : EntityAccountType.values()) {
            if (i == 0) {
                minValue = type.value;
            }
            intToEnumMap.put(type.value, type);
            if (minValue >= type.value) {
                minValue = type.value;
            }
            if (maxValue < type.value) {
                maxValue = type.value;
            }
            i = i + 1;
        }
    }

    public static EntityAccountType fromInt(final int i) {
        final EntityAccountType type = intToEnumMap.get(Integer.valueOf(i));
        return type;
    }

    public static int getMinValue() {
        return minValue;
    }

    public static int getMaxValue() {
        return maxValue;
    }

    @Override
    public String toString() {
        return name().toString();
    }

    public boolean isClientAccount() {
        return this.value.equals(EntityAccountType.CLIENT.getValue());
    }

    public boolean isLoanAccount() {
        return this.value.equals(EntityAccountType.LOAN.getValue());
    }

    public boolean isSavingsAccount() {
        return this.value.equals(EntityAccountType.SAVINGS.getValue());
    }

    public Boolean isCenterAccount() {
        return this.value.equals(EntityAccountType.CENTER.getValue());
    }

    public Boolean isGroupAccount() {
        return this.value.equals(EntityAccountType.GROUP.getValue());
    }

    public static EntityAccountType fromInt(final Integer frequency) {
        EntityAccountType entityAccountType = EntityAccountType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    entityAccountType = EntityAccountType.CLIENT;
                break;
                case 2:
                    entityAccountType = EntityAccountType.LOAN;
                break;
                case 3:
                    entityAccountType = EntityAccountType.SAVINGS;
                break;
                case 4:
                    entityAccountType = EntityAccountType.CENTER;
                break;
                case 5:
                    entityAccountType = EntityAccountType.GROUP;
                break;
                case 6:
                    entityAccountType = EntityAccountType.SHARES;
                break;
            }
        }
        return entityAccountType;
    }

    public static EnumOptionData entityAccountTypeOptionData(final int id) {
        return entityAccountType(EntityAccountType.fromInt(id));
    }

    public static EnumOptionData entityAccountType(final EntityAccountType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case CLIENT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Client");
            break;
            case LOAN:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Loan");
            break;
            case SAVINGS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Savings");
            break;
            case CENTER:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Center");
            break;
            case GROUP:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Group");
            break;
            case SHARES:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Shares");
            break;
            default:
            break;

        }
        return optionData;
    }
}
