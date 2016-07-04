package com.finflux.reconcilation.bankstatement.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

@SuppressWarnings("serial")
public class NotExcelFileException extends  AbstractPlatformDomainRuleException{

    public NotExcelFileException() {
        super("error.msg.not.excel.file.", "CPIF File is not Excel file.");
    }

}
