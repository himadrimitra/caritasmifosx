package org.apache.fineract.portfolio.group.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class UpdateStaffHierarchyException extends AbstractPlatformDomainRuleException {

    public UpdateStaffHierarchyException(Long staffId) {
        super("Can't.update.staff.to.other.than.parent.staff.when.LoanOfficerToCenterHierarchy.is.enabled ",
                "Can't update staff to other than Parent staff when LoanOfficerToCenterHierarchy is enabled" + staffId + "`", staffId);
    }

}
