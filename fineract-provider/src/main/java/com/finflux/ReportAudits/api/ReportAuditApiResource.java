/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.ReportAudits.api;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.accounting.journalentry.api.DateParam;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.PaginationParameters;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.ReportAudits.data.ReportAuditData;
import com.finflux.ReportAudits.service.ReportAuditReadPlatformService;

@Path("/reportaudits")
@Component
@Scope("singleton")
public class ReportAuditApiResource {
	
	private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<ReportAuditData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ReportAuditReadPlatformService reportAuditReadPlatformService;
    
    @Autowired
	public ReportAuditApiResource(final PlatformSecurityContext context,
			final ToApiJsonSerializer<ReportAuditData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final ReportAuditReadPlatformService reportAuditReadPlatformService) {
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.reportAuditReadPlatformService = reportAuditReadPlatformService;
	}
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("paged") final Boolean paged,
            @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
            @QueryParam("fromDate") final DateParam fromDateParam, @QueryParam("toDate") final DateParam toDateParam,
            @QueryParam("userId") final Long userId, @QueryParam("reportId") final Integer reportId,@QueryParam("orderBy") final String orderBy,
            @QueryParam("sortOrder") final String sortOrder,@QueryParam("sqlSearch") final String sqlSearch,
            @QueryParam("locale") final String locale, @QueryParam("dateFormat") final String dateFormat){
        
        this.context.authenticatedUser().validateHasReadPermission(ReportAuditConstants.REPORT_AUDIT_RESOURCE_NAME);
        Date fromDate = null;
        if (fromDateParam != null) {
            fromDate = fromDateParam.getDate("fromDate", dateFormat, locale);
        }
        Date toDate = null;
        if (toDateParam != null) {
            toDate = toDateParam.getDate("toDate", dateFormat, locale);
        }
        final PaginationParameters parameters = PaginationParameters.instance(paged, offset, limit, orderBy, sortOrder);
        final SearchParameters searchParameters = SearchParameters.fromReportAudit(userId, reportId, fromDate, toDate, offset, limit, orderBy, sortOrder, sqlSearch);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());  
        
        Page<ReportAuditData> reportAuditData = this.reportAuditReadPlatformService.retrieveAllReportAudits(searchParameters, parameters);
          
        return this.toApiJsonSerializer.serialize(settings, reportAuditData, ReportAuditConstants.REPORT_AUDIT_RESPONSE_DATA_PARAMETERS);

        
    }
    
    @GET
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveBankWithId(@PathParam("id") final Long id, @Context final UriInfo uriInfo) {

    	this.context.authenticatedUser().validateHasReadPermission(ReportAuditConstants.REPORT_AUDIT_RESOURCE_NAME);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());  
        
        ReportAuditData reportAuditData = this.reportAuditReadPlatformService.getReportAudits(id);
          
        return this.toApiJsonSerializer.serialize(settings, reportAuditData, ReportAuditConstants.REPORT_AUDIT_RESPONSE_DATA_PARAMETERS);
    }

}
