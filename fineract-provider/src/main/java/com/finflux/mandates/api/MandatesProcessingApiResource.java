package com.finflux.mandates.api;

import com.finflux.mandates.data.MandateTransactionsData;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.data.MandatesSummaryData;
import com.finflux.mandates.domain.MandateProcessTypeEnum;
import com.finflux.mandates.exception.CommandQueryParamExpectedException;
import com.finflux.mandates.exception.InvalidCommandQueryParamException;
import com.finflux.mandates.service.MandatesProcessingReadPlatformService;
import com.finflux.mandates.service.TransactionsProcessingReadPlatformService;
import com.finflux.portfolio.loan.mandate.data.MandateData;
import com.finflux.portfolio.loan.mandate.service.MandateReadPlatformService;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Path("/mandates")
@Component
@Scope("singleton")
public class MandatesProcessingApiResource {

        private final PlatformSecurityContext context;
        private final ApiRequestParameterHelper apiRequestParameterHelper;
        private final DefaultToApiJsonSerializer toApiJsonSerializer;
        private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
        private final MandatesProcessingReadPlatformService readPlatformService;
        private final MandateReadPlatformService mandateReadPlatformService;
        private final TransactionsProcessingReadPlatformService transactionsProcessingReadPlatformService;

        @Autowired
        public MandatesProcessingApiResource(final PlatformSecurityContext context,
                final ApiRequestParameterHelper apiRequestParameterHelper,
                final DefaultToApiJsonSerializer toApiJsonSerializer,
                final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                final MandatesProcessingReadPlatformService readPlatformService,
                final MandateReadPlatformService mandateReadPlatformService,
                final TransactionsProcessingReadPlatformService transactionsProcessingReadPlatformService){

                this.context = context;
                this.apiRequestParameterHelper = apiRequestParameterHelper;
                this.toApiJsonSerializer = toApiJsonSerializer;
                this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
                this.readPlatformService = readPlatformService;
                this.mandateReadPlatformService = mandateReadPlatformService;
                this.transactionsProcessingReadPlatformService = transactionsProcessingReadPlatformService;
        }

        @GET
        @Path("template")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveTemplate(@QueryParam("command") final String commandParam,
                @Context final UriInfo uriInfo) {

                this.context.authenticatedUser().validateHasReadPermission(MandatesProcessingApiConstants.RESOURCE_NAME);

                MandatesProcessData template = null;

                if(null == commandParam){
                        throw new CommandQueryParamExpectedException();
                }else if(commandParam.equalsIgnoreCase(MandateProcessTypeEnum.MANDATES_DOWNLOAD.getType())){
                        template = this.readPlatformService.retrieveMandateDownloadTemplate();
                }else if(commandParam.equalsIgnoreCase(MandateProcessTypeEnum.TRANSACTIONS_DOWNLOAD.getType())){
                        template = this.readPlatformService.retrieveTransactionsDownloadTemplate();
                }else{
                        throw new InvalidCommandQueryParamException(commandParam);
                }
                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, template);
        }

        @GET
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveAll(@QueryParam("type") final String typeParam, @QueryParam("requestDate") final String requestDateParam,
                @QueryParam("dateFormat") final String dateFormat, @QueryParam("officeId") final Long officeIdParams,
                @Context final UriInfo uriInfo) throws ParseException {

                this.context.authenticatedUser().validateHasReadPermission(MandatesProcessingApiConstants.RESOURCE_NAME);

                MandateProcessTypeEnum type = MandateProcessTypeEnum.INVALID;
                if(null == typeParam){
                        type = MandateProcessTypeEnum.ALL;
                }else {
                        type = MandateProcessTypeEnum.from(typeParam.toUpperCase());

                }
                if(type.hasStateOf(MandateProcessTypeEnum.INVALID)){
                        throw new InvalidCommandQueryParamException(typeParam);
                }

                final Date requestDate = (null == requestDateParam || null == dateFormat)? null : (new SimpleDateFormat(dateFormat)).parse(requestDateParam);

                Collection<MandatesProcessData> mandatesProcesses = this.readPlatformService.retrieveMandates(type, requestDate, officeIdParams);
                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, mandatesProcesses);
        }

        @POST
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String postDownloadRequest(@QueryParam("command") final String commandParam,
                @Context final UriInfo uriInfo, final String apiRequestBodyAsJson)
                throws InvalidFormatException, IOException {

                this.context.authenticatedUser();
                CommandWrapperBuilder builder = new CommandWrapperBuilder();
                CommandWrapper commandRequest = null;
                if(null == commandParam){
                        throw new CommandQueryParamExpectedException();
                }else if(commandParam.equalsIgnoreCase(MandateProcessTypeEnum.MANDATES_DOWNLOAD.getType())){
                        commandRequest = builder.mandatesDownload().withJson(apiRequestBodyAsJson).build();
                }else if(commandParam.equalsIgnoreCase(MandateProcessTypeEnum.TRANSACTIONS_DOWNLOAD.getType())){
                        commandRequest = builder.transactionsDownload().withJson(apiRequestBodyAsJson).build();
                }else{
                        throw new InvalidCommandQueryParamException(commandParam);
                }

                final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
                return this.toApiJsonSerializer.serialize(result);
        }

        @POST
        @Consumes({ MediaType.MULTIPART_FORM_DATA })
        @Produces({ MediaType.APPLICATION_JSON })
        public String postUploadRequest(@QueryParam("command") final String commandParam,
                @Context final UriInfo uriInfo, final FormDataMultiPart formParams)
                throws InvalidFormatException, IOException {

                this.context.authenticatedUser();
                CommandWrapperBuilder builder = new CommandWrapperBuilder();
                CommandWrapper commandRequest = null;
                if(null == commandParam){
                        throw new CommandQueryParamExpectedException();
                }else if(commandParam.equalsIgnoreCase(MandateProcessTypeEnum.MANDATES_UPLOAD.getType())){
                        commandRequest = builder.mandatesUpload().withFormDataMultiPart(formParams).build();
                }else if(commandParam.equalsIgnoreCase(MandateProcessTypeEnum.TRANSACTIONS_UPLOAD.getType())){
                        commandRequest = builder.transactionsUpload().withFormDataMultiPart(formParams).build();
                }else{
                        throw new InvalidCommandQueryParamException(commandParam);
                }

                final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
                return this.toApiJsonSerializer.serialize(result);
        }

        @GET
        @Path("summary/mandates")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveMandatesSummary(@QueryParam("officeId") final Long officeId,
                @QueryParam("includeChildOffices") final Boolean includeChildOffices,
                @QueryParam("requestFromDate") final String requestFromDate,
                @QueryParam("requestToDate") final String requestToDate,
                @QueryParam("dateFormat") final String dateFormat,
                @Context final UriInfo uriInfo) throws ParseException {

                this.context.authenticatedUser().validateHasReadPermission(MandatesProcessingApiConstants.RESOURCE_NAME);

                final Date fromDate = (null == requestFromDate || null == dateFormat)? new Date() : (new SimpleDateFormat(dateFormat)).parse(requestFromDate);
                final Date toDate = (null == requestToDate || null == dateFormat)? new Date() : (new SimpleDateFormat(dateFormat)).parse(requestToDate);
                final Long office = (null == officeId)? this.context.authenticatedUser().getOffice().getId() : officeId;
                final Boolean includeChild = (null == includeChildOffices)? false : includeChildOffices;

                Collection<MandatesSummaryData> mandatesSummary = this.mandateReadPlatformService
                        .retrieveMandateSummary(office, includeChild, fromDate, toDate);
                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, mandatesSummary);
        }

        @GET
        @Path("list/mandates")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveMandatesSummary(@QueryParam("officeId") final Long officeId,
                @QueryParam("includeChildOffices") final Boolean includeChildOffices,
                @QueryParam("requestFromDate") final String requestFromDate,
                @QueryParam("requestToDate") final String requestToDate,
                @QueryParam("dateFormat") final String dateFormat,
                @QueryParam("offset") final Integer offsetParam,
                @QueryParam("limit") final Integer limitParam,
                @Context final UriInfo uriInfo) throws ParseException {

                this.context.authenticatedUser().validateHasReadPermission(MandatesProcessingApiConstants.RESOURCE_NAME);

                final Long office = (null == officeId)? this.context.authenticatedUser().getOffice().getId() : officeId;
                final Boolean includeChild = (null == includeChildOffices)? false : includeChildOffices;
                final Date fromDate = (null == requestFromDate || null == dateFormat)? new Date() : (new SimpleDateFormat(dateFormat)).parse(requestFromDate);
                final Date toDate = (null == requestToDate || null == dateFormat)? new Date() : (new SimpleDateFormat(dateFormat)).parse(requestToDate);
                final Integer limit = (null == limitParam)? 25 : limitParam;
                final Integer offset = (null == offsetParam)? 0 : offsetParam;

                Page<MandateData> mandates = this.mandateReadPlatformService
                        .retrieveAllMandates(office, includeChild, fromDate, toDate, offset, limit);
                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, mandates);
        }

        @GET
        @Path("summary/transactions")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveTransactionsSummary(@QueryParam("officeId") final Long officeId,
                @QueryParam("includeChildOffices") final Boolean includeChildOffices,
                @QueryParam("requestFromDate") final String requestFromDate,
                @QueryParam("requestToDate") final String requestToDate,
                @QueryParam("dateFormat") final String dateFormat,
                @Context final UriInfo uriInfo) throws ParseException {

                this.context.authenticatedUser().validateHasReadPermission(MandatesProcessingApiConstants.RESOURCE_NAME);

                final Date fromDate = (null == requestFromDate || null == dateFormat)? new Date() : (new SimpleDateFormat(dateFormat)).parse(requestFromDate);
                final Date toDate = (null == requestToDate || null == dateFormat)? new Date() : (new SimpleDateFormat(dateFormat)).parse(requestToDate);
                final Long office = (null == officeId)? this.context.authenticatedUser().getOffice().getId() : officeId;
                final Boolean includeChild = (null == includeChildOffices)? false : includeChildOffices;

                Collection<MandatesSummaryData> transactionsSummary = this.transactionsProcessingReadPlatformService
                        .retrieveTransactionSummary(office, includeChild, fromDate, toDate);
                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, transactionsSummary);
        }

        @GET
        @Path("list/transactions")
        @Consumes({ MediaType.APPLICATION_JSON })
        @Produces({ MediaType.APPLICATION_JSON })
        public String retrieveTransactionsSummary(@QueryParam("officeId") final Long officeId,
                @QueryParam("includeChildOffices") final Boolean includeChildOffices,
                @QueryParam("requestFromDate") final String requestFromDate,
                @QueryParam("requestToDate") final String requestToDate,
                @QueryParam("dateFormat") final String dateFormat,
                @QueryParam("offset") final Integer offsetParam,
                @QueryParam("limit") final Integer limitParam,
                @Context final UriInfo uriInfo) throws ParseException {

                this.context.authenticatedUser().validateHasReadPermission(MandatesProcessingApiConstants.RESOURCE_NAME);

                final Long office = (null == officeId)? this.context.authenticatedUser().getOffice().getId() : officeId;
                final Boolean includeChild = (null == includeChildOffices)? false : includeChildOffices;
                final Date fromDate = (null == requestFromDate || null == dateFormat)? new Date() : (new SimpleDateFormat(dateFormat)).parse(requestFromDate);
                final Date toDate = (null == requestToDate || null == dateFormat)? new Date() : (new SimpleDateFormat(dateFormat)).parse(requestToDate);
                final Integer limit = (null == limitParam)? 25 : limitParam;
                final Integer offset = (null == offsetParam)? 0 : offsetParam;

                Page<MandateTransactionsData> transactions = this.transactionsProcessingReadPlatformService
                        .retrieveAllTransactions(office, includeChild, fromDate, toDate, offset, limit);
                final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
                return this.toApiJsonSerializer.serialize(settings, transactions);
        }

}
