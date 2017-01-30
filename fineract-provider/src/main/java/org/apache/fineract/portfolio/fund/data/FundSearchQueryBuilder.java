/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.data;

import java.util.Set;

public class FundSearchQueryBuilder {

    StringBuilder queryBuilder;
    Set<String> selectedCriteriaList;
    StringBuilder groupByClauseBuilder;
    StringBuilder joinClauseBuilder;
    StringBuilder whereClauseBuilder;
    StringBuilder selectClauseBuilder;
    boolean isSummary;

    public FundSearchQueryBuilder(StringBuilder queryBuilder, Set<String> selectedCriteriaList, StringBuilder groupByClauseBuilder,
            StringBuilder joinClauseBuilder, StringBuilder whereClauseBuilder, StringBuilder selectClauseBuilder,
            boolean isSummary) {
        this.queryBuilder = queryBuilder;
        this.selectedCriteriaList = selectedCriteriaList;
        this.groupByClauseBuilder = groupByClauseBuilder;
        this.joinClauseBuilder = joinClauseBuilder;
        this.whereClauseBuilder = whereClauseBuilder;
        this.selectClauseBuilder = selectClauseBuilder;
        this.isSummary = isSummary;
    }

    public static FundSearchQueryBuilder getQueryBuilder(StringBuilder queryBuilder, Set<String> selectedCriteriaList,
            StringBuilder groupByClauseBuilder, StringBuilder joinClauseBuilder, StringBuilder whereClauseBuilder,
            StringBuilder selectClauseBuilder, boolean isSummary) {
        return new FundSearchQueryBuilder(queryBuilder, selectedCriteriaList, groupByClauseBuilder, joinClauseBuilder, whereClauseBuilder,
                selectClauseBuilder, isSummary);
    }

    public StringBuilder getQueryBuilder() {
        return this.queryBuilder;
    }

    public void setQueryBuilder(StringBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public Set<String> getSelectedCriteriaList() {
        return this.selectedCriteriaList;
    }

    public void setSelectedCriteriaList(Set<String> selectedCriteriaList) {
        this.selectedCriteriaList = selectedCriteriaList;
    }

    public StringBuilder getGroupByClauseBuilder() {
        return this.groupByClauseBuilder;
    }

    public void setGroupByClauseBuilder(StringBuilder groupByClauseBuilder) {
        this.groupByClauseBuilder = groupByClauseBuilder;
    }

    public StringBuilder getJoinClauseBuilder() {
        return this.joinClauseBuilder;
    }

    public void setJoinClauseBuilder(StringBuilder joinClauseBuilder) {
        this.joinClauseBuilder = joinClauseBuilder;
    }

    public StringBuilder getWhereClauseBuilder() {
        return this.whereClauseBuilder;
    }

    public void setWhereClauseBuilder(StringBuilder whereClauseBuilder) {
        this.whereClauseBuilder = whereClauseBuilder;
    }

    public StringBuilder getSelectClauseBuilder() {
        return this.selectClauseBuilder;
    }

    public void setSelectClauseBuilder(StringBuilder selectClauseBuilder) {
        this.selectClauseBuilder = selectClauseBuilder;
    }
    
    public boolean isSummary() {
        return this.isSummary;
    }
    
    public void setSummary(boolean isSummary) {
        this.isSummary = isSummary;
    }
    
    
}
