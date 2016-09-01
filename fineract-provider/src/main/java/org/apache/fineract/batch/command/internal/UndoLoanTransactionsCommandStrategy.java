package org.apache.fineract.batch.command.internal;

import javax.ws.rs.core.UriInfo;

import org.apache.fineract.batch.command.CommandStrategy;
import org.apache.fineract.batch.domain.BatchRequest;
import org.apache.fineract.batch.domain.BatchResponse;
import org.apache.fineract.batch.exception.ErrorHandler;
import org.apache.fineract.batch.exception.ErrorInfo;
import org.apache.fineract.portfolio.loanaccount.api.LoanTransactionsApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UndoLoanTransactionsCommandStrategy implements CommandStrategy {

    private final LoanTransactionsApiResource loanTransactionsApiResource;

    @Autowired
    public UndoLoanTransactionsCommandStrategy(final LoanTransactionsApiResource loanTransactionsApiResource) {
        this.loanTransactionsApiResource = loanTransactionsApiResource;
    }

    @Override
    public BatchResponse execute(final BatchRequest request, @SuppressWarnings("unused") UriInfo uriInfo) {

        final BatchResponse response = new BatchResponse();
        final String responseBody;

        response.setRequestId(request.getRequestId());
        response.setHeaders(request.getHeaders());

        final String[] pathParameters = request.getRelativeUrl().split("/");
        Long loanId = Long.parseLong(pathParameters[1]);
        Long transactionId = Long.parseLong(pathParameters[3].substring(0, pathParameters[3].indexOf("?")));

        try {

            responseBody = loanTransactionsApiResource.adjustLoanTransaction(loanId, transactionId, request.getBody());

            response.setStatusCode(200);
            response.setBody(responseBody);

        } catch (RuntimeException e) {

            ErrorInfo ex = ErrorHandler.handler(e);

            response.setStatusCode(ex.getStatusCode());
            response.setBody(ex.getMessage());
        }

        return response;
    }

}
