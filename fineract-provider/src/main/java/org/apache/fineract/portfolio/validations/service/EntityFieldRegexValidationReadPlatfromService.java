package org.apache.fineract.portfolio.validations.service;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.portfolio.validations.data.EntityFieldRegexValidation;

public interface EntityFieldRegexValidationReadPlatfromService {

	Collection<EntityFieldRegexValidation> retrieveEntityFieldType(Integer entityType);

}
