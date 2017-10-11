package org.apache.fineract.portfolio.charge.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class ChargeSlabRangeOverlapException  extends AbstractPlatformDomainRuleException {

    public ChargeSlabRangeOverlapException(String type) {
        super("error.msg.charge.slab.installment."+type+".range.overlapping","Slabs installment "+type+" range overlapping.");
    }

}
