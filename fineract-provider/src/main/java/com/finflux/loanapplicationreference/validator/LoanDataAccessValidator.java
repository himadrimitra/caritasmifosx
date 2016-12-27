package com.finflux.loanapplicationreference.validator;

import org.apache.fineract.commands.annotation.CommandValidationType;
import org.apache.fineract.commands.validator.CommandSourceValidator;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.exception.NoAuthorizationException;
import org.apache.fineract.portfolio.client.service.AppUserClientAssociationService;
import org.apache.fineract.portfolio.group.service.AppUserGroupAssociationService;
import org.apache.fineract.portfolio.loanaccount.service.AppUserLoanAssociationService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Component
@CommandValidationType(entity = "LOAN")
public class LoanDataAccessValidator implements CommandSourceValidator {

    private final AppUserLoanAssociationService appUserLoanAssociationService;
    private final FromJsonHelper fromApiJsonHelper;
    private final AppUserClientAssociationService appUserClientAssociationService;
    private final AppUserGroupAssociationService appUserGroupAssociationService;

    @Autowired
    public LoanDataAccessValidator(final AppUserLoanAssociationService appUserLoanAssociationService,
            final FromJsonHelper fromApiJsonHelper, final AppUserClientAssociationService appUserClientAssociationService,
            final AppUserGroupAssociationService appUserGroupAssociationService) {
        this.appUserLoanAssociationService = appUserLoanAssociationService;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.appUserClientAssociationService = appUserClientAssociationService;
        this.appUserGroupAssociationService = appUserGroupAssociationService;
    }

    @Override
    public void validate(JsonCommand command, AppUser appUser) {
        Long loanId = null;
        loanId = command.getLoanId();
        if (loanId == null) {
            loanId = command.entityId();
        }
        boolean hasAccess = true;
        if (loanId == null) {
            // Loan creation
            JsonElement element = command.parsedJson();
            final Long clientId = this.fromApiJsonHelper.extractLongNamed("clientId", element);
            final Long groupId = this.fromApiJsonHelper.extractLongNamed("groupId", element);
            if (groupId == null) {
                hasAccess = this.appUserClientAssociationService.hasAccessToClient(clientId, appUser.getId());
            } else {
                hasAccess = this.appUserGroupAssociationService.hasAccessToGroup(groupId, appUser.getId());
            }

        } else {
            hasAccess = this.appUserLoanAssociationService.hasAccessToLoan(loanId, appUser.getId());
        }

        if (!hasAccess) {
            final String authorizationMessage = "User has no authority to modify Loan with id: " + loanId;
            throw new NoAuthorizationException(authorizationMessage);
        }
    }

}
