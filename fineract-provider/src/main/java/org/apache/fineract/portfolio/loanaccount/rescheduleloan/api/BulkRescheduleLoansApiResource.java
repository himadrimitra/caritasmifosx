/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.portfolio.loanaccount.rescheduleloan.api;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.staff.data.StaffAccountSummaryCollectionData;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.data.BulkRescheduleLoansData;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.data.BulkRescheduleLoansDataValidator;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.service.BulkLoanRescheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/loans/bulkLoanReschedule")
@Component
@Scope("singleton")
public class BulkRescheduleLoansApiResource {

    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("officeId", "LoanOfficerId", "accountSummaryCollection"));

    private final String resourceNameForPermissions = "LOAN";

    private final PlatformSecurityContext context;

    private final BulkLoanRescheduleService bulkLoanRescheduleService;
    private final DefaultToApiJsonSerializer<BulkRescheduleLoansData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final BulkRescheduleLoansDataValidator bulkRescheduleLoansDataValidator;

    @Autowired
    public BulkRescheduleLoansApiResource(final PlatformSecurityContext context, final BulkLoanRescheduleService bulkLoanRescheduleService,
            final DefaultToApiJsonSerializer<BulkRescheduleLoansData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final BulkRescheduleLoansDataValidator bulkRescheduleLoansDataValidator) {
        this.context = context;
        this.bulkLoanRescheduleService = bulkLoanRescheduleService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.bulkRescheduleLoansDataValidator = bulkRescheduleLoansDataValidator;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String loanRescheduleTemplate(@QueryParam("officeId") final Long officeId, @QueryParam("loanOfficerId") final Long loanOfficerId,
            @QueryParam("rescheduleFromDate") final Date dueDate, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        this.bulkRescheduleLoansDataValidator.validateForRetrieveOperation(officeId, loanOfficerId, dueDate);

        StaffAccountSummaryCollectionData staffAccountSummaryCollectionData = null;

        if (loanOfficerId != null) {
            staffAccountSummaryCollectionData = this.bulkLoanRescheduleService.retrieveLoanOfficerAccountSummary(loanOfficerId, dueDate);

        }

        final BulkRescheduleLoansData loanRescheduleData = BulkRescheduleLoansData.template(officeId, loanOfficerId,staffAccountSummaryCollectionData);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, loanRescheduleData, this.RESPONSE_DATA_PARAMETERS);
    }
}