/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.dataqueries.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.dataqueries.data.DatatableData;
import org.apache.fineract.infrastructure.dataqueries.data.GenericResultsetData;
import org.apache.fineract.infrastructure.dataqueries.data.ScopeOptionsData;
import org.apache.fineract.infrastructure.dataqueries.service.GenericDataService;
import org.apache.fineract.infrastructure.dataqueries.service.ReadWriteNonCoreDataService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

//import org.slf4j.Logger;

@Path("/datatables")
@Component
@Scope("singleton")
public class DatatablesApiResource {

    private final PlatformSecurityContext context;
    private final GenericDataService genericDataService;
    private final ReadWriteNonCoreDataService readWriteNonCoreDataService;
    private final ToApiJsonSerializer<GenericResultsetData> toApiJsonSerializer;
    private final ToApiJsonSerializer<ScopeOptionsData> scopeOptionsDataApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(DatatablesApiResource.class);

    @Autowired
    public DatatablesApiResource(final PlatformSecurityContext context, final GenericDataService genericDataService,
            final ReadWriteNonCoreDataService readWriteNonCoreDataService,
            final ToApiJsonSerializer<GenericResultsetData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final ToApiJsonSerializer<ScopeOptionsData> scopeOptionsDataApiJsonSerializer) {
        this.context = context;
        this.genericDataService = genericDataService;
        this.readWriteNonCoreDataService = readWriteNonCoreDataService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.scopeOptionsDataApiJsonSerializer = scopeOptionsDataApiJsonSerializer;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getDatatables(@QueryParam("apptable") final String apptable, @Context final UriInfo uriInfo, 
            @QueryParam("associatedEntityId") final Long associatedEntityId) {

        final List<DatatableData> result = this.readWriteNonCoreDataService.retrieveDatatableNames(apptable, associatedEntityId);

        final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serializePretty(prettyPrint, result);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createDatatable(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createDBDatatable(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{datatableName}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateDatatable(@PathParam("datatableName") final String datatableName, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateDBDatatable(datatableName, apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{datatableName}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteDatatable(@PathParam("datatableName") final String datatableName, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteDBDatatable(datatableName, apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("register/{datatable}/{apptable}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String registerDatatable(@PathParam("datatable") final String datatable, @PathParam("apptable") final String apptable,
            final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().registerDBDatatable(datatable, apptable)
                .withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("deregister/{datatable}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deregisterDatatable(@PathParam("datatable") final String datatable) {

        this.readWriteNonCoreDataService.deregisterDatatable(datatable);

        final CommandProcessingResult result = new CommandProcessingResultBuilder().withResourceIdAsString(datatable).build();

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{datatable}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getDatatable(@PathParam("datatable") final String datatable, @Context final UriInfo uriInfo) {

        final DatatableData result = this.readWriteNonCoreDataService.retrieveDatatable(datatable);

        final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serializePretty(prettyPrint, result);
    }

    @GET
    @Path("{datatable}/{apptableIdentifier}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getDatatable(@PathParam("datatable") final String datatable, @PathParam("apptableIdentifier") final String apptableIdentifier,
            @QueryParam("order") final String order, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasDatatableReadPermission(datatable);

        final GenericResultsetData results = this.readWriteNonCoreDataService.retrieveDataTableGenericResultSet(datatable, apptableIdentifier,
                order, null);

        String json = "";
        final boolean genericResultSet = ApiParameterHelper.genericResultSet(uriInfo.getQueryParameters());
        if (genericResultSet) {
            final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
            json = this.toApiJsonSerializer.serializePretty(prettyPrint, results);
        } else {
            json = this.genericDataService.generateJsonFromGenericResultsetData(results);
        }

        return json;
    }
    
    @GET
    @Path("{datatable}/{apptableIdentifier}/{datatableId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getDatatableManyEntry(@PathParam("datatable") final String datatable, @PathParam("apptableIdentifier") final String apptableIdentifier,
            @PathParam("datatableId") final Long datatableId, @QueryParam("order") final String order, @Context final UriInfo uriInfo) {

        logger.debug("::1 we came in the getDatatbleManyEntry apiRessource method");

        this.context.authenticatedUser().validateHasDatatableReadPermission(datatable);
        final GenericResultsetData results = this.readWriteNonCoreDataService.retrieveDataTableGenericResultSet(datatable, apptableIdentifier,
                order, datatableId);

        String json = "";
        final boolean genericResultSet = ApiParameterHelper.genericResultSet(uriInfo.getQueryParameters());
        if (genericResultSet) {
            final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
            json = this.toApiJsonSerializer.serializePretty(prettyPrint, results);
        } else {
            json = this.genericDataService.generateJsonFromGenericResultsetData(results);
        }

        return json;
    }
    
    @POST
    @Path("{datatable}/{apptableIdentifier}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createDatatableEntry(@PathParam("datatable") final String datatable, @PathParam("apptableIdentifier") final String apptableIdentifier,
            final String apiRequestBodyAsJson, @QueryParam("command") final String commandParam) {

        CommandWrapper commandRequest = null;
        if (is(commandParam, "acc_gl_journal_entry")) {
            commandRequest = new CommandWrapperBuilder() //
            .createDataTableForNonPrimaryIdentifier(datatable, apptableIdentifier, null) //
            .withJson(apiRequestBodyAsJson) //
            .build();
        } else {
            commandRequest = new CommandWrapperBuilder() //
            .createDatatable(datatable, apptableIdentifier, null) //
            .withJson(apiRequestBodyAsJson) //
            .build();
        }
        
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{datatable}/{apptableIdentifier}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateDatatableEntryOnetoOne(@PathParam("datatable") final String datatable,
            @PathParam("apptableIdentifier") final String apptableIdentifier, final String apiRequestBodyAsJson, @QueryParam("command") final String commandParam) {

        CommandWrapper commandRequest = null;
        if (is(commandParam, "acc_gl_journal_entry")) {
            commandRequest = new CommandWrapperBuilder() //
            .updateDataTableForNonPrimaryIdentifier(datatable, apptableIdentifier, null) //
            .withJson(apiRequestBodyAsJson) //
            .build();
        } else {
            commandRequest = new CommandWrapperBuilder() //
            .updateDatatable(datatable, apptableIdentifier, null) //
            .withJson(apiRequestBodyAsJson) //
            .build();
        }
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{datatable}/{apptableIdentifier}/{datatableId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateDatatableEntryOneToMany(@PathParam("datatable") final String datatable,
            @PathParam("apptableIdentifier") final String apptableIdentifier, @PathParam("datatableId") final Long datatableId,
            final String apiRequestBodyAsJson, @QueryParam("command") final String commandParam) {

        CommandWrapper commandRequest = null;
        if (is(commandParam, "acc_gl_journal_entry")) {
            commandRequest = new CommandWrapperBuilder() //
            .updateDataTableForNonPrimaryIdentifier(datatable, apptableIdentifier, datatableId) //
            .withJson(apiRequestBodyAsJson) //
            .build();
        } else {
            commandRequest = new CommandWrapperBuilder() //
            .updateDatatable(datatable, apptableIdentifier, datatableId) //
            .withJson(apiRequestBodyAsJson) //
            .build();
        }
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{datatable}/{apptableIdentifier}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteDatatableEntries(@PathParam("datatable") final String datatable, @PathParam("apptableIdentifier") final String apptableIdentifier,
            @QueryParam("command") final String commandParam) {
        
        CommandWrapper commandRequest = null;
        if (is(commandParam, "acc_gl_journal_entry")) {
            commandRequest = new CommandWrapperBuilder() //
            .deleteDataTableForNonPrimaryIdentifier(datatable, apptableIdentifier, null) //
            .build();
        } else {
            commandRequest = new CommandWrapperBuilder() //
            .deleteDatatable(datatable, apptableIdentifier, null) //
            .build();
        }
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{datatable}/{apptableIdentifier}/{datatableId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteDatatableEntries(@PathParam("datatable") final String datatable, @PathParam("apptableIdentifier") final String apptableIdentifier,
            @PathParam("datatableId") final Long datatableId, @QueryParam("command") final String commandParam) {

        CommandWrapper commandRequest = null;
        if (is(commandParam, "acc_gl_journal_entry")) {
            commandRequest = new CommandWrapperBuilder() //
            .deleteDataTableForNonPrimaryIdentifier(datatable, apptableIdentifier, datatableId) //
            .build();
        } else {
            commandRequest = new CommandWrapperBuilder() //
            .deleteDatatable(datatable, apptableIdentifier, datatableId) //
            .build();
        }
            
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
    
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retieveDatatableTemplate(@Context final UriInfo uriInfo) {
        
        ScopeOptionsData scopeOptionsData = this.readWriteNonCoreDataService.retriveAllScopeOptions();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.scopeOptionsDataApiJsonSerializer.serialize(settings, scopeOptionsData);
    }
    
    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}