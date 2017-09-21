/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

/**
 * Implements {@link org.apache.fineract.batch.command.CommandStrategy} to handle
 * transactions on a loan. It passes the contents of the body from the
 * BatchRequest to
 * {@link org.mifosplatform.portfolio.client.api.LoanTransactionsApiResource} and gets
 * back the response. This class will also catch any errors raised by
 * {@link org.mifosplatform.portfolio.client.api.LoanTransactionsApiResource} and map
 * those errors to appropriate status codes in BatchResponse.
 * 
 * @author Rishabh Shukla
 * 
 * @see org.apache.fineract.batch.command.CommandStrategy
 * @see org.apache.fineract.batch.domain.BatchRequest
 * @see org.apache.fineract.batch.domain.BatchResponse
 */
@Component
public class LoanTransactionsCommandStrategy implements CommandStrategy {

    private final LoanTransactionsApiResource loansTxnApiResource;

    @Autowired
    public LoanTransactionsCommandStrategy(final LoanTransactionsApiResource loansTxnApiResource) {
        this.loansTxnApiResource = loansTxnApiResource;
    }

    @Override
    public BatchResponse execute(final BatchRequest request, @SuppressWarnings("unused") UriInfo uriInfo) {

        final BatchResponse response = new BatchResponse();
        final String responseBody;

        response.setRequestId(request.getRequestId());
        response.setHeaders(request.getHeaders());
        
        final String[] pathParameters = request.getRelativeUrl().split("/");
        Long loanId = Long.parseLong(pathParameters[1]);
        String transactionType = pathParameters[2].substring(pathParameters[2].indexOf("?command=")+9,pathParameters[2].length());

        // Try-catch blocks to map exceptions to appropriate status codes
        try {

            // Calls 'transaction' function from 'LoanTransactionsApiResource' to do transaction on a loan account          
            responseBody = loansTxnApiResource.executeLoanTransaction(loanId, transactionType, request.getBody());

            response.setStatusCode(200);
            
            // Sets the body of the response after the successful transaction on the loan
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
