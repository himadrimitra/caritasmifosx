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
package org.apache.fineract.infrastructure.documentmanagement.api;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.data.DocumentData;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentReadPlatformService;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.loan.mandate.exception.InvalidCommandQueryParamException;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("{entityType}/{entityId}/documents")
@Component
@Scope("singleton")
public class DocumentManagementApiResource {

    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("id", "parentEntityType", "parentEntityId", "name", "fileName", "size", "type", "description"));

    private final String SystemEntityType = "DOCUMENT";
    private final String GENERATE_COMMAND = "generate";
    private final String REGENERATE_COMMAND = "regenerate";

    private final PlatformSecurityContext context;
    private final DocumentReadPlatformService documentReadPlatformService;
    private final DocumentWritePlatformService documentWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<DocumentData> toApiJsonSerializer;

    @Autowired
    public DocumentManagementApiResource(final PlatformSecurityContext context,
            final DocumentReadPlatformService documentReadPlatformService, final DocumentWritePlatformService documentWritePlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper, final ToApiJsonSerializer<DocumentData> toApiJsonSerializer) {
        this.context = context;
        this.documentReadPlatformService = documentReadPlatformService;
        this.documentWritePlatformService = documentWritePlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveAllDocuments(@Context final UriInfo uriInfo, @PathParam("entityType") final String entityType,
            @PathParam("entityId") final Long entityId) {

        this.context.authenticatedUser().validateHasReadPermission(this.SystemEntityType);

        final Collection<DocumentData> documentDatas = this.documentReadPlatformService.retrieveAllDocuments(entityType, entityId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, documentDatas, this.RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createDocument(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @HeaderParam("Content-Length") final Long fileSize, @FormDataParam("file") final InputStream inputStream,
            @FormDataParam("file") final FormDataContentDisposition fileDetails, @FormDataParam("file") final FormDataBodyPart bodyPart,
            @FormDataParam("name") final String name, @FormDataParam("description") final String description,
            @FormDataParam("tagIdentifier") final Long tagIdentifier) {

        /**
         * TODO: also need to have a backup and stop reading from stream after
         * max size is reached to protect against malicious clients
         **/

        /**
         * TODO: need to extract the actual file type and determine if they are
         * permissable
         **/
        final Long reportIdentifier = null;
        final DocumentCommand documentCommand = new DocumentCommand(null, null, entityType, entityId, name, fileDetails.getFileName(),
                fileSize, bodyPart.getMediaType().toString(), description, null, reportIdentifier, tagIdentifier);

        final Long documentId = this.documentWritePlatformService.createDocument(documentCommand, inputStream);

        return this.toApiJsonSerializer.serialize(CommandProcessingResult.resourceResult(documentId, null));
    }

    @PUT
    @Path("{documentId}")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateDocument(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("documentId") final Long documentId, @HeaderParam("Content-Length") final Long fileSize,
            @FormDataParam("file") final InputStream inputStream, @FormDataParam("file") final FormDataContentDisposition fileDetails,
            @FormDataParam("file") final FormDataBodyPart bodyPart, @FormDataParam("name") final String name,
            @FormDataParam("description") final String description) {

        final Set<String> modifiedParams = new HashSet<>();
        modifiedParams.add("name");
        modifiedParams.add("description");
        final Long reportIdentifier = null;
        final Long tagIdentifier = null;
        /***
         * Populate Document command based on whether a file has also been
         * passed in as a part of the update
         ***/
        DocumentCommand documentCommand = null;
        if (inputStream != null && fileDetails.getFileName() != null) {
            modifiedParams.add("fileName");
            modifiedParams.add("size");
            modifiedParams.add("type");
            modifiedParams.add("location");

            documentCommand = new DocumentCommand(modifiedParams, documentId, entityType, entityId, name, fileDetails.getFileName(),
                    fileSize, bodyPart.getMediaType().toString(), description, null, reportIdentifier, tagIdentifier);
        } else {
            documentCommand = new DocumentCommand(modifiedParams, documentId, entityType, entityId, name, null, null, null, description,
                    null, reportIdentifier, tagIdentifier);
        }
        /***
         * TODO: does not return list of changes, should be done for consistency
         * with rest of API
         **/
        final CommandProcessingResult identifier = this.documentWritePlatformService.updateDocument(documentCommand, inputStream);

        return this.toApiJsonSerializer.serialize(identifier);
    }

    @GET
    @Path("{documentId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getDocument(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("documentId") final Long documentId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.SystemEntityType);

        final DocumentData documentData = this.documentReadPlatformService.retrieveDocument(entityType, entityId, documentId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, documentData, this.RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("generate/{identifier}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String generateDocument(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("identifier") final Long identifier, @QueryParam("command") final String commandParam,
            @Context final UriInfo uriInfo) {
        Long documentId = null;
        this.context.authenticatedUser().validateHasReadPermission(this.SystemEntityType);
        if (this.GENERATE_COMMAND.equals(commandParam)) {
            // identifier is report identifier
            documentId = this.documentWritePlatformService.generateDocument(entityType, entityId, identifier, uriInfo.getQueryParameters());
        } else if (this.REGENERATE_COMMAND.equals(commandParam)) {
            // identifier is document identifier
            documentId = this.documentWritePlatformService.reGenerateDocument(entityType, entityId, identifier,
                    uriInfo.getQueryParameters());
        } else {
            throw new InvalidCommandQueryParamException(commandParam);
        }
        return this.toApiJsonSerializer.serialize(CommandProcessingResult.resourceResult(documentId, null));
    }

    @GET
    @Path("{documentId}/attachment")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    public Response downloadFile(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("documentId") final Long documentId) {

        this.context.authenticatedUser().validateHasReadPermission(this.SystemEntityType);

        final FileData fileData = this.documentReadPlatformService.retrieveFileData(entityType, entityId, documentId);
        final ResponseBuilder response = Response.ok(fileData.file());
        response.header("Content-Disposition", "attachment; filename=\"" + fileData.name() + "\"");
        response.header("Content-Type", fileData.contentType());

        return response.build();
    }

    @DELETE
    @Path("{documentId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteDocument(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("documentId") final Long documentId) {
        final Long reportIdentifier = null;
        final Long tagIdentifier = null;
        final DocumentCommand documentCommand = new DocumentCommand(null, documentId, entityType, entityId, null, null, null, null, null,
                null, reportIdentifier, tagIdentifier);

        final CommandProcessingResult documentIdentifier = this.documentWritePlatformService.deleteDocument(documentCommand);

        return this.toApiJsonSerializer.serialize(documentIdentifier);
    }
}