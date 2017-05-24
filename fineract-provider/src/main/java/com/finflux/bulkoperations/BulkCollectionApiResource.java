package com.finflux.bulkoperations;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.documentmanagement.data.DocumentData;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentReadPlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.commands.service.CommandWrapperBuilder;
import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bankstatement.data.BankStatementData;
import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;
import com.finflux.reconcilation.bankstatement.service.BankStatementReadPlatformService;
import com.sun.jersey.multipart.FormDataMultiPart;

@Path("/bulkcollection")
@Component
@Scope("singleton")
public class BulkCollectionApiResource {

    private final ToApiJsonSerializer<DocumentData> toDocumentDataApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<BankStatementData> toApiJsonSerializer;
    private final DocumentReadPlatformService documentReadPlatformService;
    private final BankStatementReadPlatformService bankStatementReadPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ToApiJsonSerializer<BankStatementDetailsData> toBankStatementDetailsApiJsonSerializer;
    private final BulkCollectionWritePlatformService bulkCollectionWritePlatformService;

    @Autowired
    public BulkCollectionApiResource(final ApiRequestParameterHelper apiRequestParameterHelper, final PlatformSecurityContext context,
            final ToApiJsonSerializer<BankStatementData> toApiJsonSerializer, final DocumentReadPlatformService documentReadPlatformService,
            final BankStatementReadPlatformService bankStatementReadPlatformService,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ToApiJsonSerializer<DocumentData> toDocumentDataApiJsonSerializer,
            ToApiJsonSerializer<BankStatementDetailsData> toBankStatementDetailsApiJsonSerializer,
            final BulkCollectionWritePlatformService bulkCollectionWritePlatformService) {

        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.documentReadPlatformService = documentReadPlatformService;
        this.bankStatementReadPlatformService = bankStatementReadPlatformService;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.toDocumentDataApiJsonSerializer = toDocumentDataApiJsonSerializer;
        this.toBankStatementDetailsApiJsonSerializer = toBankStatementDetailsApiJsonSerializer;
        this.bulkCollectionWritePlatformService = bulkCollectionWritePlatformService;
    }

    @POST
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createBankStatement(FormDataMultiPart formParams) throws InvalidFormatException, IOException {

        final Long bankStatementId = this.bulkCollectionWritePlatformService.createBulkTransactionStatement(formParams);

        return this.toApiJsonSerializer.serialize(CommandProcessingResult.resourceResult(bankStatementId, null));
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveAllBulkStatements(@Context final UriInfo uriInfo, @QueryParam("statementType") final Integer statementType,
            @QueryParam("processed") final Boolean isProcessed,@QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
            @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder) {

        this.context.authenticatedUser().validateHasReadPermission(ReconciliationApiConstants.BANK_STATEMENT_RESOURCE_NAME);
        final SearchParameters searchParameters = SearchParameters.forPagination(offset, limit, orderBy, sortOrder);
        final Page<BankStatementData> bankStatementData = this.bankStatementReadPlatformService
                .retrieveAllBankStatements(statementType, isProcessed, searchParameters);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, bankStatementData);
    }

    @POST
    @Path("{bankStatementId}/generatetransactions")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String generatePortfolioTransactions(@PathParam("bankStatementId") final Long bankStatementId,
            final String apiRequestBodyAsJson) {

        this.context.authenticatedUser();

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        final CommandWrapper commandRequest = builder.completePortfolioTransactions(bankStatementId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest,false);

        return this.toBankStatementDetailsApiJsonSerializer.serialize(result);

    }

}
