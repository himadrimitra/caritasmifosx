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

    private final Long staffId;

    private final Long loanId;

    private final Long savingsId;
    private final Long clientStatus;
    private final String mobileNoLength;

	public static SearchParameters from(final String sqlSearch, final Long officeId, final String externalId, final String name,
            final String hierarchy, final String mobileNoLength) {
        final Long staffId = null;
        final String accountNo = null;
        final Long loanId = null;
        final Long savingsId = null;
        return new SearchParameters(sqlSearch, officeId, externalId, name, hierarchy, null, null, null, null, null, null, staffId,
                accountNo, loanId, savingsId, null, mobileNoLength);
    }

    public static SearchParameters forClients(final String sqlSearch, final Long officeId, final String externalId,
            final String displayName, final String firstname, final String lastname, final String hierarchy, final Integer offset,
            final Integer limit, final String orderBy, final String sortOrder, final Long clientStatus, final String mobileNoLength) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final String accountNo = null;
        final Long loanId = null;
        final Long savingsId = null;

        return new SearchParameters(sqlSearch, officeId, externalId, displayName, hierarchy, firstname, lastname, offset, maxLimitAllowed,
                orderBy, sortOrder, staffId, accountNo, loanId, savingsId, clientStatus, mobileNoLength);
    }

    public static SearchParameters forGroups(final String sqlSearch, final Long officeId, final Long staffId, final String externalId,
            final String name, final String hierarchy, final Integer offset, final Integer limit, final String orderBy,
            final String sortOrder) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final String accountNo = null;
        final Long loanId = null;
        final Long savingsId = null;
        final String mobileNoLength = null;
        return new SearchParameters(sqlSearch, officeId, externalId, name, hierarchy, null, null, offset, maxLimitAllowed, orderBy,
                sortOrder, staffId, accountNo, loanId, savingsId, null, mobileNoLength);
    }

    public static SearchParameters forOffices(final String orderBy, final String sortOrder) {
        final String mobileNoLength = null;
        return new SearchParameters(null, null, null, null, null, null, null, null, null, orderBy, sortOrder, null, null, null, null, null, mobileNoLength);
    }

    public static SearchParameters forLoans(final String sqlSearch, final String externalId, final Integer offset, final Integer limit,
            final String orderBy, final String sortOrder, final String accountNo) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final Long loanId = null;
        final Long savingsId = null;
        final String mobileNoLength = null;

        return new SearchParameters(sqlSearch, null, externalId, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder,
                staffId, accountNo, loanId, savingsId, null, mobileNoLength);
    }

    public static SearchParameters forJournalEntries(final Long officeId, final Integer offset, final Integer limit, final String orderBy,
            final String sortOrder, final Long loanId, final Long savingsId) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final String mobileNoLength = null;

        return new SearchParameters(null, officeId, null, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder, staffId,
                null, loanId, savingsId, null, mobileNoLength);
    }

    public static SearchParameters forPagination(final Integer offset, final Integer limit, final String orderBy, final String sortOrder) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final Long loanId = null;
        final Long savingsId = null;
        final String mobileNoLength = null;

        return new SearchParameters(null, null, null, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder, staffId, null,
                loanId, savingsId, null, mobileNoLength);
    }

    public static SearchParameters forSavings(final String sqlSearch, final String externalId, final Integer offset, final Integer limit,
            final String orderBy, final String sortOrder) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final String accountNo = null;
        final Long loanId = null;
        final Long savingsId = null;
        final String mobileNoLength = null;

        return new SearchParameters(sqlSearch, null, externalId, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder,
                staffId, accountNo, loanId, savingsId, null, mobileNoLength);
    }

    public static SearchParameters forAccountTransfer(final String sqlSearch, final String externalId, final Integer offset,
            final Integer limit, final String orderBy, final String sortOrder) {

        final Integer maxLimitAllowed = getCheckedLimit(limit);
        final Long staffId = null;
        final String accountNo = null;
        final Long loanId = null;
        final Long savingsId = null;
        final String mobileNoLength = null;

        return new SearchParameters(sqlSearch, null, externalId, null, null, null, null, offset, maxLimitAllowed, orderBy, sortOrder,
                staffId, accountNo, loanId, savingsId, null, mobileNoLength);
    }

    private SearchParameters(final String sqlSearch, final Long officeId, final String externalId, final String name,
            final String hierarchy, final String firstname, final String lastname, final Integer offset, final Integer limit,
            final String orderBy, final String sortOrder, final Long staffId, final String accountNo, final Long loanId,
            final Long savingsId, final Long clientStatus, final String mobileNoLength) {
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
        this.clientStatus = clientStatus;
        this.mobileNoLength = mobileNoLength;
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
    
    public boolean isclientStatusPassed() {
        return this.clientStatus != null && this.clientStatus != 0;
    }
    
    public Long getclientStatus() {
        return this.clientStatus;
    }
    public String getMobileNoLength() {
		return this.mobileNoLength;
	}

}