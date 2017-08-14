package com.finflux.transaction.execution.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.transaction.execution.api.BankTransactionApiConstants;
import com.finflux.transaction.execution.domain.BankAccountTransaction;
import com.finflux.transaction.execution.service.BankTransactionLoanActionsValidationService;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class BankTransactionDataValidator {

    private final FromJsonHelper fromApiJsonHelper;
    private final BankTransactionLoanActionsValidationService bankTransactionLoanActionsValidationService;

    @Autowired
    public BankTransactionDataValidator(final FromJsonHelper fromApiJsonHelper,
            final BankTransactionLoanActionsValidationService bankTransactionLoanActionsValidationService) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.bankTransactionLoanActionsValidationService = bankTransactionLoanActionsValidationService;
    }

    public void validateForSubmitTransaction(final BankAccountTransaction bankTransaction, final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, BankTransactionApiConstants.SUBMIT_TRANSACTION_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(BankTransactionApiConstants.SUBMIT_BANK_TRANSACTION_RESOURCE);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String transferType = this.fromApiJsonHelper.extractStringNamed(BankTransactionApiConstants.transferType, element);
        baseDataValidator.reset().parameter(BankTransactionApiConstants.transferType).value(transferType).notNull();

        List<Integer> statusList = new ArrayList<>(Arrays.asList(TransactionStatus.SUBMITTED.getValue(),
                TransactionStatus.INITIATED.getValue(), TransactionStatus.PENDING.getValue(), TransactionStatus.SUCCESS.getValue(),
                TransactionStatus.FAILED.getValue(), TransactionStatus.ERROR.getValue()));

        Boolean isSubmitBankTransaction = true;
        this.bankTransactionLoanActionsValidationService.validateForInactiveBankTransactions(bankTransaction.getEntityId(), statusList,
                isSubmitBankTransaction);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}