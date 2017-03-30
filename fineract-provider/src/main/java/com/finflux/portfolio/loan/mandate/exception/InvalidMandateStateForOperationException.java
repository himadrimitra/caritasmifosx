package com.finflux.portfolio.loan.mandate.exception;

import com.finflux.portfolio.loan.mandate.domain.MandateStatusEnum;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidMandateStateForOperationException extends AbstractPlatformDomainRuleException {
        public InvalidMandateStateForOperationException(final String operation, final MandateStatusEnum currentStatus){
                super("error.msg.loan.mandate.status.invalid.for.requested.operation",
                        operation + " is not supported on Mandate with status " + currentStatus.getCode());
        }
}
