package com.finflux.reconcilation.bankstatement.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

@SuppressWarnings("serial")
public class InvalidCifFileFormatException extends AbstractPlatformDomainRuleException {

    public InvalidCifFileFormatException() {
        super("error.msg.invalid.cif.format.excel.file.", "CPIF File format is not valid.");
    }
}
