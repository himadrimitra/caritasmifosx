package com.finflux.transaction.execution.api;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.commands.service.CommandWrapperBuilder;
import com.finflux.transaction.execution.data.BankTransactionDetail;
import com.finflux.transaction.execution.data.BankTransactionEntityType;
import com.finflux.transaction.execution.service.BankTransactionService;

/**
 * Created by dhirendra on 15/09/16.
 */
@Path("/banktransaction")
@Component
@Scope("singleton")
public class BankTransactionApiResource {

    private final String resourceNameForPermissions = "BANK_TRANSACTION";
    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final BankTransactionService bankTransactionService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;

    @Autowired
    public BankTransactionApiResource(final PlatformSecurityContext context,
                                    final DefaultToApiJsonSerializer toApiJsonSerializer,
                                    final BankTransactionService bankTransactionService,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                                      final ApiRequestParameterHelper apiRequestParameterHelper) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.bankTransactionService = bankTransactionService;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;

     }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getTransactionsByEntity(@QueryParam("entityType") final String entityType,
                               @QueryParam("entityId") final Long entityId,
                               @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        BankTransactionEntityType transferEntityType = BankTransactionEntityType.getEntityType(entityType);
        final List<BankTransactionDetail> transactions = this.bankTransactionService.getAllTransaction(transferEntityType,entityId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, transactions);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOneTransaction(@PathParam("transactionId") final Long transactionId,
                                          @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final BankTransactionDetail transaction = this.bankTransactionService.getTransactionDetail(transactionId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, transaction);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{transactionId}/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTransactionTemplate(@PathParam("transactionId") final Long transactionId,
                                         @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final BankTransactionDetail transaction = this.bankTransactionService.getTransactionDetail(transactionId);
        List<EnumOptionData> supportedTransfers = this.bankTransactionService.geetSupportedTransfers(transactionId);
        transaction.setSupportedTransferTypes(supportedTransfers);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, transaction);
    }

    @SuppressWarnings("unchecked")
    @POST
    @Path("{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String doAction(@PathParam("transactionId") final Long transactionId,
            @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {
        String jsonApiRequest = apiRequestBodyAsJson;
        if (StringUtils.isBlank(jsonApiRequest)) {
            jsonApiRequest = "{}";
        }

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(jsonApiRequest);
        CommandProcessingResult result = null;
        if (is(commandParam, "initiate")) {
            CommandWrapper commandRequest = builder.initiateBankTransaction(transactionId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "submit")) {
            CommandWrapper commandRequest = builder.submitBankTransaction(transactionId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "retry")) {
            CommandWrapper commandRequest = builder.retryBankTransaction(transactionId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, BankTransactionApiConstants.rejectCommandParam)) {
            CommandWrapper commandRequest = builder.rejectBankTransaction(transactionId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }  else if (is(commandParam, BankTransactionApiConstants.CLOSE_COMMAND_PARAM)) {
            CommandWrapper commandRequest = builder.closeBankTransaction(transactionId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } 
        if (result == null) { throw new UnrecognizedQueryParamException("command", commandParam,
                new Object[] { "activate", "deactivate" }); }
        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

}
