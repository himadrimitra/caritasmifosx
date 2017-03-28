package com.finflux.portfolio.loan.mandate.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class DoesNotMatchActiveMandateUMRNException extends AbstractPlatformDomainRuleException {

        public DoesNotMatchActiveMandateUMRNException(final String umrn) {
                super("error.msg.loan.mandate.umrn.does.not.match.active.mandate.umrn",
                        "UMRN provided does not match the UMRN of active mandate", umrn);
        }
}
