package org.apache.fineract.batch.command.internal;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.apache.fineract.batch.command.CommandStrategy;
import org.apache.fineract.batch.domain.BatchRequest;
import org.apache.fineract.batch.domain.BatchResponse;
import org.apache.fineract.batch.exception.ErrorHandler;
import org.apache.fineract.batch.exception.ErrorInfo;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.api.LoanTransactionsApiResource;
import org.apache.fineract.portfolio.savings.api.SavingsAccountChargesApiResource;
import org.apache.fineract.portfolio.savings.api.SavingsAccountTransactionsApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Implements {@link org.apache.fineract.batch.command.CommandStrategy} to
 * handle loan repayments and savings payments.
 *
 * @see org.apache.fineract.batch.command.CommandStrategy
 * @see org.apache.fineract.batch.domain.BatchRequest
 * @see org.apache.fineract.batch.domain.BatchResponse
 */
@Component
public class ClientPaymentsCommandStrategy implements CommandStrategy {

    private final FromJsonHelper fromJsonHelper;
    private final LoanTransactionsApiResource loansTxnApiResource;
    private final SavingsAccountTransactionsApiResource savingsTxnApiResource;
    private final SavingsAccountChargesApiResource savingsAccountChargesApiResource;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final ClientRepositoryWrapper clientRepositoryWrapper;

    @Autowired
    public ClientPaymentsCommandStrategy(final FromJsonHelper fromJsonHelper, final LoanTransactionsApiResource loansTxnApiResource,
            final SavingsAccountTransactionsApiResource savingsTxnApiResource,
            final SavingsAccountChargesApiResource savingsAccountChargesApiResource,
            final BusinessEventNotifierService businessEventNotifierService, final ClientRepositoryWrapper clientRepositoryWrapper) {
        this.fromJsonHelper = fromJsonHelper;
        this.loansTxnApiResource = loansTxnApiResource;
        this.savingsTxnApiResource = savingsTxnApiResource;
        this.savingsAccountChargesApiResource = savingsAccountChargesApiResource;
        this.businessEventNotifierService = businessEventNotifierService;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
    }

    @Override
    public BatchResponse execute(final BatchRequest request, @SuppressWarnings("unused") final UriInfo uriInfo) {

        final BatchResponse response = new BatchResponse();

        response.setRequestId(request.getRequestId());
        response.setHeaders(request.getHeaders());

        final String[] pathParameters = request.getRelativeUrl().split("/");
        final Long clientId = Long.parseLong(pathParameters[1]);

        // Try-catch blocks to map exceptions to appropriate status codes
        try {
            final Client client = this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
            final String requestBody = request.getBody();
            final JsonObject json = this.fromJsonHelper.parse(requestBody).getAsJsonObject();
            final String receiptNumber = json.get("receiptNumber").getAsString();
            final StringBuilder responses = new StringBuilder();
            final JsonArray loanRepayments = json.get("loanRepayments").getAsJsonArray();
            for (final JsonElement jsonElement : loanRepayments) {
                final JsonObject repayment = jsonElement.getAsJsonObject();
                final Long loanId = repayment.remove("loanId").getAsLong();
                final String repaymentResponse = this.loansTxnApiResource.executeLoanTransaction(loanId, "repayment", repayment.toString());
                responses.append(repaymentResponse);
            }
            final JsonArray savingsDeposits = json.get("savingsDeposits").getAsJsonArray();
            for (final JsonElement jsonElement : savingsDeposits) {
                final JsonObject deposit = jsonElement.getAsJsonObject();
                final Long savingsAccountId = deposit.remove("savingsAccountId").getAsLong();
                final String depositResponse = this.savingsTxnApiResource.transaction(savingsAccountId, "deposit", deposit.toString());
                responses.append(depositResponse);
            }
            final JsonArray savingCharges = json.get("savingCharges").getAsJsonArray();
            for (final JsonElement jsonElement : savingCharges) {
                final JsonObject charge = jsonElement.getAsJsonObject();
                final Long savingsAccountId = charge.remove("savingAccountId").getAsLong();
                final Long chargeId = charge.remove("savingsChargeId").getAsLong();
                final String chargeResponse = this.savingsAccountChargesApiResource.payOrWaiveSavingsAccountCharge(savingsAccountId,
                        chargeId, "paycharge", charge.toString());
                responses.append(chargeResponse);
            }

            final Map<BUSINESS_ENTITY, Object> map = new HashMap<>(1);
            map.put(BUSINESS_ENTITY.CLIENT, client);
            map.put(BUSINESS_ENTITY.RECEIPT_NO, receiptNumber);
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.CLIENT_PAYMENTS, map);

            response.setStatusCode(200);
            // Sets the body of the response after the successful activation of
            // the client
            response.setBody(responses.toString());

        } catch (final RuntimeException e) {

            // Gets an object of type ErrorInfo, containing information about
            // raised exception
            final ErrorInfo ex = ErrorHandler.handler(e);

            response.setStatusCode(ex.getStatusCode());
            response.setBody(ex.getMessage());
        }

        return response;
    }

}
