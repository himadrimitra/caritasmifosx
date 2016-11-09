/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.api;

import java.math.BigDecimal;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;

public class MathUtility {

    public static Boolean isGreaterThanZero(BigDecimal amount) {
        return amount == null ? false : amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public static Boolean isGreaterThanZero(Money amount) {
        return amount == null ? false : amount.getAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    public static BigDecimal zeroIfNull(BigDecimal amount) {
        return (amount == null) ? BigDecimal.ZERO : amount;
    }

    public static BigDecimal zeroIfNull(Money amount) {
        return (amount == null) ? BigDecimal.ZERO : amount.getAmount();
    }

    public static Boolean isNull(BigDecimal amount) {
        return (amount == null);
    }

    public static Boolean isNullOrZero(BigDecimal amount) {
        return (amount == null) || amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public static Boolean isZero(BigDecimal amount) {
        return (amount != null) && amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public static Boolean isEqual(BigDecimal amount1, BigDecimal amount2) {
        return (amount1 != null) && (amount2 != null) && amount1.compareTo(amount2) == 0;
    }

    public static Boolean isEqualOrGreater(BigDecimal first, BigDecimal second) {
        return zeroIfNull(first).compareTo(zeroIfNull(second)) >= 0;
    }

    public static Boolean isGreater(BigDecimal first, BigDecimal second) {
        return zeroIfNull(first).compareTo(zeroIfNull(second)) > 0;
    }
    
    public static Boolean isLesserOrEqualTo(BigDecimal first, BigDecimal second) {
        return zeroIfNull(first).compareTo(zeroIfNull(second)) == -1 || zeroIfNull(first).compareTo(zeroIfNull(second)) == 0;
    }

    public static BigDecimal add(BigDecimal... first) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal bigDecimal : first) {
            sum = sum.add(zeroIfNull(bigDecimal));
        }
        return sum;
    }

    public static BigDecimal subtract(BigDecimal value, BigDecimal... subtractValues) {
        return zeroIfNull(value).subtract(add(subtractValues));
    }

    public static BigDecimal getShare(BigDecimal givenValue, BigDecimal shareAmount, BigDecimal totalAmount, MonetaryCurrency currency) {
        Money amount = Money.of(currency, BigDecimal.valueOf((givenValue.multiply(shareAmount).doubleValue() / totalAmount.doubleValue())));
        return amount.getAmount();
    }

    public static BigDecimal multiply(BigDecimal amount, int val) {
        return amount.multiply(BigDecimal.valueOf(Double.valueOf(val)));
    }

    public static BigDecimal divide(BigDecimal amount, int val, MonetaryCurrency currency) {
        return Money.of(currency, BigDecimal.valueOf(amount.doubleValue() / Double.valueOf(val))).getAmount();
    }

    public static BigDecimal getInstallmentAmount(BigDecimal amount, int numberOfRepayment, MonetaryCurrency currency,
            int currentInstallment) {
        BigDecimal deafultInstallmentAmount = divide(amount, numberOfRepayment, currency);
        if (numberOfRepayment != currentInstallment) { return deafultInstallmentAmount; }
        return amount.subtract(multiply(deafultInstallmentAmount, numberOfRepayment - 1));
    }

    public static BigDecimal multiply(BigDecimal amount, BigDecimal... values) {
    	BigDecimal product = zeroIfNull(amount);
        for (BigDecimal value : values) {
        	product = product.multiply(zeroIfNull(value));
        }
        return product;
    }
}
