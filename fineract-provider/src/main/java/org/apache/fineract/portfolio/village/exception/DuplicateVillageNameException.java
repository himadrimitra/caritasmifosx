package org.apache.fineract.portfolio.village.exception;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class DuplicateVillageNameException extends AbstractPlatformDomainRuleException{
    public DuplicateVillageNameException(String VillageName){
        super("error.msg.villageName.already.exists", 
                "Village Name already exists", VillageName);
    }    
}
