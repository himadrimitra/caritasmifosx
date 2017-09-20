package org.apache.fineract.portfolio.loanaccount.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.fineract.accounting.journalentry.api.DateParam;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueChargeData;
import org.apache.fineract.portfolio.loanaccount.service.LoanCalculationReadService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/loans/overduecharges")
@Component
@Scope("singleton")
public class LoanOverdueChargeApiResource {

    private final String resourceNameForPermission = "OVERDUECHARGE";
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final DefaultToApiJsonSerializer<LoanOverdueChargeData> apiJsonSerializerService;
    private final LoanCalculationReadService loanCalculationReadService;
    private final PlatformSecurityContext context;

    @Autowired
    public LoanOverdueChargeApiResource(final DefaultToApiJsonSerializer<LoanOverdueChargeData> apiJsonSerializerService,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final LoanCalculationReadService loanCalculationReadService, final PlatformSecurityContext context) {
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiJsonSerializerService = apiJsonSerializerService;
        this.loanCalculationReadService = loanCalculationReadService;
        this.context = context;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String calculateLoanOverdueDetails(@QueryParam("loanId") final Long loanId, @QueryParam("date") final DateParam date,
            @QueryParam("locale") final String locale, @QueryParam("dateFormat") final String dateFormat) {
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermission);
        LoanOverdueChargeData loanOverdueChargeData = null;
        if (loanId != null) {
            LocalDate fromDate = null;
            if (date != null) {
                fromDate = new LocalDate(date.getDate("date", dateFormat, locale));
            }
            loanOverdueChargeData = this.loanCalculationReadService.retrieveLoanOverdueChargeDetailAsOnDate(loanId, fromDate);
        }
        return this.apiJsonSerializerService.serialize(loanOverdueChargeData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String executeOverdueChargeJob(final String jsonRequestBody) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().executeOverdueCharges().withJson(jsonRequestBody).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest, false);

        return this.apiJsonSerializerService.serialize(result);
    }

}