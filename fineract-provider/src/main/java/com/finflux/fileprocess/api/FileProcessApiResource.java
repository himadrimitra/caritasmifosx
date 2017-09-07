package com.finflux.fileprocess.api;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.fileprocess.service.FileProcessService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("fileprocess")
@Component
@Scope("singleton")
public class FileProcessApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final FileProcessService service;

    @SuppressWarnings("rawtypes")
    @Autowired
    public FileProcessApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, final FileProcessService service) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.service = service;
    }

    @POST
    @Path("upload/{fileProcessType}")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String fileUploadProcess(@PathParam("fileProcessType") final String fileProcessType,
            @HeaderParam("Content-Length") final Long fileSize, @FormDataParam("file") final InputStream inputStream,
            @FormDataParam("file") final FormDataContentDisposition fileDetails, @FormDataParam("file") final FormDataBodyPart bodyPart,
            @FormDataParam("name") final String name, @FormDataParam("description") final String description) {
        final Long reportIdentifier = null;
        final String parentEntityType = null;
        final Long parentEntityId = null;
        final Long tagIdentifier = null;
        final DocumentCommand documentCommand = new DocumentCommand(null, null, parentEntityType, parentEntityId, name,
                fileDetails.getFileName(), fileSize, bodyPart.getMediaType().toString(), description, null, reportIdentifier, tagIdentifier);
        final Long fileProcessId = this.service.fileUploadProcess(fileProcessType, documentCommand, inputStream);
        return this.toApiJsonSerializer.serialize(CommandProcessingResult.resourceResult(fileProcessId, null));
    }
}