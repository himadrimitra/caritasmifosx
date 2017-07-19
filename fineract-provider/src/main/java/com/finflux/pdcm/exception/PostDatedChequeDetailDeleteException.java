package com.finflux.pdcm.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PostDatedChequeDetailDeleteException extends AbstractPlatformDomainRuleException {

    public PostDatedChequeDetailDeleteException(final Long id) {
        super("error.msg.pdc.id.already.deleted", "PDC details with identifier " + id + " already deleted.", id);
    }

}