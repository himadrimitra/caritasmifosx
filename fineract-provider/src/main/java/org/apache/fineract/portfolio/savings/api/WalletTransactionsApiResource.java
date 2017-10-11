package org.apache.fineract.portfolio.savings.api;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/wallet/{mobilenumber}")
@Component
@Scope("singleton")
public class WalletTransactionsApiResource {
	private final ClientReadPlatformService clientReadPlatformService;
	private final SavingsAccountTransactionsApiResource savingsAccountTransactionsApiResource;
	private final SavingsAccountsApiResource savingsAccountsApiResource;

	@Autowired
	public WalletTransactionsApiResource(
			final SavingsAccountTransactionsApiResource savingsAccountTransactionsApiResource,
			final PlatformSecurityContext context,
			final DefaultToApiJsonSerializer<SavingsAccountTransactionData> toApiJsonSerializer,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final ClientReadPlatformService clientReadPlatformService,
			final SavingsAccountsApiResource savingsAccountsApiResource) {
		this.clientReadPlatformService = clientReadPlatformService;
		this.savingsAccountTransactionsApiResource = savingsAccountTransactionsApiResource;
		this.savingsAccountsApiResource = savingsAccountsApiResource;
	}

	private boolean is(final String commandParam, final String commandValue) {
		return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String transaction(@PathParam("mobilenumber") final String mobileno,
			@QueryParam("command") final String commandParam, final String apiRequestBodyAsJson) {

		Long savingsId = this.clientReadPlatformService.retrieveSavingsAccountIdByMobileNo(mobileno);
		if (savingsId == null) {

			throw new SavingsIdNotFoundException(savingsId);
		}
		String result = null;
		if (is(commandParam, "deposit")) {
			result = this.savingsAccountTransactionsApiResource.transaction(savingsId, commandParam,
					apiRequestBodyAsJson);
		} else if (is(commandParam, "withdrawal")) {
			result = this.savingsAccountTransactionsApiResource.transaction(savingsId, commandParam,
					apiRequestBodyAsJson);
		}

		return result;

	}

        @GET
        @Path("/transactions")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveAll(@PathParam("mobilenumber") final String mobileno,
                @QueryParam("searchConditions") final String searchConditions,
                @QueryParam("transactionsCount") final Integer transactionsCount, @QueryParam("fromDate") final Date fromDate,
                @QueryParam("toDate") final Date toDate, @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
                @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder, @Context final UriInfo uriInfo) {
            Long savingsId = this.clientReadPlatformService.retrieveSavingsAccountIdByMobileNo(mobileno);
            if (savingsId == null) {
                throw new SavingsIdNotFoundException(savingsId); 
            }
            return this.savingsAccountTransactionsApiResource.retrieveAll(savingsId, searchConditions, transactionsCount, fromDate, toDate,
                    offset, limit, orderBy, sortOrder, uriInfo);
        }

        @GET
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveOne(@PathParam("mobilenumber") final String mobileno,
                @DefaultValue("false") @QueryParam("staffInSelectedOfficeOnly") final boolean staffInSelectedOfficeOnly,
                @DefaultValue("all") @QueryParam("chargeStatus") final String chargeStatus, @Context final UriInfo uriInfo,
                @QueryParam("transactionsCount") final Integer transactionsCount, @QueryParam("fromDate") final Date fromDate,
                @QueryParam("toDate") final Date toDate, @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
                @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder,
                @QueryParam("searchConditions") final String searchConditions) {
            Long savingsId = this.clientReadPlatformService.retrieveSavingsAccountIdByMobileNo(mobileno);
            if (savingsId == null) {
                throw new SavingsIdNotFoundException(savingsId); 
            }
            return this.savingsAccountsApiResource.retrieveOne(savingsId, staffInSelectedOfficeOnly, chargeStatus, uriInfo, transactionsCount,
                    fromDate, toDate, offset, limit, orderBy, sortOrder, searchConditions);
        }
}
