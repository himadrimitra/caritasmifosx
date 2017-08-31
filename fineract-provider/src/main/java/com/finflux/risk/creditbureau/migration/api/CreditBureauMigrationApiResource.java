package com.finflux.risk.creditbureau.migration.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import com.finflux.risk.creditbureau.migration.service.CreditBureauMigrationService;

@Path("/creditbureau/migration")
@Component
@Scope("singleton")
public class CreditBureauMigrationApiResource {
    
    private final String AUTHORIZEDUSER = "conflux1" ;
    private final CreditBureauMigrationService migrationService ;
    private final PlatformSecurityContext platformSecurityContext ;
    
    @Autowired
    public CreditBureauMigrationApiResource(final CreditBureauMigrationService migrationService,
            final PlatformSecurityContext platformSecurityContext) {
        this.migrationService = migrationService ;
        this.platformSecurityContext = platformSecurityContext ;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String doAction(final String apiRequestBodyAsJson) {
        final AppUser user = this.platformSecurityContext.authenticatedUser() ;
        if(user == null || !AUTHORIZEDUSER.equalsIgnoreCase(user.getUsername())) {
            throw new BadCredentialsException("Unauthorized user to access this API") ; 
        }
        migrationService.updateCreditBureauEnquiry();
        migrationService.updateLoanCreditBureauEnquiry();
        return "SUCCESS";
    }

}
