
package com.finflux.portfolio.bank.api;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.portfolio.bank.domain.BankAccountDetailEntityType;
import com.finflux.portfolio.bank.exception.BankAccountDetailEntityTypeNotSupportedException;
import com.finflux.portfolio.bank.service.BankAccountDetailsReadService;
import com.finflux.task.data.TaskExecutionData;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path("/{entityType}/{entityId}/bankaccountdetail")
@Component
@Scope("singleton")
public class BankAccountDetailApiResource {

    private final PlatformSecurityContext context;
    private final BankAccountDetailsReadService readPlatformService;
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public BankAccountDetailApiResource(final PlatformSecurityContext context,
            final BankAccountDetailsReadService readPlatformService,
            final DefaultToApiJsonSerializer toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    /**
     * @param entityType
     * @param entityId
     * @param uriInfo
     * @return
     */
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveBankAccountDetailsByEntity(@PathParam("entityType") final String entityType,
            @PathParam("entityId") final Long entityId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser()
                .validateHasReadPermission(BankAccountDetailConstants.resourceNameForPermission);

        final BankAccountDetailEntityType bankEntityType = getBankAccountDetailEntityType(entityType);

        BankAccountDetailData bankAccountDetailData = this.readPlatformService.retrieveOneBy(bankEntityType, entityId);
        if (bankAccountDetailData == null) {
            bankAccountDetailData = new BankAccountDetailData(readPlatformService.bankAccountTypeOptions());
        } else {
            bankAccountDetailData.setBankAccountTypeOptions(readPlatformService.bankAccountTypeOptions());
        }
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
                .process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, bankAccountDetailData);
    }

    @GET
    @Path("workflow")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveBankAccountWorkflowByEntity(@PathParam("entityType") final String entityType,
            @PathParam("entityId") final Long entityId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser()
                .validateHasReadPermission(BankAccountDetailConstants.resourceNameForPermission);
        final BankAccountDetailEntityType bankEntityType = getBankAccountDetailEntityType(entityType);
        TaskExecutionData taskExecutionData = readPlatformService.createOrFetchBankAccountWorkflow(bankEntityType,
                entityId);
        return this.toApiJsonSerializer.serialize(taskExecutionData);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String createBankAccountDetails(@PathParam("entityType") final String entityType,
            @PathParam("entityId") final Long entityId, final String apiRequestBodyAsJson) {

        final BankAccountDetailEntityType bankEntityType = getBankAccountDetailEntityType(entityType);

        final CommandWrapper commandRequest = new CommandWrapperBuilder()
                .createBankAccountDetail(entityType, entityId, bankEntityType.getValue()).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);

    }

    private BankAccountDetailEntityType getBankAccountDetailEntityType(@PathParam("entityType") String entityType) {
        final BankAccountDetailEntityType bankEntityType = BankAccountDetailEntityType.getEntityType(entityType);
        if (bankEntityType == null) {
            throw new BankAccountDetailEntityTypeNotSupportedException(entityType);
        }
        return bankEntityType;
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String updateBankAccountDetails(@PathParam("entityType") final String entityType,
            @PathParam("entityId") final Long entityId, final String jsonRequestBody) {

        final BankAccountDetailEntityType bankEntityType = getBankAccountDetailEntityType(entityType);
        // check permission for updating the future meeting dates
        final CommandWrapper commandRequest = new CommandWrapperBuilder()
                .updateBankAccountDetail(entityType, entityId, bankEntityType.getValue()).withJson(jsonRequestBody)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String deleteBankAccountDetails(@PathParam("entityType") final String entityType,
            @PathParam("entityId") final Long entityId) {

        final BankAccountDetailEntityType bankEntityType = getBankAccountDetailEntityType(entityType);

        final CommandWrapper commandRequest = new CommandWrapperBuilder()
                .deleteBankAccountDetail(entityType, entityId, bankEntityType.getValue()).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("action")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String doAction(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @QueryParam("command") final String commandParam, final String jsonRequestBody) {
        String jsonApiRequest = jsonRequestBody;
        if (StringUtils.isBlank(jsonApiRequest)) {
            jsonApiRequest = "{}";
        }
        final BankAccountDetailEntityType bankEntityType = getBankAccountDetailEntityType(entityType);

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(jsonApiRequest);

        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, "activate")) {
            commandRequest = builder.activateBankAccountDetail(entityType, entityId, bankEntityType.getValue()).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "updateCheckerInfo")) {
            commandRequest = builder.updateCheckerInfoBankAccountDetail(entityType, entityId, bankEntityType.getValue())
                    .withJson(jsonRequestBody).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        }
        if (result == null) {
            throw new UnrecognizedQueryParamException("command", commandParam,
                    new Object[]{"activate", "updateCheckerInfo"});
        }
        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

}