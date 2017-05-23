package org.apache.fineract.portfolio.collectionsheet.serialization;

import java.math.BigDecimal;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.serialization.AbstractFromApiJsonDeserializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.collectionsheet.CollectionSheetConstants;
import org.apache.fineract.portfolio.collectionsheet.command.CollectionSheetClientChargeRepaymentCommand;
import org.apache.fineract.portfolio.collectionsheet.command.ClientChargeRepaymentCommand;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class CollectionSheetChargeRepaymentCommandFromApiJsonDeserializer extends AbstractFromApiJsonDeserializer<CollectionSheetClientChargeRepaymentCommand>{

	private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public CollectionSheetChargeRepaymentCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }
    
    @Override
    public CollectionSheetClientChargeRepaymentCommand commandFromApiJson(final String json) {
    	if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
    	
    	final JsonElement element = this.fromApiJsonHelper.parse(json);
        final JsonObject topLevelJsonElement = element.getAsJsonObject();

        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        final String dateFormat = this.fromApiJsonHelper.extractStringNamed("dateFormat", element);
        ClientChargeRepaymentCommand[] chargeTransactions = null;
        
        if (element.isJsonObject()) {
            if (topLevelJsonElement.has(CollectionSheetConstants.clientChargeTransactionsParamName)
                    && topLevelJsonElement.get(CollectionSheetConstants.clientChargeTransactionsParamName).isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get(CollectionSheetConstants.clientChargeTransactionsParamName).getAsJsonArray();
                chargeTransactions = new ClientChargeRepaymentCommand[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject chargeTransactionElement = array.get(i).getAsJsonObject();

                    final Long clientId = this.fromApiJsonHelper.extractLongNamed(CollectionSheetConstants.clientIdParamName, chargeTransactionElement);
                    final Long chargeId = this.fromApiJsonHelper.extractLongNamed(CollectionSheetConstants.chargeIdParamName, chargeTransactionElement);
                    final BigDecimal chargeAmount = this.fromApiJsonHelper.extractBigDecimalNamed(CollectionSheetConstants.transactionAmountParamName,
                            chargeTransactionElement, locale);
                    if(!chargeAmount.equals(BigDecimal.ZERO)){
                   chargeTransactions[i] = new ClientChargeRepaymentCommand(clientId, chargeId, chargeAmount);
                    }
                }
            }
            
        }
    	return new CollectionSheetClientChargeRepaymentCommand(dateFormat,locale,transactionDate,chargeTransactions);
    }
}
