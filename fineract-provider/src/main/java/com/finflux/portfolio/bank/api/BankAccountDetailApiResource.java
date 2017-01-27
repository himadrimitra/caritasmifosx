
package com.finflux.portfolio.bank.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.portfolio.bank.domain.BankAccountDetailEntityType;
import com.finflux.portfolio.bank.exception.BankAccountDetailEntityTypeNotSupportedException;
import com.finflux.portfolio.bank.service.BankAccountDetailsReadService;

@Path("/{entityType}/{entityId}/bankaccountdetail")
@Component
@Scope("singleton")
public class BankAccountDetailApiResource {

    private final PlatformSecurityContext context;
    private final BankAccountDetailsReadService readPlatformService;
    private final DefaultToApiJsonSerializer<BankAccountDetailData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public BankAccountDetailApiResource(final PlatformSecurityContext context, final BankAccountDetailsReadService readPlatformService,
            final DefaultToApiJsonSerializer<BankAccountDetailData> toApiJsonSerializer,
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
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveBankAccountDetailsByEntity(@PathParam("entityType") final String entityType,
            @PathParam("entityId") final Long entityId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(BankAccountDetailConstants.resourceNameForPermission);

        final BankAccountDetailEntityType bankEntityType = BankAccountDetailEntityType.getEntityType(entityType);
        if (bankEntityType == null) { throw new BankAccountDetailEntityTypeNotSupportedException(entityType); }

        BankAccountDetailData bankAccountDetailData = this.readPlatformService.retrieveOneBy(bankEntityType, entityId);
        if(bankAccountDetailData == null){
            bankAccountDetailData = new BankAccountDetailData(readPlatformService.bankAccountTypeOptions());
        } else {
            bankAccountDetailData.setBankAccountTypeOptions(readPlatformService.bankAccountTypeOptions());
        }
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, bankAccountDetailData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createBankAccountDetails(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            final String apiRequestBodyAsJson) {

        final BankAccountDetailEntityType bankEntityType = BankAccountDetailEntityType.getEntityType(entityType);
        if (bankEntityType == null) { throw new BankAccountDetailEntityTypeNotSupportedException(entityType); }

        final CommandWrapper commandRequest = new CommandWrapperBuilder()
                .createBankAccountDetail(entityType, entityId, bankEntityType.getValue()).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);

    }

    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateBankAccountDetails(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            final String jsonRequestBody) {

        final BankAccountDetailEntityType bankEntityType = BankAccountDetailEntityType.getEntityType(entityType);
        if (bankEntityType == null) { throw new BankAccountDetailEntityTypeNotSupportedException(entityType); }
        // check permission for updating the future meeting dates
        final CommandWrapper commandRequest = new CommandWrapperBuilder()
                .updateBankAccountDetail(entityType, entityId, bankEntityType.getValue()).withJson(jsonRequestBody).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteBankAccountDetails(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId) {

        final BankAccountDetailEntityType bankEntityType = BankAccountDetailEntityType.getEntityType(entityType);
        if (bankEntityType == null) { throw new BankAccountDetailEntityTypeNotSupportedException(entityType); }

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteBankAccountDetail(entityType, entityId,
                bankEntityType.getValue()).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

}