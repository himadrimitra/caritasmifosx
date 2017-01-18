package com.finflux.portfolio.loanemipacks.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.math.BigDecimal;
import java.util.List;

public class LoanEMIPackData {
        private final Long id;
        private final Long loanProductId;
        private final Integer repaymentEvery;
        private final EnumOptionData repaymentFrequencyType;
        private final Integer numberOfRepayments;
        private final BigDecimal sanctionAmount;
        private final BigDecimal fixedEmi;
        private final BigDecimal disbursalAmount1;
        private final BigDecimal disbursalAmount2;
        private final BigDecimal disbursalAmount3;
        private final BigDecimal disbursalAmount4;
        private final Integer disbursalEmi2;
        private final Integer disbursalEmi3;
        private final Integer disbursalEmi4;
        private final String loanProductName;
        private final Boolean multiDisburseLoan;
        private final Integer maxTrancheCount;
        private final List<EnumOptionData> repaymentFrequencyTypeOptions;

        private LoanEMIPackData(final Long id,
                final Long loanProductId,
                final Integer repaymentEvery,
                final EnumOptionData repaymentFrequencyType,
                final Integer numberOfRepayments,
                final BigDecimal sanctionAmount,
                final BigDecimal fixedEmi,
                final BigDecimal disbursalAmount1,
                final BigDecimal disbursalAmount2,
                final BigDecimal disbursalAmount3,
                final BigDecimal disbursalAmount4,
                final Integer disbursalEmi2,
                final Integer disbursalEmi3,
                final Integer disbursalEmi4,
                final String loanProductName,
                final Boolean multiDisburseLoan,
                final Integer maxTrancheCount,
                final List<EnumOptionData> repaymentFrequencyTypeOptions){

                this.id = id;
                this.loanProductId = loanProductId;
                this.repaymentEvery = repaymentEvery;
                this.repaymentFrequencyType = repaymentFrequencyType;
                this.numberOfRepayments = numberOfRepayments;
                this.sanctionAmount = sanctionAmount;
                this.fixedEmi = fixedEmi;
                this.disbursalAmount1 = disbursalAmount1;
                this.disbursalAmount2 = disbursalAmount2;
                this.disbursalAmount3 = disbursalAmount3;
                this.disbursalAmount4 = disbursalAmount4;
                this.disbursalEmi2 = disbursalEmi2;
                this.disbursalEmi3 = disbursalEmi3;
                this.disbursalEmi4 = disbursalEmi4;
                this.loanProductName = loanProductName;
                this.multiDisburseLoan = multiDisburseLoan;
                this.maxTrancheCount = maxTrancheCount;
                this.repaymentFrequencyTypeOptions = repaymentFrequencyTypeOptions;

        }

        public static LoanEMIPackData loanEMIPackData(final Long id,
                final Long loanProductId,
                final Integer repaymentEvery,
                final EnumOptionData repaymentFrequencyType,
                final Integer numberOfRepayments,
                final BigDecimal sanctionAmount,
                final BigDecimal fixedEmi,
                final BigDecimal disbursalAmount1,
                final BigDecimal disbursalAmount2,
                final BigDecimal disbursalAmount3,
                final BigDecimal disbursalAmount4,
                final Integer disbursalEmi2,
                final Integer disbursalEmi3,
                final Integer disbursalEmi4,
                final String loanProductName){

                Boolean multiDisburseLoan = null;
                Integer maxTrancheCount = null;
                List<EnumOptionData> repaymentFrequencyTypeOptions = null;

                return new LoanEMIPackData(id,
                        loanProductId,
                        repaymentEvery,
                        repaymentFrequencyType,
                        numberOfRepayments,
                        sanctionAmount,
                        fixedEmi,
                        disbursalAmount1,
                        disbursalAmount2,
                        disbursalAmount3,
                        disbursalAmount4,
                        disbursalEmi2,
                        disbursalEmi3,
                        disbursalEmi4,
                        loanProductName,
                        multiDisburseLoan,
                        maxTrancheCount,
                        repaymentFrequencyTypeOptions);
        }

        public static LoanEMIPackData template(final Long loanProductId,
                final String loanProductName,
                final Boolean multiDisburseLoan,
                final Integer maxTrancheCount,
                final List<EnumOptionData> repaymentFrequencyTypeOptions){

                Long id = null;
                Integer repaymentEvery = null;
                EnumOptionData repaymentFrequencyType = null;
                Integer numberOfRepayments = null;
                BigDecimal sanctionAmount = null;
                BigDecimal fixedEmi = null;
                BigDecimal disbursalAmount1 = null;
                BigDecimal disbursalAmount2 = null;
                BigDecimal disbursalAmount3 = null;
                BigDecimal disbursalAmount4 = null;
                Integer disbursalEmi2 = null;
                Integer disbursalEmi3 = null;
                Integer disbursalEmi4 = null;

                return new LoanEMIPackData(id,
                        loanProductId,
                        repaymentEvery,
                        repaymentFrequencyType,
                        numberOfRepayments,
                        sanctionAmount,
                        fixedEmi,
                        disbursalAmount1,
                        disbursalAmount2,
                        disbursalAmount3,
                        disbursalAmount4,
                        disbursalEmi2,
                        disbursalEmi3,
                        disbursalEmi4,
                        loanProductName,
                        multiDisburseLoan,
                        maxTrancheCount,
                        repaymentFrequencyTypeOptions);
        }

        public static LoanEMIPackData loanData(final Long loanProductId, final String loanProductName){
                Long id = null;
                Integer repaymentEvery = null;
                EnumOptionData repaymentFrequencyType = null;
                Integer numberOfRepayments = null;
                BigDecimal sanctionAmount = null;
                BigDecimal fixedEmi = null;
                BigDecimal disbursalAmount1 = null;
                BigDecimal disbursalAmount2 = null;
                BigDecimal disbursalAmount3 = null;
                BigDecimal disbursalAmount4 = null;
                Integer disbursalEmi2 = null;
                Integer disbursalEmi3 = null;
                Integer disbursalEmi4 = null;
                Boolean multiDisburseLoan = null;
                Integer maxTrancheCount = null;
                List<EnumOptionData> repaymentFrequencyTypeOptions = null;

                return new LoanEMIPackData(id,
                        loanProductId,
                        repaymentEvery,
                        repaymentFrequencyType,
                        numberOfRepayments,
                        sanctionAmount,
                        fixedEmi,
                        disbursalAmount1,
                        disbursalAmount2,
                        disbursalAmount3,
                        disbursalAmount4,
                        disbursalEmi2,
                        disbursalEmi3,
                        disbursalEmi4,
                        loanProductName,
                        multiDisburseLoan,
                        maxTrancheCount,
                        repaymentFrequencyTypeOptions);
        }
}
