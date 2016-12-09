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
package org.apache.fineract.infrastructure.core.service;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public final class SearchParameters {

    private final String sqlSearch;
    private final Long officeId;
    private final String externalId;
    private final String name;
    private final String hierarchy;
    private final String firstname;
    private final String lastname;
    private final Integer offset;
    private final Integer limit;
    private final String orderBy;
    private final String sortOrder;
    private final String accountNo;
    private final String currencyCode;

    private final Long staffId;

    private final Long loanId;

    private final Long savingsId;
    private final Boolean orphansOnly;

    // Provisning Entries Search Params
    private final Long provisioningEntryId;
    private final Long productId;
    private final Long categoryId;
    private final boolean isSelfUser;
    private final Long centerId;
    private final Long groupId;
    
    //report audit search parameter
    private Long userId;
    private Integer reportId;
    private Date startDate;
    private Date endDate;
    private Long paymentTypeId;
    

    public static SearchParameters from(final String sqlSearch, final Long officeId, final String externalId, final String name,
            final String hierarchy) {
        final Long staffId = null;
        final String accountNo = null;
        final Long loanId = null;
        final Long savingsId = null;
        final Boolean orphansOnly = false;
        final boolean isSelfUser = false;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(sqlSearch, officeId, externalId, name, hierarchy, null, null, null, null, null, null, staffId,
                accountNo, loanId, savingsId, orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }

    public static SearchParameters forClients(final String sqlSearch, final Long officeId, final String externalId,
            final String displayName, final String firstname, final String lastname, final String hierarchy, final Integer offset,
            final Integer limit, final String orderBy, final String sortOrder, final Boolean orphansOnly, final boolean isSelfUser) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final String accountNo = null;
        final Long loanId = null;
        final Long savingsId = null;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(sqlSearch, officeId, externalId, displayName, hierarchy, firstname, lastname, offset, maxLimitAllowed,
                orderBy, sortOrder, staffId, accountNo, loanId, savingsId, orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }

    public static SearchParameters forGroups(final String sqlSearch, final Long officeId, final Long staffId, final String externalId,
            final String name, final String hierarchy, final Integer offset, final Integer limit, final String orderBy,
            final String sortOrder, final Boolean orphansOnly) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final String accountNo = null;
        final Long loanId = null;
        final Long savingsId = null;
        final boolean isSelfUser = false;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(sqlSearch, officeId, externalId, name, hierarchy, null, null, offset, maxLimitAllowed, orderBy,
                sortOrder, staffId, accountNo, loanId, savingsId, orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }
    
    public static SearchParameters forVillages(final String sqlSearch, final Long officeId, final String externalId, final String name, 
            final Integer offset, final Integer limit, final String orderBy, final String sortOrder) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Boolean orphansOnly = false;
        final boolean isSelfUser = false;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(sqlSearch, officeId, externalId, name, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder, null,
                null, null, null, orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }

    public static SearchParameters forOffices(final String orderBy, final String sortOrder) {
        final Boolean orphansOnly = false;
        final boolean isSelfUser = false;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(null, null, null, null, null, null, null, null, null, orderBy, sortOrder, null, null, null, null,
                orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }

    public static SearchParameters forLoans(final String sqlSearch, final String externalId, final Integer offset, final Integer limit,
            final String orderBy, final String sortOrder, final String accountNo, final Long officeId) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final Long loanId = null;
        final Long savingsId = null;
        final Boolean orphansOnly = false;
        final boolean isSelfUser = false;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(sqlSearch, officeId, externalId, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder,
                staffId, accountNo, loanId, savingsId, orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }

    public static SearchParameters forJournalEntries(final Long officeId, final Integer offset, final Integer limit, final String orderBy,
            final String sortOrder, final Long loanId, final Long savingsId) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final Boolean orphansOnly = false;
        final boolean isSelfUser = false;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(null, officeId, null, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder, staffId,
                null, loanId, savingsId, orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }

    public static SearchParameters forJournalEntries(final Long officeId, final Integer offset, final Integer limit, final String orderBy,
            final String sortOrder, final Long loanId, final Long savingsId, final String currencyCode) {
        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final Boolean orphansOnly = false;

        return new SearchParameters(null, officeId, null, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder, staffId,
                null, loanId, savingsId, orphansOnly, currencyCode);
    }

    public static SearchParameters forPagination(final Integer offset, final Integer limit, final String orderBy, final String sortOrder) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final Long loanId = null;
        final Long savingsId = null;
        final Boolean orphansOnly = false;
        final boolean isSelfUser = false;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(null, null, null, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder, staffId, null,
                loanId, savingsId, orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }

    public static SearchParameters forPaginationAndAccountNumberSearch(final Integer offset, final Integer limit, final String orderBy,
            final String sortOrder, final String accountNumber) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final Long loanId = null;
        final Long savingsId = null;
        final Boolean orphansOnly = false;
        final boolean isSelfUser = false;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(null, null, null, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder, staffId,
                accountNumber, loanId, savingsId, orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }

    public static SearchParameters forPagination(final Integer offset, final Integer limit) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final Long loanId = null;
        final Long savingsId = null;
        final Boolean orphansOnly = false;
        final String orderBy = null;
        final String sortOrder = null;
        final boolean isSelfUser = false;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(null, null, null, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder, staffId, null,
                loanId, savingsId, orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }

    public final static SearchParameters forProvisioningEntries(final Long provisioningEntryId, final Long officeId, final Long productId,
            final Long categoryId, final Integer offset, final Integer limit) {
        return new SearchParameters(provisioningEntryId, officeId, productId, categoryId, offset, limit);
    }

    public static SearchParameters forSavings(final String sqlSearch, final String externalId, final Integer offset, final Integer limit,
            final String orderBy, final String sortOrder, final Long officeId) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final String accountNo = null;
        final Long loanId = null;
        final Long savingsId = null;
        final Boolean orphansOnly = false;
        final boolean isSelfUser = false;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(sqlSearch, officeId, externalId, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder,
                staffId, accountNo, loanId, savingsId, orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }

    public static SearchParameters forAccountTransfer(final String sqlSearch, final String externalId, final Integer offset,
            final Integer limit, final String orderBy, final String sortOrder) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final String accountNo = null;
        final Long loanId = null;
        final Long savingsId = null;
        final Boolean orphansOnly = false;
        final boolean isSelfUser = false;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(sqlSearch, null, externalId, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder,
                staffId, accountNo, loanId, savingsId, orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }  
    
    public static SearchParameters forPledges(Integer offset, Integer limit, String orderBy) {
        final Boolean orphansOnly = false;
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(null, null, null, null, null, null, null, offset, limit, orderBy, null, null, null, null, null,
                orphansOnly,false, centerId, groupId, paymentTypeId);
    }

    public static SearchParameters forTask(final String sqlSearch, final Long officeId, final Long staffId, final Long centerId,final Long groupId,
              final Integer offset, final Integer limit, final String orderBy,final String sortOrder, Long paymentTypeId) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final String accountNo = null;
        final Long loanId = null;
        final Long savingsId = null;
        final boolean isSelfUser = false;
        final String externalId = null;
        final String name = null;
        final String hierarchy = null;
        final Boolean orphansOnly = null;
        final String firstname = null;
        final String lastname = null;
        return new SearchParameters(sqlSearch, officeId, externalId, name, hierarchy,firstname,lastname, offset, maxLimitAllowed, orderBy,
                sortOrder, staffId, accountNo, loanId, savingsId, orphansOnly, isSelfUser, centerId, groupId, paymentTypeId);
    }
    
	public static SearchParameters formPaginationSearchParameters(final Long officeId, final Integer offset, final Integer limit,
			final String orderBy, final String sortOrder) {

		final Integer maxLimitAllowed = getCheckedLimit(limit);
		final String accountNo = null;
		final Long loanId = null;
		final Long savingsId = null;
		final boolean isSelfUser = false;
		final Long centerId = null;
		final Long groupId = null;
		final String name = null;
		final String hierarchy = null;
		final String sqlSearch = null;
		final String externalId = null;
		final Boolean orphansOnly = null;
		final Long staffId = null;
		final Long paymentTypeId = null;
		return new SearchParameters(sqlSearch, officeId, externalId, name, hierarchy, null, null, offset,
				maxLimitAllowed, orderBy, sortOrder, staffId, accountNo, loanId, savingsId, orphansOnly, isSelfUser,
				centerId, groupId, paymentTypeId);
	}
    
    private SearchParameters(final String sqlSearch, final Long officeId, final String externalId, final String name,
            final String hierarchy, final String firstname, final String lastname, final Integer offset, final Integer limit,
            final String orderBy, final String sortOrder, final Long staffId, final String accountNo, final Long loanId,
            final Long savingsId, final Boolean orphansOnly, boolean isSelfUser, Long centerId, Long groupId, Long paymentTypeId) {
        this.sqlSearch = sqlSearch;
        this.officeId = officeId;
        this.externalId = externalId;
        this.name = name;
        this.hierarchy = hierarchy;
        this.firstname = firstname;
        this.lastname = lastname;
        this.offset = offset;
        this.limit = limit;
        this.orderBy = orderBy;
        this.sortOrder = sortOrder;
        this.staffId = staffId;
        this.accountNo = accountNo;
        this.loanId = loanId;
        this.savingsId = savingsId;
        this.orphansOnly = orphansOnly;
        this.currencyCode = null;
        this.provisioningEntryId = null;
        this.productId = null;
        this.categoryId = null;
        this.isSelfUser = isSelfUser;
        this.centerId = centerId;
        this.groupId = groupId;
        this.paymentTypeId = paymentTypeId;

    }

    private SearchParameters(final Long provisioningEntryId, final Long officeId, final Long productId, final Long categoryId,
            final Integer offset, final Integer limit) {
        this.sqlSearch = null;
        this.externalId = null;
        this.name = null;
        this.hierarchy = null;
        this.firstname = null;
        this.lastname = null;
        this.orderBy = null;
        this.sortOrder = null;
        this.staffId = null;
        this.accountNo = null;
        this.loanId = null;
        this.savingsId = null;
        this.orphansOnly = null;
        this.currencyCode = null;
        this.officeId = officeId;
        this.offset = offset;
        this.limit = limit;
        this.provisioningEntryId = provisioningEntryId;
        this.productId = productId;
        this.categoryId = categoryId;
        this.isSelfUser = false;
        this.centerId = null;
        this.groupId = null;

    }

    public SearchParameters(final String sqlSearch, final Long officeId, final String externalId, final String name,
            final String hierarchy, final String firstname, final String lastname, final Integer offset, final Integer limit,
            final String orderBy, final String sortOrder, final Long staffId, final String accountNo, final Long loanId,
            final Long savingsId, final Boolean orphansOnly, final String currencyCode) {
        this.sqlSearch = sqlSearch;
        this.officeId = officeId;
        this.externalId = externalId;
        this.name = name;
        this.hierarchy = hierarchy;
        this.firstname = firstname;
        this.lastname = lastname;
        this.offset = offset;
        this.limit = limit;
        this.orderBy = orderBy;
        this.sortOrder = sortOrder;
        this.staffId = staffId;
        this.accountNo = accountNo;
        this.loanId = loanId;
        this.savingsId = savingsId;
        this.orphansOnly = orphansOnly;
        this.currencyCode = currencyCode;
        this.provisioningEntryId = null;
        this.productId = null;
        this.categoryId = null;
        this.isSelfUser = false;
        this.centerId = null;
        this.groupId = null;
    }
    
    private SearchParameters(final Long userId, final Integer reportId, final Date startDate,
    		final Date endDate, final Integer offset,final Integer limit, final String orderBy,
    		final String sortOrder, String sqlSearch) {
        this.userId = userId;
        this.reportId = reportId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sqlSearch = sqlSearch;
        this.offset = offset;
        this.limit = limit;
        this.orderBy = orderBy;
        this.sortOrder = sortOrder;
        this.officeId = null;
        this.externalId = null;
        this.name = null;
        this.hierarchy = null;
        this.firstname = null;
        this.lastname = null;
        this.staffId = null;
        this.accountNo = null;
        this.loanId = null;
        this.savingsId = null;
        this.orphansOnly = null;
        this.currencyCode = null;
        this.provisioningEntryId = null;
        this.productId = null;
        this.categoryId = null;
        this.isSelfUser = false;
        this.centerId = null;
        this.groupId = null;

    }


    public boolean isOrderByRequested() {
        return StringUtils.isNotBlank(this.orderBy);
    }

    public boolean isSortOrderProvided() {
        return StringUtils.isNotBlank(this.sortOrder);
    }

    public static Integer getCheckedLimit(final Integer limit) {

        final Integer maxLimitAllowed = 200;
        // default to max limit first off
        Integer checkedLimit = maxLimitAllowed;

        if (limit != null && limit > 0) {
            checkedLimit = limit;
        } else if (limit != null) {
            // unlimited case: limit provided and 0 or less
            checkedLimit = null;
        }

        return checkedLimit;
    }

    public boolean isOfficeIdPassed() {
        return this.officeId != null && this.officeId != 0;
    }

    public boolean isCurrencyCodePassed() {
        return this.currencyCode != null;
    }

    public boolean isLimited() {
        return this.limit != null && this.limit.intValue() > 0;
    }

    public boolean isOffset() {
        return this.offset != null;
    }

    public boolean isScopedByOfficeHierarchy() {
        return StringUtils.isNotBlank(this.hierarchy);
    }

    public String getSqlSearch() {
        return this.sqlSearch;
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public String getName() {
        return this.name;
    }

    public String getHierarchy() {
        return this.hierarchy;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public Integer getOffset() {
        return this.offset;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public String getSortOrder() {
        return this.sortOrder;
    }

    public boolean isStaffIdPassed() {
        return this.staffId != null && this.staffId != 0;
    }

    public Long getStaffId() {
        return this.staffId;
    }

    public String getAccountNo() {
        return this.accountNo;
    }

    public boolean isLoanIdPassed() {
        return this.loanId != null && this.loanId != 0;
    }

    public boolean isSavingsIdPassed() {
        return this.savingsId != null && this.savingsId != 0;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public Long getSavingsId() {
        return this.savingsId;
    }

    public Boolean isOrphansOnly() {
        if (this.orphansOnly != null) { return this.orphansOnly; }
        return false;
    }

    public Long getProvisioningEntryId() {
        return this.provisioningEntryId;
    }

    public boolean isProvisioningEntryIdPassed() {
        return this.provisioningEntryId != null && this.provisioningEntryId != 0;
    }

    public Long getProductId() {
        return this.productId;
    }

    public boolean isProductIdPassed() {
        return this.productId != null && this.productId != 0;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public boolean isCategoryIdPassed() {
        return this.categoryId != null && this.categoryId != 0;
    }

    public boolean isSelfUser() {
        return this.isSelfUser;
    }
    
    public Long getCenterId() {
        return this.centerId;
    }

    
    public Long getGroupId() {
        return this.groupId;
    }
    
    public Long getUserId() {
		return this.userId;
	}

	public Integer getReportId() {
		return this.reportId;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	/** 
     * creates an instance of the SearchParameters from a request for the report mailing job run history
     * 
     * @return SearchParameters object
     **/
    public static SearchParameters fromReportMailingJobRunHistory(final Integer offset, 
            final Integer limit, final String orderBy, final String sortOrder) {
        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(null, null, null, null, null, null, null, offset, maxLimitAllowed, orderBy,
                sortOrder, null, null, null, null, null, false,centerId,groupId, paymentTypeId);
    }
    
    /**
     * creates an instance of the {@link SearchParameters} from a request for the report mailing job
     * 
     * @param offset
     * @param limit
     * @param orderBy
     * @param sortOrder
     * @return {@link SearchParameters} object
     */
    public static SearchParameters fromReportMailingJob(final Integer offset, 
            final Integer limit, final String orderBy, final String sortOrder) {
        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long centerId = null;
        final Long groupId = null;
        final Long paymentTypeId = null;
        return new SearchParameters(null, null, null, null, null, null, null, offset, maxLimitAllowed, orderBy,
                sortOrder, null, null, null, null, null, false,centerId,groupId, paymentTypeId);
    }
    
    public static SearchParameters fromReportAudit(final Long userId, final Integer reportId, final Date startDate,
    		final Date endDate, final Integer offset,final Integer limit, final String orderBy, final String sortOrder, String sqlSearch) {
        final Integer maxLimitAllowed = getCheckedLimit(limit);
        return new SearchParameters(userId, reportId, startDate, endDate, offset, maxLimitAllowed, orderBy, sortOrder, sqlSearch);
    }
    
    public Long getPaymentType() {
        return this.paymentTypeId;
    }
}
