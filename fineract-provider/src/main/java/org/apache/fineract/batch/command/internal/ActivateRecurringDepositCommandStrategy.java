package org.apache.fineract.batch.command.internal;

import javax.ws.rs.core.UriInfo;

import org.apache.fineract.batch.command.CommandStrategy;
import org.apache.fineract.batch.domain.BatchRequest;
import org.apache.fineract.batch.domain.BatchResponse;
import org.apache.fineract.batch.exception.ErrorHandler;
import org.apache.fineract.batch.exception.ErrorInfo;
import org.apache.fineract.portfolio.savings.api.RecurringDepositAccountsApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivateRecurringDepositCommandStrategy implements CommandStrategy{
	
	private final RecurringDepositAccountsApiResource recurringDepositAccountsApiResource;
	
	@Autowired
	public ActivateRecurringDepositCommandStrategy(
			RecurringDepositAccountsApiResource recurringDepositAccountsApiResource) {
		this.recurringDepositAccountsApiResource = recurringDepositAccountsApiResource;
	}



	@Override
	public BatchResponse execute(BatchRequest request, UriInfo uriInfo) {
		 final BatchResponse response = new BatchResponse();
	        final String responseBody;

	        response.setRequestId(request.getRequestId());
	        response.setHeaders(request.getHeaders());
	        
	        final String[] pathParameters = request.getRelativeUrl().split("/");
	        final Long RDId = Long.parseLong(pathParameters[1].substring(0, pathParameters[1].indexOf("?")));
	        try{
	        	
	        	// Calls 'approve' function from 'RecurringDepositProductsApiResource' to approve a loan          
	            responseBody = recurringDepositAccountsApiResource.handleCommands(RDId, "activate", uriInfo ,request.getBody());

	            response.setStatusCode(200);
	            // Sets the body of the response after the successful approval of a loan
	            response.setBody(responseBody);
	        	
	        } catch (RuntimeException e) {

	            // Gets an object of type ErrorInfo, containing information about
	            // raised exception
	            ErrorInfo ex = ErrorHandler.handler(e);

	            response.setStatusCode(ex.getStatusCode());
	            response.setBody(ex.getMessage());
	        }

	        return response;
	}

}
