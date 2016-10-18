package org.apache.fineract.portfolio.charge.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class ChargeNotSupportedException extends AbstractPlatformResourceNotFoundException {

    public ChargeNotSupportedException(final String entityName, final Long chargeId, final String userErrorMessage) {
        super("error.msg.charge.not.supported.to."+entityName+"", "Charge With identifier " + chargeId +" "+userErrorMessage+" ", chargeId);
    }

}
