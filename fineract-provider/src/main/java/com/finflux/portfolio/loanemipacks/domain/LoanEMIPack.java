package com.finflux.portfolio.loanemipacks.domain;

import com.finflux.portfolio.loanemipacks.api.LoanEMIPacksApiConstants;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "f_loan_emi_packs")
public class LoanEMIPack  extends AbstractPersistable<Long> {

        @Column(name = "loan_product_id", nullable = false)
        public Long loanProductId;

        @Column(name = "repay_every", nullable = false)
        public Integer repaymentEvery;

        @Column(name = "repayment_period_frequency_enum", nullable = false)
        public Integer repaymentFrequencyType;

        @Column(name = "number_of_repayments", nullable = false)
        public Integer numberOfRepayments;

        @Column(name = "sanction_amount", nullable = false)
        public BigDecimal sanctionAmount;

        @Column(name = "fixed_emi", nullable = false)
        public BigDecimal fixedEmi;

        @Column(name = "disbursal_1_amount", nullable = true)
        public BigDecimal disbursalAmount1;

        @Column(name = "disbursal_2_amount", nullable = true)
        public BigDecimal disbursalAmount2;

        @Column(name = "disbursal_3_amount", nullable = true)
        public BigDecimal disbursalAmount3;

        @Column(name = "disbursal_4_amount", nullable = true)
        public BigDecimal disbursalAmount4;

        @Column(name = "disbursal_2_emi", nullable = true)
        public Integer disbursalEmi2;

        @Column(name = "disbursal_3_emi", nullable = true)
        public Integer disbursalEmi3;

        @Column(name = "disbursal_4_emi", nullable = true)
        public Integer disbursalEmi4;

        protected LoanEMIPack(){

        }

        public LoanEMIPack(final Long loanProductId,
                final Integer repaymentEvery,
                final Integer repaymentFrequencyType,
                final Integer numberOfRepayments,
                final BigDecimal sanctionAmount,
                final BigDecimal fixedEmi,
                final BigDecimal disbursalAmount1,
                final BigDecimal disbursalAmount2,
                final BigDecimal disbursalAmount3,
                final BigDecimal disbursalAmount4,
                final Integer disbursalEmi2,
                final Integer disbursalEmi3,
                final Integer disbursalEmi4){

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
        }

        public Map<String,Object> update(Integer repaymentEvery, Integer repaymentFrequencyType, Integer numberOfRepayments, BigDecimal sanctionAmount,
                BigDecimal fixedEmi, BigDecimal disbursalAmount1, BigDecimal disbursalAmount2, BigDecimal disbursalAmount3,
                BigDecimal disbursalAmount4, Integer disbursalEmi2, Integer disbursalEmi3, Integer disbursalEmi4) {

                Map<String,Object> changes = new HashMap<>();

                if(this.repaymentEvery.compareTo(repaymentEvery) != 0){
                        this.repaymentEvery = repaymentEvery;
                        changes.put(LoanEMIPacksApiConstants.repaymentEvery, this.repaymentEvery);
                }

                if(this.repaymentFrequencyType.compareTo(repaymentFrequencyType) != 0){
                        this.repaymentFrequencyType = repaymentFrequencyType;
                        changes.put(LoanEMIPacksApiConstants.repaymentFrequencyType, this.repaymentFrequencyType);
                }

                if(this.numberOfRepayments.compareTo(numberOfRepayments) != 0){
                        this.numberOfRepayments = numberOfRepayments;
                        changes.put(LoanEMIPacksApiConstants.numberOfRepayments, this.numberOfRepayments);
                }

                if(this.sanctionAmount.compareTo(sanctionAmount) != 0){
                        this.sanctionAmount = sanctionAmount;
                        changes.put(LoanEMIPacksApiConstants.sanctionAmount, this.sanctionAmount);
                }

                if(this.fixedEmi.compareTo(fixedEmi) != 0){
                        this.fixedEmi = fixedEmi;
                        changes.put(LoanEMIPacksApiConstants.fixedEmi, this.fixedEmi);
                }

                if(differenceExists(this.disbursalAmount1, disbursalAmount1)){
                        this.disbursalAmount1 = disbursalAmount1;
                        changes.put(LoanEMIPacksApiConstants.disbursalAmount1, this.disbursalAmount1);
                }

                if(differenceExists(this.disbursalAmount2, disbursalAmount2)){
                        this.disbursalAmount2 = disbursalAmount2;
                        changes.put(LoanEMIPacksApiConstants.disbursalAmount2, this.disbursalAmount2);
                }

                if(differenceExists(this.disbursalAmount3, disbursalAmount3)){
                        this.disbursalAmount3 = disbursalAmount3;
                        changes.put(LoanEMIPacksApiConstants.disbursalAmount3, this.disbursalAmount3);
                }

                if(differenceExists(this.disbursalAmount4, disbursalAmount4)){
                        this.disbursalAmount4 = disbursalAmount4;
                        changes.put(LoanEMIPacksApiConstants.disbursalAmount4, this.disbursalAmount4);
                }

                if(differenceExists(this.disbursalEmi2, disbursalEmi2)){
                        this.disbursalEmi2 = disbursalEmi2;
                        changes.put(LoanEMIPacksApiConstants.disbursalEmi2, this.disbursalEmi2);
                }

                if(differenceExists(this.disbursalEmi3, disbursalEmi3)){
                        this.disbursalEmi3 = disbursalEmi3;
                        changes.put(LoanEMIPacksApiConstants.disbursalEmi3, this.disbursalEmi3);
                }

                if(differenceExists(this.disbursalEmi4, disbursalEmi4)){
                        this.disbursalEmi4 = disbursalEmi4;
                        changes.put(LoanEMIPacksApiConstants.disbursalEmi4, this.disbursalEmi4);
                }

                return changes;
        }

        private boolean differenceExists(final Comparable baseValue, final Comparable workingCopyValue) {
                boolean differenceExists = false;

                if (baseValue != null) {
                        if (workingCopyValue != null) {
                                differenceExists = baseValue.compareTo(workingCopyValue) != 0;
                        } else {
                                differenceExists = true;
                        }
                } else {
                        differenceExists = workingCopyValue != null;
                }

                return differenceExists;
        }

}
