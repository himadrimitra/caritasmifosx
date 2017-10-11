package com.finflux.risk.creditbureau.migration.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
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
    private ExecutorService executorService ;
    
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
        this.executorService = Executors.newSingleThreadExecutor() ;
        //Doing the migration in new thread as HTTP client will sent another post if response is delayed.
        this.executorService.execute(new MigrationThread(ThreadLocalContextUtil.getTenant(), this.migrationService));
        return "SUCCESS";
    }

    class MigrationThread implements Runnable {
        
        final FineractPlatformTenant tenant ;
        final CreditBureauMigrationService migrationService ;
        
        
        public MigrationThread(final FineractPlatformTenant tenant, final CreditBureauMigrationService migrationService) {
           this.tenant = tenant ;
           this.migrationService = migrationService ;
        }
        
        @Override
        public void run() {
            ThreadLocalContextUtil.setTenant(this.tenant);
            this.migrationService.updateCreditBureauEnquiry();
            this.migrationService.updateLoanCreditBureauEnquiry();
        }
        
    }
}
