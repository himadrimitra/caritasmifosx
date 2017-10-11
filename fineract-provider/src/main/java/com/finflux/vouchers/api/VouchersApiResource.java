package com.finflux.vouchers.api;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.vouchers.data.VoucherData;
import com.finflux.vouchers.exception.InvalidVoucherCommandQueryParamException;
import com.finflux.vouchers.service.VoucherReadPlatformService;

@Component
@Scope("singleton")
@Path("/vouchers")
public class VouchersApiResource {

    private final Set<String> VOUCHER_PARAMS = new HashSet<>(Arrays.asList("voucherId", "voucherNumber", "voucherType", "journalEntryData",
            "templateData"));

    private final PlatformSecurityContext platformSecurityContext;
    private final DefaultToApiJsonSerializer<VoucherData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final VoucherReadPlatformService voucherReadPlatformService;

    @Autowired
    public VouchersApiResource(final PlatformSecurityContext platformSecurityContext,
            final DefaultToApiJsonSerializer<VoucherData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper, final VoucherReadPlatformService voucherReadPlatformService) {
        this.platformSecurityContext = platformSecurityContext;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.voucherReadPlatformService = voucherReadPlatformService;
    }

    // Voucher Bill copies should be taken
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createVouchers(@QueryParam("voucherType") final String voucherType, final String apiRequestBodyAsJson) {
        this.platformSecurityContext.authenticatedUser();
        CommandWrapper commandWrapper = new CommandWrapperBuilder().createVoucher(voucherType).withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult commandProcessingResult = this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);
        return this.toApiJsonSerializer.serialize(commandProcessingResult);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@QueryParam("voucherType") final String voucherType, @Context final UriInfo uriInfo) {
        this.platformSecurityContext.authenticatedUser();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final VoucherData templateData = this.voucherReadPlatformService.retrieveVoucheTemplate(voucherType);
        return this.toApiJsonSerializer.serialize(settings, templateData);
    }

    @GET
    @Path("{voucherId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@QueryParam("voucherType") final String voucherType, @PathParam("voucherId") final Long voucherId, @Context final UriInfo uriInfo) {
        this.platformSecurityContext.authenticatedUser();
        VoucherData voucherData = this.voucherReadPlatformService.retrieveOne(voucherType, voucherId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, voucherData, this.VOUCHER_PARAMS);
    }

    @GET
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("voucherType") final String voucherType,
            @QueryParam("fromDate") final Date fromDate, @QueryParam("toDate") final Date toDate,
            @QueryParam("officeId") final Long officeId, @QueryParam("voucherNumber") final String voucherNumber,
            @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit) {
        this.platformSecurityContext.authenticatedUser();
        SearchParameters searchParams = SearchParameters.forVouchers(officeId, voucherType, voucherNumber, fromDate, toDate, offset, limit);
        Page<VoucherData> page = this.voucherReadPlatformService.retrieveVouchers(searchParams);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, page, VOUCHER_PARAMS);
    }

    @PUT
    @Path("{voucherId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateVoucher(@QueryParam("voucherType") final String voucherType, @QueryParam("command") final String command, 
            @PathParam("voucherId") final Long voucherId, final String apiRequestBodyAsJson) {
        this.platformSecurityContext.authenticatedUser();
        if("UpdateVoucher".equals(command)) {
            CommandWrapper commandWrapper = new CommandWrapperBuilder().updateVoucher(voucherType, voucherId).withJson(apiRequestBodyAsJson).build();
            final CommandProcessingResult commandProcessingResult = this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);
            return this.toApiJsonSerializer.serialize(commandProcessingResult);    
        }else if("ReverseVoucher".equals(command)){
            CommandWrapper commandWrapper = new CommandWrapperBuilder().reverseVoucher(voucherType, voucherId).withJson(apiRequestBodyAsJson).build();
            final CommandProcessingResult commandProcessingResult = this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);
            return this.toApiJsonSerializer.serialize(commandProcessingResult); 
        }
        
        throw new InvalidVoucherCommandQueryParamException(command) ;
    }
}
