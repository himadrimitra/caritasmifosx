package com.finflux.fileprocess.api;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.fileprocess.data.FileProcessData;
import com.finflux.fileprocess.service.FileProcessReadPlatformService;
import com.finflux.fileprocess.service.FileProcessService;
import com.finflux.risk.creditbureau.provider.service.ContentServiceUtil;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("fileprocess")
@Component
@Scope("singleton")
public class FileProcessApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<FileProcessData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final FileProcessService service;
    private final FileProcessReadPlatformService fileProcessReadPlatformService;
    private final ContentServiceUtil contentServiceUtil;

    @Autowired
    public FileProcessApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final ToApiJsonSerializer<FileProcessData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, final FileProcessService service,
            final FileProcessReadPlatformService fileProcessReadPlatformService, final ContentServiceUtil contentServiceUtil) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.service = service;
        this.fileProcessReadPlatformService = fileProcessReadPlatformService;
        this.contentServiceUtil = contentServiceUtil;
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
                fileDetails.getFileName(), fileSize, bodyPart.getMediaType().toString(), description, null, reportIdentifier,
                tagIdentifier);
        final Long fileProcessId = this.service.fileUploadProcess(fileProcessType, documentCommand, inputStream);
        return this.toApiJsonSerializer.serialize(CommandProcessingResult.resourceResult(fileProcessId, null));
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("fileName") final String fileName,
            @QueryParam("fileProcessType") final Integer fileProcessType, @QueryParam("status") final Integer status,
            @QueryParam("createdBy") final Long createdBy, @QueryParam("offset") final Integer offset,
            @QueryParam("limit") final Integer limit, @QueryParam("orderBy") final String orderBy,
            @QueryParam("sortOrder") final String sortOrder) {
        final SearchParameters searchParameters = SearchParameters.forPagination(offset, limit, orderBy, sortOrder);
        final Page<FileProcessData> data = this.fileProcessReadPlatformService.retrieveAll(searchParameters, fileName, fileProcessType,
                status, createdBy);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, data);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo) {
        final FileProcessData data = this.fileProcessReadPlatformService.retrieveTemplate();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, data);
    }

    @GET
    @Path("{fileProcessId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@Context final UriInfo uriInfo, @PathParam("fileProcessId") final Long fileProcessId) {
        final FileProcessData data = this.fileProcessReadPlatformService.retrieveOne(fileProcessId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, data);
    }

    @GET
    @Path("{fileProcessId}/attachment")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    public Response downloadFile(@PathParam("fileProcessId") final Long fileProcessId) {

        final FileProcessData data = this.fileProcessReadPlatformService.retrieveOne(fileProcessId);
        final FileData fileData = this.contentServiceUtil.fetchFile(data.getFilePath());
        final ResponseBuilder response = Response.ok(fileData.file());
        response.header("Content-Disposition", "attachment; filename=\"" + data.getFileName() + "\"");
        response.header("Content-Type", data.getContentType());

        return response.build();
    }
}