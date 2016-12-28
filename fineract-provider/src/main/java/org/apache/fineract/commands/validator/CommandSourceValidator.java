
package org.apache.fineract.commands.validator;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.useradministration.domain.AppUser;

public interface CommandSourceValidator {

    void validate(JsonCommand command, AppUser appUser);
}