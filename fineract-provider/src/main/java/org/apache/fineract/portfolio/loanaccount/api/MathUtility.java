/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.api;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;

public class MathUtility {

    public static Boolean isGreaterThanZero(final BigDecimal amount) {
        return amount == null ? false : compareTo(amount, BigDecimal.ZERO) > 0;
    }

    public static Boolean isGreaterThanZero(final Money amount) {
        return amount == null ? false : amount.getAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    public static BigDecimal zeroIfNull(final BigDecimal amount) {
        return (amount == null) ? BigDecimal.ZERO : amount;
    }

    public static BigDecimal zeroIfNull(final Money amount) {
        return (amount == null) ? BigDecimal.ZERO : amount.getAmount();
    }

    public static Boolean isNull(final BigDecimal amount) {
        return (amount == null);
    }

    public static Boolean isNullOrZero(final BigDecimal amount) {
        return (amount == null) || compareTo(amount, BigDecimal.ZERO) == 0;
    }

    public static Boolean isZero(final BigDecimal amount) {
        return (amount != null) && compareTo(amount, BigDecimal.ZERO) == 0;
    }

    public static Boolean isEqual(final BigDecimal amount1, final BigDecimal amount2) {
        return (amount1 != null) && (amount2 != null) && compareTo(amount1, amount2) == 0;
    }

    public static Boolean isEqualOrGreater(final BigDecimal first, final BigDecimal second) {
        return compareTo(first, second) >= 0;
    }

    public static Boolean isGreater(final BigDecimal first, final BigDecimal second) {
        return compareTo(first, second) > 0;
    }

    public static Boolean isLesser(final BigDecimal first, final BigDecimal second) {
        return compareTo(first, second) < 0;
    }

    public static Boolean isNegative(final BigDecimal amount) {
        return compareTo(amount, BigDecimal.ZERO) < 0;
    }

    public static Boolean isLesserOrEqualTo(final BigDecimal first, final BigDecimal second) {
        return compareTo(first, second) <= 0;
    }

    public static BigDecimal add(final BigDecimal... first) {
        BigDecimal sum = BigDecimal.ZERO;
        for (final BigDecimal bigDecimal : first) {
            sum = sum.add(zeroIfNull(bigDecimal));
        }
        return sum;
    }

    public static BigDecimal subtract(final BigDecimal value, final BigDecimal... subtractValues) {
        return zeroIfNull(value).subtract(add(subtractValues));
    }

    public static BigDecimal getShare(final BigDecimal givenValue, final BigDecimal shareAmount, final BigDecimal totalAmount,
            final MonetaryCurrency currency) {
        Money amount = Money.of(currency, BigDecimal.valueOf((givenValue.multiply(shareAmount).doubleValue() / totalAmount.doubleValue())));
        return amount.getAmount();
    }
    
    public static BigDecimal getShare(final BigDecimal givenValue, final Integer share, final Integer total,
            final MonetaryCurrency currency) {
        Money amount = Money.of(currency, BigDecimal.valueOf((multiply(givenValue, share).doubleValue() / total.doubleValue())));
        return amount.getAmount();
    }

    public static BigDecimal multiply(final BigDecimal amount, final int val) {
        return amount.multiply(BigDecimal.valueOf(Double.valueOf(val)));
    }

    public static BigDecimal divide(final BigDecimal amount, final int val, final MonetaryCurrency currency) {
        return Money.of(currency, BigDecimal.valueOf(amount.doubleValue() / Double.valueOf(val))).getAmount();
    }

    public static BigDecimal getInstallmentAmount(final BigDecimal amount, final int numberOfRepayment, final MonetaryCurrency currency,
            final int currentInstallment) {
        BigDecimal deafultInstallmentAmount = divide(amount, numberOfRepayment, currency);
        if (numberOfRepayment != currentInstallment) { return deafultInstallmentAmount; }
        return amount.subtract(multiply(deafultInstallmentAmount, numberOfRepayment - 1));
    }

    public static BigDecimal multiply(final BigDecimal amount, final BigDecimal... values) {
        BigDecimal product = zeroIfNull(amount);
        for (final BigDecimal value : values) {
            product = product.multiply(zeroIfNull(value));
        }
        return product;
    }
    
    public static BigDecimal percentageOf(final BigDecimal value, final BigDecimal percentage) {

        BigDecimal percentageOf = BigDecimal.ZERO;

        if (isGreaterThanZero(value)) {
            final MathContext mc = new MathContext(8, MoneyHelper.getRoundingMode());
            final BigDecimal multiplicand = percentage.divide(BigDecimal.valueOf(100l), mc);
            percentageOf = value.multiply(multiplicand, mc);
        }
        return percentageOf;
    }

    public static String randomStringGenerator(final String prefix, final int len, final String sourceSetString) {
        final int lengthOfSource = sourceSetString.length();
        final Random rnd = new Random();
        final StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append((sourceSetString).charAt(rnd.nextInt(lengthOfSource)));
        }
        return (prefix + (sb.toString()));
    }

    public static String randomStringGenerator(final String prefix, final int len) {
        return randomStringGenerator(prefix, len, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    public static String randomNameGenerator(final String prefix, final int lenOfRandomSuffix) {
        return randomStringGenerator(prefix, lenOfRandomSuffix);
    }

    public static Integer compareTo(final BigDecimal first, final BigDecimal second) {
        return zeroIfNull(first).compareTo(zeroIfNull(second));
    }
    
    public static boolean isInRange(final BigDecimal min, final BigDecimal max, final BigDecimal value) {
        return isEqualOrGreater(value, min) && isLesserOrEqualTo(value, max);
    }
}