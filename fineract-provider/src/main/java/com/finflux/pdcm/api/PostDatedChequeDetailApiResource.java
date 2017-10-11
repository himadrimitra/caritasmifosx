package com.finflux.pdcm.api;

import java.util.Collection;
import java.util.HashSet;

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

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.api.JsonQuery;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.common.exception.EntityTypeNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.commands.service.CommandWrapperBuilder;
import com.finflux.pdcm.constants.PostDatedChequeDetailApiConstants;
import com.finflux.pdcm.data.PostDatedChequeDetailData;
import com.finflux.pdcm.data.PostDatedChequeDetailSearchTemplateData;
import com.finflux.pdcm.data.PostDatedChequeDetailTemplateData;
import com.finflux.pdcm.service.PostDatedChequeDetailReadPlatformService;
import com.google.gson.JsonElement;

@Path(PostDatedChequeDetailApiConstants.parentPathUrl)
@Component
@Scope("singleton")
public class PostDatedChequeDetailApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PostDatedChequeDetailReadPlatformService readPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final FromJsonHelper fromJsonHelper;

    @SuppressWarnings("rawtypes")
    @Autowired
    public PostDatedChequeDetailApiResource(final PlatformSecurityContext context,
            final ApiRequestParameterHelper apiRequestParameterHelper, final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PostDatedChequeDetailReadPlatformService readPlatformService,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, final FromJsonHelper fromJsonHelper) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.readPlatformService = readPlatformService;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.fromJsonHelper = fromJsonHelper;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path(PostDatedChequeDetailApiConstants.templatePath)
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@PathParam(PostDatedChequeDetailApiConstants.entityTypeParam) final String entityType,
            @PathParam(PostDatedChequeDetailApiConstants.entityIdParam) final Long entityId, @Context final UriInfo uriInfo) {

        final Integer entityTypeId = validateEntityTypeAndGetEntityTypeId(entityType);

        this.context.authenticatedUser().validateHasReadPermission(PostDatedChequeDetailApiConstants.RESOURCE_NAME);

        final PostDatedChequeDetailTemplateData templateData = this.readPlatformService.template(entityTypeId, entityId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, templateData);
    }

    @POST
    @Path(PostDatedChequeDetailApiConstants.createPathUrl)
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(@PathParam(PostDatedChequeDetailApiConstants.entityTypeParam) final String entityType,
            @PathParam(PostDatedChequeDetailApiConstants.entityIdParam) final Long entityId, final String apiRequestBodyAsJson) {

        final Integer entityTypeId = validateEntityTypeAndGetEntityTypeId(entityType);

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createPDC(entityTypeId, entityType, entityId)
                .withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    private Integer validateEntityTypeAndGetEntityTypeId(final String entityType) {
        final EntityType entityTypeEnum = EntityType.getEntityType(entityType);
        if (entityTypeEnum == null || entityTypeEnum.isInvalid()) { throw new EntityTypeNotSupportedException(entityType); }
        return entityTypeEnum.getValue();
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path(PostDatedChequeDetailApiConstants.retrieveAllPathUrl)
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@PathParam(PostDatedChequeDetailApiConstants.entityTypeParam) final String entityType,
            @PathParam(PostDatedChequeDetailApiConstants.entityIdParam) final Long entityId, @Context final UriInfo uriInfo) {

        final Integer entityTypeId = validateEntityTypeAndGetEntityTypeId(entityType);

        this.context.authenticatedUser().validateHasReadPermission(PostDatedChequeDetailApiConstants.RESOURCE_NAME);

        final Collection<PostDatedChequeDetailData> postDatedChequeDetailDatas = this.readPlatformService.retrieveAll(entityTypeId,
                entityId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, postDatedChequeDetailDatas);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path(PostDatedChequeDetailApiConstants.retrieveOnePathUrl)
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam(PostDatedChequeDetailApiConstants.pdcIdParam) final Long pdcId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(PostDatedChequeDetailApiConstants.RESOURCE_NAME);

        final PostDatedChequeDetailData postDatedChequeDetailData = this.readPlatformService.retrieveOne(pdcId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, postDatedChequeDetailData);
    }

    @PUT
    @Path(PostDatedChequeDetailApiConstants.updatePathUrl)
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam(PostDatedChequeDetailApiConstants.pdcIdParam) final Long pdcId,
            @QueryParam(PostDatedChequeDetailApiConstants.commandParam) final String commandParam, final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, PostDatedChequeDetailApiConstants.deleteCommandParam)) {
            commandRequest = builder.deletePDC(pdcId).build();
        } else {
            commandRequest = builder.updatePDC(pdcId).build();
        }
        result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path(PostDatedChequeDetailApiConstants.searchTemplatePath)
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveSearchTemplate(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(PostDatedChequeDetailApiConstants.RESOURCE_NAME);

        final PostDatedChequeDetailSearchTemplateData searchTemplateData = this.readPlatformService.searchTemplate();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, searchTemplateData);
    }

    @SuppressWarnings("unchecked")
    @POST
    @Path(PostDatedChequeDetailApiConstants.searchPDCUrl)
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String searchPDC(@Context final UriInfo uriInfo, final String apiRequestBodyAsJson) {
        final JsonElement parsedQuery = this.fromJsonHelper.parse(apiRequestBodyAsJson);
        final JsonQuery query = JsonQuery.from(apiRequestBodyAsJson, parsedQuery, this.fromJsonHelper);
        final Collection<PostDatedChequeDetailData> postDatedChequeDetailDatas = this.readPlatformService.searchPDC(query);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, postDatedChequeDetailDatas, new HashSet<String>());
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String action(@QueryParam(PostDatedChequeDetailApiConstants.commandParam) final String commandParam,
            final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, PostDatedChequeDetailApiConstants.presentCommandParam)) {
            commandRequest = builder.presentPDC().build();
        } else if (is(commandParam, PostDatedChequeDetailApiConstants.bouncedCommandParam)) {
            commandRequest = builder.bouncedPDC().build();
        } else if (is(commandParam, PostDatedChequeDetailApiConstants.clearCommandParam)) {
            commandRequest = builder.clearPDC().build();
        } else if (is(commandParam, PostDatedChequeDetailApiConstants.cancelCommandParam)) {
            commandRequest = builder.cancelPDC().build();
        } else if (is(commandParam, PostDatedChequeDetailApiConstants.returnCommandParam)) {
            commandRequest = builder.returnPDC().build();
        } else if (is(commandParam, PostDatedChequeDetailApiConstants.undoCommandParam)) {
            commandRequest = builder.undoPDC().build();
        } else {
            return null;
        }
        final boolean isTransactionalScopeRequiredInprocessAndLogCommand = false;
        result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest,
                isTransactionalScopeRequiredInprocessAndLogCommand);
        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

}