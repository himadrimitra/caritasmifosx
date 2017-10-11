package org.apache.fineract.portfolio.client.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ClientRecurringChargeNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ClientRecurringChargeNotFoundException(final Long clientId) {
        super("error.msg.client.recurringchargefor.clientid.invalid", "Client recurringcharge for client with identifier " + clientId + " does not exist", clientId);
    }
    
    public ClientRecurringChargeNotFoundException(final Long clientId,final Long recurringChargeId) {
        super("error.msg.client.recurringchargefor.id.invalid", "Client recurringcharge with identifier " + recurringChargeId + " does not exist for", clientId);
    }
}
