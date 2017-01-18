package com.finflux.portfolio.loanemipacks.service;

import com.finflux.portfolio.loanemipacks.api.LoanEMIPacksApiConstants;
import com.finflux.portfolio.loanemipacks.data.LoanEMIPackDataValidator;
import com.finflux.portfolio.loanemipacks.domain.LoanEMIPack;
import com.finflux.portfolio.loanemipacks.domain.LoanEMIPackRepository;
import com.finflux.portfolio.loanemipacks.exception.LoanEMIPackNotFoundException;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LoanEMIPacksWritePlatformServiceImpl implements LoanEMIPacksWritePlatformService {

        private final LoanEMIPackDataValidator validator;
        private final LoanProductReadPlatformServiceImpl loanProductReadPlatformService;
        private final LoanEMIPackRepository loanEMIPackRepository;

        @Autowired
        public LoanEMIPacksWritePlatformServiceImpl(final LoanEMIPackDataValidator validator,
                final LoanProductReadPlatformServiceImpl loanProductReadPlatformService,
                final LoanEMIPackRepository loanEMIPackRepository){

                this.validator = validator;
                this.loanProductReadPlatformService = loanProductReadPlatformService;
                this.loanEMIPackRepository = loanEMIPackRepository;
        }

        @Transactional
        @Override
        public CommandProcessingResult create(JsonCommand command) {
                Long loanProductId = command.getProductId();
                LoanProductData loanProductData = this.loanProductReadPlatformService.retrieveLoanProduct(loanProductId);
                this.validator.validateForCreate(command.json());
                Integer repaymentEvery = command.integerValueOfParameterNamed(LoanEMIPacksApiConstants.repaymentEvery);
                Integer repaymentFrequencyType = command.integerValueOfParameterNamed(LoanEMIPacksApiConstants.repaymentFrequencyType);
                Integer numberOfRepayments = command.integerValueOfParameterNamed(LoanEMIPacksApiConstants.numberOfRepayments);
                BigDecimal sanctionAmount = command.bigDecimalValueOfParameterNamed(LoanEMIPacksApiConstants.sanctionAmount);
                BigDecimal fixedEmi = command.bigDecimalValueOfParameterNamed(LoanEMIPacksApiConstants.fixedEmi);
                BigDecimal disbursalAmount1 = command.bigDecimalValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalAmount1);
                BigDecimal disbursalAmount2 = null;
                BigDecimal disbursalAmount3 = null;
                BigDecimal disbursalAmount4 = null;
                Integer disbursalEmi2 = null;
                Integer disbursalEmi3 = null;
                Integer disbursalEmi4 = null;
                //disbursal info must be ordered, if there are gaps the later disbursal info would be ignored
                int numberOfDisbursals = 0;
                BigDecimal sumDisbursalAmounts = BigDecimal.ZERO;
                if(disbursalAmount1 !=null){
                        disbursalAmount2 = command.bigDecimalValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalAmount2);
                        disbursalEmi2 = command.integerValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalEmi2);
                        numberOfDisbursals++;
                        sumDisbursalAmounts = sumDisbursalAmounts.add(disbursalAmount1);
                }
                if(disbursalAmount2 != null){
                        disbursalAmount3 = command.bigDecimalValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalAmount3);
                        disbursalEmi3 = command.integerValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalEmi3);
                        numberOfDisbursals++;
                        sumDisbursalAmounts = sumDisbursalAmounts.add(disbursalAmount2);
                }
                if(disbursalAmount3 != null){
                        disbursalAmount4 = command.bigDecimalValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalAmount4);
                        disbursalEmi4 = command.integerValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalEmi4);
                        numberOfDisbursals++;
                        sumDisbursalAmounts = sumDisbursalAmounts.add(disbursalAmount3);
                }
                if(disbursalAmount4 != null){
                        numberOfDisbursals++;
                        sumDisbursalAmounts = sumDisbursalAmounts.add(disbursalAmount4);
                }

                validateAgainstProductDefinition(loanProductData, numberOfRepayments, sanctionAmount, numberOfDisbursals, sumDisbursalAmounts);

                LoanEMIPack loanEMIPack = new LoanEMIPack(loanProductId,
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
                        disbursalEmi4);
                this.loanEMIPackRepository.save(loanEMIPack);

                return new CommandProcessingResultBuilder() //
                        .withCommandId(command.commandId()) //
                        .withEntityId(loanEMIPack.getId())
                        .withProductId(loanProductId)
                        .build();
        }

        @Transactional
        @Override
        public CommandProcessingResult update(JsonCommand command) {
                this.validator.validateForUpdate(command.json());
                LoanEMIPack loanEMIPack = this.loanEMIPackRepository.findOne(command.entityId());
                if(loanEMIPack == null){
                        throw new LoanEMIPackNotFoundException(command.entityId());
                }

                LoanProductData loanProductData = this.loanProductReadPlatformService.retrieveLoanProduct(loanEMIPack.loanProductId);
                Integer repaymentEvery = command.parameterExists(LoanEMIPacksApiConstants.repaymentEvery)?
                        command.integerValueOfParameterNamed(LoanEMIPacksApiConstants.repaymentEvery): loanEMIPack.repaymentEvery;
                Integer repaymentFrequencyType = command.parameterExists(LoanEMIPacksApiConstants.repaymentFrequencyType)?
                        command.integerValueOfParameterNamed(LoanEMIPacksApiConstants.repaymentFrequencyType): loanEMIPack.repaymentFrequencyType;
                Integer numberOfRepayments = command.parameterExists(LoanEMIPacksApiConstants.numberOfRepayments)?
                        command.integerValueOfParameterNamed(LoanEMIPacksApiConstants.numberOfRepayments): loanEMIPack.numberOfRepayments;
                BigDecimal sanctionAmount = command.parameterExists(LoanEMIPacksApiConstants.sanctionAmount)?
                        command.bigDecimalValueOfParameterNamed(LoanEMIPacksApiConstants.sanctionAmount): loanEMIPack.sanctionAmount;
                BigDecimal fixedEmi = command.parameterExists(LoanEMIPacksApiConstants.fixedEmi)?
                        command.bigDecimalValueOfParameterNamed(LoanEMIPacksApiConstants.fixedEmi): loanEMIPack.fixedEmi;
                BigDecimal disbursalAmount1 = command.parameterExists(LoanEMIPacksApiConstants.disbursalAmount1)?
                        command.bigDecimalValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalAmount1): loanEMIPack.disbursalAmount1;
                BigDecimal disbursalAmount2 = null;
                BigDecimal disbursalAmount3 = null;
                BigDecimal disbursalAmount4 = null;
                Integer disbursalEmi2 = null;
                Integer disbursalEmi3 = null;
                Integer disbursalEmi4 = null;
                //disbursal info must be ordered, if there are gaps the later disbursal info would be ignored
                int numberOfDisbursals = 0;
                BigDecimal sumDisbursalAmounts = BigDecimal.ZERO;
                if(disbursalAmount1 !=null){
                        disbursalAmount2 = command.parameterExists(LoanEMIPacksApiConstants.disbursalAmount2)?
                                command.bigDecimalValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalAmount2): loanEMIPack.disbursalAmount2;
                        disbursalEmi2 = command.parameterExists(LoanEMIPacksApiConstants.disbursalEmi2)?
                                command.integerValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalEmi2): loanEMIPack.disbursalEmi2;
                        numberOfDisbursals++;
                        sumDisbursalAmounts = sumDisbursalAmounts.add(disbursalAmount1);
                }
                if(disbursalAmount2 != null){
                        disbursalAmount3 = command.parameterExists(LoanEMIPacksApiConstants.disbursalAmount3)?
                                command.bigDecimalValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalAmount3): loanEMIPack.disbursalAmount3;
                        disbursalEmi3 = command.parameterExists(LoanEMIPacksApiConstants.disbursalEmi3)?
                                command.integerValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalEmi3): loanEMIPack.disbursalEmi3;
                        numberOfDisbursals++;
                        sumDisbursalAmounts = sumDisbursalAmounts.add(disbursalAmount2);
                }
                if(disbursalAmount3 != null){
                        disbursalAmount4 = command.parameterExists(LoanEMIPacksApiConstants.disbursalAmount4)?
                                command.bigDecimalValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalAmount4): loanEMIPack.disbursalAmount4;
                        disbursalEmi4 = command.parameterExists(LoanEMIPacksApiConstants.disbursalEmi4)?
                                command.integerValueOfParameterNamed(LoanEMIPacksApiConstants.disbursalEmi4): loanEMIPack.disbursalEmi4;
                        numberOfDisbursals++;
                        sumDisbursalAmounts = sumDisbursalAmounts.add(disbursalAmount3);
                }
                if(disbursalAmount4 != null){
                        numberOfDisbursals++;
                        sumDisbursalAmounts = sumDisbursalAmounts.add(disbursalAmount4);
                }

                validateAgainstProductDefinition(loanProductData, numberOfRepayments, sanctionAmount, numberOfDisbursals, sumDisbursalAmounts);
                validateDisbursalEMIs(loanProductData, numberOfRepayments, disbursalEmi2, disbursalEmi3, disbursalEmi4);

                Map<String, Object> changes = loanEMIPack.update(repaymentEvery,
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
                        disbursalEmi4);
                this.loanEMIPackRepository.save(loanEMIPack);

                return new CommandProcessingResultBuilder() //
                        .withCommandId(command.commandId()) //
                        .withEntityId(loanEMIPack.getId())
                        .with(changes)
                        .build();
        }

        @Transactional
        @Override
        public CommandProcessingResult delete(JsonCommand command) {
                LoanEMIPack loanEMIPack = this.loanEMIPackRepository.findOne(command.entityId());
                if(loanEMIPack == null){
                        throw new LoanEMIPackNotFoundException(command.entityId());
                }
                this.loanEMIPackRepository.delete(loanEMIPack);
                return new CommandProcessingResultBuilder() //
                        .withCommandId(command.commandId()) //
                        .withEntityId(command.entityId())
                        .build();
        }

        private void validateAgainstProductDefinition(LoanProductData loanProductData, Integer numberOfRepayments,
                BigDecimal sanctionAmount, int numberOfDisbursals, BigDecimal sumDisbursalAmounts) {
                final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                if(loanProductData.getMinNumberOfRepayments() != null && numberOfRepayments < loanProductData.getMinNumberOfRepayments()){
                        final ApiParameterError error = ApiParameterError.generalError(
                                "validation.msg.loanemipack.number.of.repayments.lesser.than.minimum.number.of.repayments",
                                "Number of repayments is lesser than minimum number of repayments defined for the product");
                        dataValidationErrors.add(error);
                }

                if(loanProductData.getMaxNumberOfRepayments() != null && numberOfRepayments > loanProductData.getMaxNumberOfRepayments()){
                        final ApiParameterError error = ApiParameterError.generalError(
                                "validation.msg.loanemipack.number.of.repayments.greater.than.maximum.number.of.repayments",
                                "Number of repayments is greater than maximum number of repayments defined for the product");
                        dataValidationErrors.add(error);
                }
                if(loanProductData.getMultiDisburseLoan()){
                        if(numberOfDisbursals > loanProductData.getMaxTrancheCount()){
                                final ApiParameterError error = ApiParameterError.generalError(
                                        "validation.msg.loanemipack.number.of.tranche.greater.than.maximum.number.of.tranche",
                                        "Number of tranche disbursements is greater than maximum number of tranche defined for the product");
                                dataValidationErrors.add(error);
                        }
                        if(numberOfDisbursals < 1){
                                final ApiParameterError error = ApiParameterError.generalError(
                                        "validation.msg.loanemipack.at.least.one.disbursal.info.to.be.provided",
                                        "Product is configured for multi-tranche, at least one disbursal info must be present");
                                dataValidationErrors.add(error);
                        }
                } else {
                        if(numberOfDisbursals > 0){
                                final ApiParameterError error = ApiParameterError.generalError(
                                        "validation.msg.loanemipack.multiple.disbursals.not.allowed",
                                        "Multiple disbursal/tranche are not allowed for the product");
                                dataValidationErrors.add(error);
                        }
                }
                if(loanProductData.getMinPrincipal() != null && sanctionAmount.compareTo(loanProductData.getMinPrincipal())<0){
                        final ApiParameterError error = ApiParameterError.generalError(
                                "validation.msg.loanemipack.sanctionAmount.less.than.min.principal",
                                "Sanctioned amount is less than the minimum principal allowed by the product");
                        dataValidationErrors.add(error);
                }
                if(loanProductData.getMaxPrincipal() != null && sanctionAmount.compareTo(loanProductData.getMaxPrincipal())>0){
                        final ApiParameterError error = ApiParameterError.generalError(
                                "validation.msg.loanemipack.sanctionAmount.more.than.max.principal",
                                "Sanctioned amount is more than the maximum principal allowed by the product");
                        dataValidationErrors.add(error);
                }
                if(numberOfDisbursals > 0 && sumDisbursalAmounts.compareTo(sanctionAmount) != 0){
                        final ApiParameterError error = ApiParameterError.generalError(
                                "validation.msg.loanemipack.sanctionAmount.not.equal.to.sum.of.disbursal.amounts",
                                "Sanctioned amount should be equal to sum of all disbursal amounts");
                        dataValidationErrors.add(error);
                }
                if(!dataValidationErrors.isEmpty()){
                        throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                                "Validation errors exist.", dataValidationErrors);
                }
        }

        private void validateDisbursalEMIs(LoanProductData loanProductData, Integer numberOfRepayments, Integer disbursalEmi2,
                Integer disbursalEmi3, Integer disbursalEmi4) {
                final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

                if(loanProductData.getMultiDisburseLoan()){
                        if(disbursalEmi2 != null && disbursalEmi2 > numberOfRepayments){
                                final ApiParameterError error = ApiParameterError.generalError(
                                        "validation.msg.loanemipack.disbursalEmi2.should.be.less.than.numberOfRepayments",
                                        "2nd Disbursal intallment number should be less than the total number of repayments");
                                dataValidationErrors.add(error);
                        }
                        if(disbursalEmi3 != null && disbursalEmi3 > numberOfRepayments){
                                final ApiParameterError error = ApiParameterError.generalError(
                                        "validation.msg.loanemipack.disbursalEmi3.should.be.less.than.numberOfRepayments",
                                        "3rd Disbursal intallment number should be less than the total number of repayments");
                                dataValidationErrors.add(error);
                        }
                        if(disbursalEmi4 != null && disbursalEmi4 > numberOfRepayments){
                                final ApiParameterError error = ApiParameterError.generalError(
                                        "validation.msg.loanemipack.disbursalEmi4.should.be.less.than.numberOfRepayments",
                                        "4th Disbursal intallment number should be less than the total number of repayments");
                                dataValidationErrors.add(error);
                        }
                }

                if(!dataValidationErrors.isEmpty()){
                        throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                                "Validation errors exist.", dataValidationErrors);
                }
        }

}
