/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.api;

import java.math.BigDecimal;
import java.util.Collection;
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


import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringData;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringRepaymentScheduleData;
import org.apache.fineract.portfolio.loanaccount.service.GroupLoanIndividualMonitoringReadPlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/grouploanindividualmonitoring")
@Component
@Scope("singleton")
public class GroupLoanIndividualMonitoringApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer<GroupLoanIndividualMonitoringData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<GroupLoanIndividualMonitoringRepaymentScheduleData> toJsonSerializer;
    private final GroupLoanIndividualMonitoringReadPlatformService groupLoanIndividualMonitoringReadPlatformService;

    @Autowired
    public GroupLoanIndividualMonitoringApiResource(final PlatformSecurityContext context,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer<GroupLoanIndividualMonitoringData> toApiJsonSerializer,
            final GroupLoanIndividualMonitoringReadPlatformService groupLoanIndividualMonitoringReadPlatformService,
            final DefaultToApiJsonSerializer<GroupLoanIndividualMonitoringRepaymentScheduleData> toJsonSerializer) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.groupLoanIndividualMonitoringReadPlatformService = groupLoanIndividualMonitoringReadPlatformService;
        this.toJsonSerializer = toJsonSerializer;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo) {
        
        this.context.authenticatedUser();

        final Collection<GroupLoanIndividualMonitoringData> groupLoanIndividualMonitoringData = this.groupLoanIndividualMonitoringReadPlatformService
                .retrieveAll();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, groupLoanIndividualMonitoringData);
    }

    @GET
    @Path("resource/{resourceId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("resourceId") final Long resourceId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser();
        
        final GroupLoanIndividualMonitoringData groupLoanIndividualMonitoringData = this.groupLoanIndividualMonitoringReadPlatformService
                .retrieveOne(resourceId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, groupLoanIndividualMonitoringData);
    }

    @GET
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllGroupLoanIndividualMonitoringDataByLoan(@Context final UriInfo uriInfo, @PathParam("loanId") final Long loanId) {

        this.context.authenticatedUser();
        
        final Collection<GroupLoanIndividualMonitoringData> groupLoanIndividualMonitoringData = this.groupLoanIndividualMonitoringReadPlatformService
                .retrieveAllByLoanId(loanId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, groupLoanIndividualMonitoringData);
    }

    @GET
    @Path("{loanId}/{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveGroupLoanIndividualMonitoringDataByLoanAndClient(@Context final UriInfo uriInfo,
            @PathParam("loanId") final Long loanId, @PathParam("clientId") final Long clientId) {

        final GroupLoanIndividualMonitoringData groupLoanIndividualMonitoringData = this.groupLoanIndividualMonitoringReadPlatformService
                .retrieveByLoanAndClientId(loanId, clientId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, groupLoanIndividualMonitoringData);
    }

    @GET
    @Path("viewrepaymentschedule/{glimId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveGlimRepaymentSchedule(@Context final UriInfo uriInfo, @PathParam("glimId") final Long glimId,
            @QueryParam("disbursedDate") final Date disbursedDate, @QueryParam("disbursedAmount") final BigDecimal disbursedAmount) {

        this.context.authenticatedUser();
        
        final GroupLoanIndividualMonitoringRepaymentScheduleData glimRepaymentScheduleData = this.groupLoanIndividualMonitoringReadPlatformService
                .retriveGlimRepaymentScheduleById(glimId, disbursedAmount, new LocalDate(disbursedDate));

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toJsonSerializer.serialize(settings, glimRepaymentScheduleData);
    }

}
