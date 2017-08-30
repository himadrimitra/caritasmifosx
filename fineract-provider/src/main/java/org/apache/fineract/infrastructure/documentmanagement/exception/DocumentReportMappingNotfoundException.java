package org.apache.fineract.infrastructure.documentmanagement.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class DocumentReportMappingNotfoundException extends AbstractPlatformDomainRuleException {

    public DocumentReportMappingNotfoundException() {
        super("error.msg.document.report.mapping.notfound", "No mapping is defined", "No mapping is defined");
    }

}
