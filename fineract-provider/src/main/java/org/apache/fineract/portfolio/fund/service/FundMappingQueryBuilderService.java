/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.fund.api.FundApiConstants;
import org.apache.fineract.portfolio.fund.data.FundDataValidator;
import org.apache.fineract.portfolio.fund.data.FundMappingSearchData;
import org.apache.fineract.portfolio.fund.data.FundSearchQueryBuilder;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class FundMappingQueryBuilderService {

    private FromJsonHelper fromApiJsonHelper;
    private final JdbcTemplate jdbcTemplate;
    private final FundDataValidator fundDataValidator;
    private final OfficeReadPlatformService officeReadPlatformService;

    @Autowired
    public FundMappingQueryBuilderService(FromJsonHelper fromApiJsonHelper, final RoutingDataSource dataSource,
            final FundDataValidator fundDataValidator, final OfficeReadPlatformService officeReadPlatformService) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.fundDataValidator = fundDataValidator;
        this.officeReadPlatformService = officeReadPlatformService;
    }

    @SuppressWarnings("null")
    public FundSearchQueryBuilder getSummaryQuery(final String json) {

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        this.fundDataValidator.validateSearchData(JsonCommand.from(this.fromApiJsonHelper, element, null));
        Set<String> selectedCriteriaList = null;
        if (this.fromApiJsonHelper.parameterExists(FundApiConstants.selectedCriteriaListParamName, element)) {
            selectedCriteriaList = new TreeSet<>(Arrays.asList(this.fromApiJsonHelper.extractArrayNamed(
                    FundApiConstants.selectedCriteriaListParamName, element)));
        }
        Locale locale = this.fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject());
        String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(element.getAsJsonObject());
        StringBuilder selectSql = new StringBuilder();
        selectSql = selectSql.append(FundApiConstants.summarySelectQuery);
        StringBuilder whereSql = new StringBuilder(" where l.loan_status_id in "+FundApiConstants.fundAssignmentValidLoanStatusAsString
                + " and l.loan_type_enum in (" + AccountType.INDIVIDUAL.getValue() + " ," + AccountType.JLG.getValue() + ") ");
        boolean isFundsNotSelected = !selectedCriteriaList.contains(FundApiConstants.fundsParamName);
        if(isFundsNotSelected){
            whereSql.append(" and (cv.code_value not in ('Buyout', 'Securitization') or fund.id is null) ");
        }
        StringBuilder groupBySql = new StringBuilder(" group by ");
        StringBuilder joinSql = new StringBuilder();
        int groupCount = 0;
        joinSql.append(FundApiConstants.joinClauseMap.get(FundApiConstants.fundsParamName));
        joinSql.append(FundApiConstants.joinClauseMap.get(FundApiConstants.client));
        joinSql.append(FundApiConstants.joinClauseMap.get(FundApiConstants.officesParamName));
        boolean isAddressJoinColumns = !Collections.disjoint(selectedCriteriaList, FundApiConstants.addressJoinColumns);
        if (isAddressJoinColumns) {
            joinSql.append(FundApiConstants.joinClauseMap.get(FundApiConstants.address));
        }
        if (selectedCriteriaList.contains(FundApiConstants.overDueFromDaysParamName)) {
            joinSql.append(FundApiConstants.joinClauseMap.get(FundApiConstants.overDueFromDaysParamName));
        }
        if (selectedCriteriaList.contains(FundApiConstants.trancheDisburseParam)) {
            whereSql.append(" and product.allow_multiple_disbursals = 1 ");
            if (!selectedCriteriaList.contains(FundApiConstants.loanProductsParam)) {
                joinSql.append(FundApiConstants.joinClauseMap.get(FundApiConstants.loanProductsParam));
            }
        }
        for (String param : selectedCriteriaList) {
            boolean isFromGroupingColumn = FundApiConstants.groupByColumns.contains(param);
            boolean isOperatorBasedColumns = FundApiConstants.operatorBasedColumns.contains(param);
            if (!param.equalsIgnoreCase("principalOutstanding")) {
                selectSql.append(", " + FundApiConstants.selectClauseMap.get(param));
            }
            if (isFromGroupingColumn) {
                groupCount++;
                List<String> ids = new ArrayList<>(Arrays.asList(this.fromApiJsonHelper.extractArrayNamed(param, element)));
                String idsAsString = ids.toString().replace('[', '(').replace(']', ')');
                groupBySql.append((groupCount == 1) ? FundApiConstants.groupByClauseMap.get(param) : " , "
                        + FundApiConstants.groupByClauseMap.get(param));
                if(param.equalsIgnoreCase("offices")){
                    List<Long> allOffices = this.officeReadPlatformService.retrieveAllChildOfficesForDropdown(getIdAsLong(ids));
                    whereSql.append(" and " + FundApiConstants.whereClauseMap.get(param) + allOffices.toString().replace('[', '(').replace(']', ')'));
                }else{
                    whereSql.append(" and " + FundApiConstants.whereClauseMap.get(param) + idsAsString);
                }
                
                if(isFundsNotSelected && !param.equals(FundApiConstants.officesParamName)){
                    joinSql.append(FundApiConstants.joinClauseMap.get(param));
                }                

            } else if (isOperatorBasedColumns) {
                JsonObject object = element.getAsJsonObject().get(param).getAsJsonObject();
                String operator = object.get(FundApiConstants.operatorParamName).getAsString();
                StringBuilder constructWhereClause = constructWhereClauseForOperatorBasedCriteria(operator, object, param, dateFormat,
                        locale);
                whereSql.append(" and " + constructWhereClause);

            } else if (param.equals(FundApiConstants.trancheDisburseParam)) {

            }
        }
        StringBuilder fromQuery = new StringBuilder(FundApiConstants.fromQuery);
        boolean isSummary = true;
        StringBuilder sql = new StringBuilder("").append(selectSql).append(fromQuery).append(joinSql).append(whereSql).append(groupBySql);
        return FundSearchQueryBuilder.getQueryBuilder(sql, selectedCriteriaList, groupBySql, joinSql, whereSql, selectSql, isSummary);
    }
    
    private List<Long> getIdAsLong(List<String> idList){
        List<Long> ids = new ArrayList<>();
        for (String id : idList) {
            ids.add(Long.parseLong(id));
        }
        return ids;
    }

    public StringBuilder constructWhereClauseForOperatorBasedCriteria(String operator, JsonObject object, String param, String dateFormat,
            Locale locale) {

        StringBuilder where = new StringBuilder(FundApiConstants.whereClauseMap.get(param));
        where.append(operator + " ").append(getRangeValue(object, param, FundApiConstants.minParamName, dateFormat, locale));
        if (operator.equalsIgnoreCase(FundApiConstants.betweenParamName)) {
            where.append(" and " + getRangeValue(object, param, FundApiConstants.maxParamName, dateFormat, locale));
        }
        where.append(")");
        return where;
    }

    public String getRangeValue(JsonObject element, String param, String type, String dateFormat, Locale locale) {
        if (FundApiConstants.datesColumns.contains(param)) {
            return "'" + this.fromApiJsonHelper.extractLocalDateNamed(type, element, dateFormat, locale) + "'";
        } else if (FundApiConstants.repaymentColumns.contains(param)) { return this.fromApiJsonHelper.extractIntegerSansLocaleNamed(type,
                element).toString(); }
        return this.fromApiJsonHelper.extractBigDecimalNamed(type, element, locale).toString();
    }

    public static final class SearchDataMapper implements RowMapper<FundMappingSearchData> {
        Set<String> searchCriteriaList ;
        boolean isSummary;
        public SearchDataMapper(Set<String> searchCriteriaList, boolean isSummary) {
            this.searchCriteriaList = searchCriteriaList;
            this.isSummary = isSummary;
        }

        public String schema(FundSearchQueryBuilder fundSearchQueryBuilder) {
            return fundSearchQueryBuilder.getQueryBuilder().toString();
        }

        @SuppressWarnings({ "unused"})
        @Override
        public FundMappingSearchData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            Integer loanCount = null;
            BigDecimal disbursedAmount = rs.getBigDecimal("disbursedAmount");
            BigDecimal principalOutstandingAmount = rs.getBigDecimal("principalOutstandingAmount");
            String clientName = null;
            Long loanId = null;   
            if(this.isSummary){
                loanCount = rs.getInt("loanCount");
            }else{
                clientName = rs.getString("clientName");
                loanId = rs.getLong("loanId"); 
            }
            String officeName = null;
            String loanProductName = null;
            String districtName = null;
            String stateName = null;
            String fundName = null;
            String loanPurposeName = null;
            String loanPurposeGroup = null;
            String genderName = null;
            String clientTypeName = null;
            String clientClassificationName = null;
            Date approvedDate = null;
            Date disbursementDate = null;
            Integer pendingRepayment = null;
            Integer paidRepayment = null;
            Boolean trancheDisburse = false;
            Integer overDueFromDays = null;
            
            for (String column: this.searchCriteriaList) {
                switch (column) {
                    case "offices":
                        officeName = rs.getString("officeName");
                    break;
                    case "genders":
                        genderName = rs.getString("genderName");
                    break;
                    case "clientTypes":
                        clientTypeName = rs.getString("clientTypeName");
                    break;
                    case "clientClassifications":
                        clientClassificationName = rs.getString("clientClassificationName");
                    break;
                    case "loanProducts":
                        loanProductName = rs.getString("loanProductName");
                    break;
                    case "districts":
                        districtName = rs.getString("districtName");
                    break;
                    case "states":
                        stateName = rs.getString("stateName");
                    break;
                    case "funds":
                        fundName = rs.getString("fundName");
                    break;
                    case "loanPurposes":
                        loanPurposeName = rs.getString("loanPurposeName");
                    break;
                    case "loanPurposeCategories":
                        loanPurposeGroup = rs.getString("loanPurposeGroup");
                    break;
                    case "approvedDate":
                        approvedDate = rs.getDate("approvedDate");
                    break;
                    case "disbursementDate":
                        disbursementDate = rs.getDate("disbursementDate");
                    break;
                    case "pendingRepayment":
                        pendingRepayment = rs.getInt("pendingRepayment");
                    break;
                    case "paidRepayment":
                        paidRepayment = rs.getInt("paidRepayment");
                    break;
                    case "trancheDisburse":
                        trancheDisburse = rs.getBoolean("trancheDisburse");
                    break;
                    case "overDueFromDays":
                        overDueFromDays = rs.getInt("overDueFromDays");
                    break;
                }
            }

            return FundMappingSearchData.instance(loanId, disbursedAmount, principalOutstandingAmount, clientName,
                    clientClassificationName, clientTypeName, districtName, fundName, genderName, loanCount, loanProductName,
                    loanPurposeGroup, loanPurposeName, officeName, stateName, approvedDate, disbursementDate, pendingRepayment,
                    paidRepayment, trancheDisburse, overDueFromDays);
        }
    }

    public List<FundMappingSearchData> getSearchedData(FundSearchQueryBuilder fundSearchQueryBuilder) {
        SearchDataMapper rm = new SearchDataMapper(fundSearchQueryBuilder.getSelectedCriteriaList(),fundSearchQueryBuilder.isSummary());
        final String sql = rm.schema(fundSearchQueryBuilder);
        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    public FundSearchQueryBuilder getQuery(final String json, boolean isDeatilQuery) {

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        Set<String> selectedCriteriaList = null;
        if (this.fromApiJsonHelper.parameterExists(FundApiConstants.selectedCriteriaListParamName, element)) {
            selectedCriteriaList = new TreeSet<>(Arrays.asList(this.fromApiJsonHelper.extractArrayNamed(
                    FundApiConstants.selectedCriteriaListParamName, element)));
        }
        String groupByClauseBuilder = "";
        if (this.fromApiJsonHelper.parameterExists(FundApiConstants.groupByClauseBuilderParamName, element)) {
            groupByClauseBuilder = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.groupByClauseBuilderParamName, element);
        }
        String joinClauseBuilder = "";
        if (this.fromApiJsonHelper.parameterExists(FundApiConstants.joinClauseBuilderParamName, element)) {
            joinClauseBuilder = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.joinClauseBuilderParamName, element);
        }
        String whereClauseBuilder = "";
        if (this.fromApiJsonHelper.parameterExists(FundApiConstants.whereClauseBuilderParamName, element)) {
            whereClauseBuilder = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.whereClauseBuilderParamName, element);
        }
        StringBuilder selectClauseBuilder = new StringBuilder();
        if (this.fromApiJsonHelper.parameterExists(FundApiConstants.selectClauseBuilderParamName, element)) {
            String selectClause = this.fromApiJsonHelper.extractStringNamed(FundApiConstants.selectClauseBuilderParamName, element);
            selectClauseBuilder.append(selectClause);
        }
        String selectClause = (isDeatilQuery) ? selectClauseBuilder.toString() : " select l.id ";
        if (isDeatilQuery) {
            selectClause = selectClause.replace("sum(", "(");
            selectClause = selectClause.replace("count(*) as loanCount,", "DISTINCT l.id as loanId, c.display_name as clientName ,");
        }
        StringBuilder sql = new StringBuilder();
        sql = sql.append(selectClause).append(FundApiConstants.fromQuery).append(joinClauseBuilder).append(whereClauseBuilder);
        boolean isSummary = false;
        return FundSearchQueryBuilder.getQueryBuilder(sql, selectedCriteriaList, new StringBuilder(groupByClauseBuilder),
                new StringBuilder(joinClauseBuilder), new StringBuilder(whereClauseBuilder), new StringBuilder(selectClauseBuilder), isSummary);
    }

}
