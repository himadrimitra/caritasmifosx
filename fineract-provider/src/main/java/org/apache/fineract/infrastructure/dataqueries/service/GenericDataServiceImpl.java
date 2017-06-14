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
package org.apache.fineract.infrastructure.dataqueries.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.fineract.infrastructure.codes.domain.Code;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.dataqueries.data.AllowedValueOptions;
import org.apache.fineract.infrastructure.dataqueries.data.GenericResultsetData;
import org.apache.fineract.infrastructure.dataqueries.data.ResultsetColumnHeaderData;
import org.apache.fineract.infrastructure.dataqueries.data.ResultsetColumnValueData;
import org.apache.fineract.infrastructure.dataqueries.data.ResultsetRowData;
import org.apache.fineract.infrastructure.dataqueries.data.ResultsetVisibilityCriteriaData;
import org.apache.fineract.infrastructure.dataqueries.data.ScopeCriteriaData;
import org.apache.fineract.infrastructure.dataqueries.domain.DataTableScopes;
import org.apache.fineract.infrastructure.dataqueries.exception.DatatableNotFoundException;
import org.apache.fineract.portfolio.client.domain.LegalForm;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;

@Service
public class GenericDataServiceImpl implements GenericDataService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final static Logger logger = LoggerFactory.getLogger(GenericDataServiceImpl.class);
    private final CodeValueRepositoryWrapper codeValueRepositoryWrapper;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final SavingsProductReadPlatformService savingsProductReadPlatformService;
    

    @Autowired
    public GenericDataServiceImpl(final RoutingDataSource dataSource, final CodeValueRepositoryWrapper codeValueRepositoryWrapper,
            final LoanProductReadPlatformService loanProductReadPlatformService, SavingsProductReadPlatformService savingsProductReadPlatformService) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        this.codeValueRepositoryWrapper = codeValueRepositoryWrapper;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.savingsProductReadPlatformService = savingsProductReadPlatformService;

    }

    @Override
    public GenericResultsetData fillGenericResultSet(final String sql) {

        final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);

        final List<ResultsetColumnHeaderData> columnHeaders = new ArrayList<>();
        final List<ResultsetRowData> resultsetDataRows = new ArrayList<>();

        final SqlRowSetMetaData rsmd = rs.getMetaData();

        for (int i = 0; i < rsmd.getColumnCount(); i++) {

            final String columnName = rsmd.getColumnName(i + 1);
            final String columnType = rsmd.getColumnTypeName(i + 1);

            final ResultsetColumnHeaderData columnHeader = ResultsetColumnHeaderData.basic(columnName, columnType);
            columnHeaders.add(columnHeader);
        }

        while (rs.next()) {
            final List<String> columnValues = new ArrayList<>();
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                final String columnName = rsmd.getColumnName(i + 1);
                final String columnValue = rs.getString(columnName);
                columnValues.add(columnValue);
            }

            final ResultsetRowData resultsetDataRow = ResultsetRowData.create(columnValues);
            resultsetDataRows.add(resultsetDataRow);
        }

        String registeredDataTableDisplayName = null;
        
        return new GenericResultsetData(columnHeaders, resultsetDataRows, registeredDataTableDisplayName);
    }

    @Override
    public String replace(final String str, final String pattern, final String replace) {
        // JPW - this replace may / may not be any better or quicker than the
        // apache stringutils equivalent. It works, but if someone shows the
        // apache one to be about the same then this can be removed.
        int s = 0;
        int e = 0;
        final StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

    @Override
    public String wrapSQL(final String sql) {
        // wrap sql to prevent JDBC sql errors, prevent malicious sql and a
        // CachedRowSetImpl bug

        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7046875 - prevent
        // Invalid Column Name bug in sun's CachedRowSetImpl where it doesn't
        // pick up on label names, only column names
        return "select x.* from (" + sql + ") x";
    }

    @Override
    public String generateJsonFromGenericResultsetData(final GenericResultsetData grs) {

        final StringBuffer writer = new StringBuffer();

        writer.append("[");

        final List<ResultsetColumnHeaderData> columnHeaders = grs.getColumnHeaders();

        final List<ResultsetRowData> data = grs.getData();
        List<String> row;
        Integer rSize;
        final String doubleQuote = "\"";
        final String slashDoubleQuote = "\\\"";
        String currColType;
        String currVal;

        for (int i = 0; i < data.size(); i++) {
            writer.append("\n{");

            row = data.get(i).getRow();
            rSize = row.size();
            for (int j = 0; j < rSize; j++) {

                writer.append(doubleQuote + columnHeaders.get(j).getColumnName() + doubleQuote + ": ");
                currColType = columnHeaders.get(j).getColumnDisplayType();
                final String colType = columnHeaders.get(j).getColumnType();
                if (currColType == null && colType.equalsIgnoreCase("INT")) {
                    currColType = "INTEGER";
                }
                if (currColType == null && colType.equalsIgnoreCase("VARCHAR")) {
                    currColType = "VARCHAR";
                }
                if (currColType == null && colType.equalsIgnoreCase("DATE")) {
                    currColType = "DATE";
                }
                currVal = row.get(j);
                if (currVal != null && currColType != null) {
                    if (currColType.equals("DECIMAL") || currColType.equals("INTEGER")) {
                        writer.append(currVal);
                    } else {
                        if (currColType.equals("DATE")) {
                            final LocalDate localDate = new LocalDate(currVal);
                            writer.append("[" + localDate.getYear() + ", " + localDate.getMonthOfYear() + ", " + localDate.getDayOfMonth()
                                    + "]");
                        } else if (currColType.equals("DATETIME")) {
                            final LocalDateTime localDateTime = new LocalDateTime(currVal);
                            writer.append("[" + localDateTime.getYear() + ", " + localDateTime.getMonthOfYear() + ", "
                                    + localDateTime.getDayOfMonth() + " " + localDateTime.getHourOfDay() + ", "
                                    + localDateTime.getMinuteOfHour() + ", " + localDateTime.getSecondOfMinute() + ", "
                                    + localDateTime.getMillisOfSecond() + "]");
                        } else {
                            writer.append(doubleQuote + replace(currVal, doubleQuote, slashDoubleQuote) + doubleQuote);
                        }
                    }
                } else {
                    writer.append("null");
                }
                if (j < (rSize - 1)) {
                    writer.append(",\n");
                }
            }

            if (i < (data.size() - 1)) {
                writer.append("},");
            } else {
                writer.append("}");
            }
        }

        writer.append("\n]");
        return writer.toString();

    }

    @Override
    public List<ResultsetColumnHeaderData> fillResultsetColumnHeaders(final String datatable) {

        logger.debug("::3 Was inside the fill ResultSetColumnHeader");

        final SqlRowSet columnDefinitions = getDatatableMetaData(datatable);

        final List<ResultsetColumnHeaderData> columnHeaders = new ArrayList<>();

        columnDefinitions.beforeFirst();
        while (columnDefinitions.next()) {
            String columnName = columnDefinitions.getString("COLUMN_NAME");
            final String isNullable = columnDefinitions.getString("IS_NULLABLE");
            final String isPrimaryKey = columnDefinitions.getString("COLUMN_KEY");
            final String columnType = columnDefinitions.getString("DATA_TYPE");
            final Long columnLength = columnDefinitions.getLong("CHARACTER_MAXIMUM_LENGTH");
            String displayName = columnDefinitions.getString("COLUMN_NAME");
            Integer dependsOn = null;
            Long orderPosition =columnDefinitions.getLong("ORDINAL_POSITION");
            Boolean visible = null;
            Boolean mandatoryIfVisible = null;
            Integer watchColumn = null;
            Integer codeValueId = null;
            String dependsOnColumnName = null;
 
            boolean columnNullable = "YES".equalsIgnoreCase(isNullable);
            final boolean columnIsPrimaryKey = "PRI".equalsIgnoreCase(isPrimaryKey);

            List<ResultsetColumnValueData> columnValues = new ArrayList<>();
            List<ResultsetVisibilityCriteriaData> visibilityCriteria = new ArrayList<>();
            List<ResultsetColumnValueData> visibilityCriteriaValues = new ArrayList<>();
            String codeName = null;
            if ("varchar".equalsIgnoreCase(columnType)) {

                final int codePosition = columnName.indexOf("_cv");
                if (codePosition > 0) {
                    codeName = columnName.substring(0, codePosition);

                    columnValues = retreiveColumnValues(codeName);
                }

            } else if ("int".equalsIgnoreCase(columnType)) {

                final int codePosition = columnName.indexOf("_cd");
                if (codePosition > 0) {
                    codeName = columnName.substring(0, codePosition);
                    columnValues = retreiveColumnValues(codeName);
                }
            }
            if (codeName == null) {
                final SqlRowSet rsValues = getDatatableCodeData(datatable, columnName);
                Integer codeId = null;
                while (rsValues.next()) {
                    codeId = rsValues.getInt("id");
                    codeName = rsValues.getString("code_name");
                }
                columnValues = retreiveColumnValues(codeId);

            }
            String metaDataColumnName = null;
            String tempColumnName = null;
            final SqlRowSet rsValues = retriveXRegisteredMetadata(datatable);
            if (columnName.contains("_cd")) {
                tempColumnName = columnName.substring(columnName.lastIndexOf("_") + 1, columnName.length());
            } else {
                tempColumnName = columnName;
            }
            while (rsValues.next()) {
                metaDataColumnName = rsValues.getString("columnName");
                if (tempColumnName != null && tempColumnName.equalsIgnoreCase(metaDataColumnName)) {
                    displayName = rsValues.getString("displayName");
                    dependsOn = rsValues.getInt("dependsOn");
                    dependsOnColumnName = retreiveDependsOnColumnName(dependsOn);
                    orderPosition = rsValues.getLong("orderPosition");
                    if(orderPosition == 0){
                    	orderPosition = columnDefinitions.getLong("ORDINAL_POSITION");
                    }
                    visible = rsValues.getBoolean("visible");
                    watchColumn = rsValues.getInt("watchColumn");
                    codeValueId = rsValues.getInt("codeValueId");
                    if (watchColumn > 0 && codeValueId > 0) {
                        visibilityCriteriaValues = retreiveColumnValuesByCodeValueId(codeValueId);
                        String watchColumnName = retreiveWatchColumnName(watchColumn);
                        visibilityCriteria.add(new ResultsetVisibilityCriteriaData(watchColumnName, visibilityCriteriaValues));
                    }
                    mandatoryIfVisible = rsValues.getBoolean("mandatoryIfVisible");
                    if (visible != null && visible && mandatoryIfVisible != null && mandatoryIfVisible) { 
                        columnNullable = false;
                    }
                }
            }
            /**TODO : Dirty Quick fix for chaitanya**/
            if(columnName.matches("Village Name")){	
            	columnValues = retreiveAllVillages();
            }
            
            final ResultsetColumnHeaderData rsch = ResultsetColumnHeaderData.detailed(columnName, columnType, columnLength, columnNullable,
                    columnIsPrimaryKey, columnValues, codeName, displayName, dependsOnColumnName, orderPosition, visible, mandatoryIfVisible, visibilityCriteria);

            columnHeaders.add(rsch);
        }
        
        
        Collections.sort(columnHeaders);
        return columnHeaders;
    }

    private String retreiveDependsOnColumnName(Integer dependsOn) {
        
        String dependsOnColumnName = null;
        if (dependsOn != null) {
            final String sql = "SELECT xrtm.column_name as columnName FROM x_registered_table_metadata xrtm WHERE xrtm.id = " + dependsOn
                    + "";
            final SqlRowSet rsValues = this.jdbcTemplate.queryForRowSet(sql);

            while (rsValues.next()) {
                dependsOnColumnName = rsValues.getString("columnName");
            }
        }

        return dependsOnColumnName;
    }

    private String retreiveWatchColumnName(Integer watchColumn) {

        String watchColumnName = null;
        final String sql = "SELECT xrtm.column_name as columnName FROM x_registered_table_metadata xrtm WHERE xrtm.id = " + watchColumn
                + "";
        final SqlRowSet rsValues = this.jdbcTemplate.queryForRowSet(sql);

        while (rsValues.next()) {
            watchColumnName = rsValues.getString("columnName");
        }
        return watchColumnName;
    }

    private SqlRowSet retriveXRegisteredMetadata(String datatable) {
        final String sql = "select m.column_name columnName, m.display_name displayName, m.associate_with dependsOn, m.order_position orderPosition, m.visible visible,"
                + "m.mandatory_if_visible mandatoryIfVisible, r.watch_column watchColumn,v.code_value_id codeValueId from x_registered_table x inner join x_registered_table_metadata m on m.registered_table_id = x.id "
                + "left join x_registered_table_display_rules r on r.registered_table_metadata_id = m.id left join x_registered_table_display_rules_value v on v.registered_table_display_rules_id = r.id "
                + " where x.registered_table_name = '" + datatable + "'";
        final SqlRowSet rsValues = this.jdbcTemplate.queryForRowSet(sql);
        return rsValues;
    }

    /*
     * Candidate for using caching there to get allowed 'column values' from
     * code/codevalue tables
     */
    private List<ResultsetColumnValueData> retreiveColumnValues(final String codeName) {

        final List<ResultsetColumnValueData> columnValues = new ArrayList<>();
   
        final String sql = "select v.id, v.code_score, v.code_value, v.parent_id from m_code m " + " join m_code_value v on v.code_id = m.id "
                + " where m.code_name = '" + codeName + "' order by v.order_position, v.id";

        final SqlRowSet rsValues = this.jdbcTemplate.queryForRowSet(sql);

        rsValues.beforeFirst();
        while (rsValues.next()) {
            final Integer id = rsValues.getInt("id");
            final String codeValue = rsValues.getString("code_value");
            final Integer score = rsValues.getInt("code_score");
            final Integer parentId = rsValues.getInt("parent_id");
            columnValues.add(new ResultsetColumnValueData(id, codeValue, score, parentId));
        }

        return columnValues;
    }

    private List<ResultsetColumnValueData> retreiveColumnValues(final Integer codeId) {

        final List<ResultsetColumnValueData> columnValues = new ArrayList<>();
        if (codeId != null) {
            final String sql = "select v.id, v.code_value from m_code_value v where v.code_id =" + codeId
                    + " order by v.order_position, v.id";
            final SqlRowSet rsValues = this.jdbcTemplate.queryForRowSet(sql);
            rsValues.beforeFirst();
            while (rsValues.next()) {
                final Integer id = rsValues.getInt("id");
                final String codeValue = rsValues.getString("code_value");
                columnValues.add(new ResultsetColumnValueData(id, codeValue));
            }
        }

        return columnValues;
    }
    
    private List<ResultsetColumnValueData> retreiveColumnValuesByCodeValueId(final Integer codeValueId) {

        final List<ResultsetColumnValueData> columnValues = new ArrayList<>();
        if (codeValueId != null) {
            final String sql = "select v.id, v.code_value from m_code_value v where v.id =" + codeValueId
                    + " order by v.order_position, v.id";
            final SqlRowSet rsValues = this.jdbcTemplate.queryForRowSet(sql);
            rsValues.beforeFirst();
            while (rsValues.next()) {
                final Integer id = rsValues.getInt("id");
                final String codeValue = rsValues.getString("code_value");
                columnValues.add(new ResultsetColumnValueData(id, codeValue));
            }
        }

        return columnValues;
    }
    
    private SqlRowSet getDatatableMetaData(final String datatable) {

        final String sql = "select COLUMN_NAME, IS_NULLABLE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, COLUMN_KEY, ORDINAL_POSITION"
                + " from INFORMATION_SCHEMA.COLUMNS " + " where TABLE_SCHEMA = schema() and TABLE_NAME = '" + datatable
                + "'order by ORDINAL_POSITION";

        final SqlRowSet columnDefinitions = this.jdbcTemplate.queryForRowSet(sql);
        if (columnDefinitions.next()) { return columnDefinitions; }

        throw new DatatableNotFoundException(datatable);
    }

    private SqlRowSet getDatatableCodeData(final String datatable, final String columnName) {

        final String sql = "select mc.id,mc.code_name from m_code mc join x_table_column_code_mappings xcc on xcc.code_id = mc.id where xcc.column_alias_name='"
                + datatable.toLowerCase().replaceAll("\\s", "_") + "_" + columnName + "'";
        final SqlRowSet rsValues = this.jdbcTemplate.queryForRowSet(sql);

        return rsValues;
    }
    
    /**
     * Quick Fix: Dirty code fetches villages for Chaitanya
     * 
     * @return
     */
    private List<ResultsetColumnValueData> retreiveAllVillages() {

        final List<ResultsetColumnValueData> columnValues = new ArrayList<>();

        final String sql = "select cv.id as id, cv.village_name as village  from chai_villages cv group by cv.id";

        final SqlRowSet rsValues = this.jdbcTemplate.queryForRowSet(sql);
        rsValues.beforeFirst();
        while (rsValues.next()) {
            final Integer id = rsValues.getInt("id");
            final String villageName = rsValues.getString("village");

            columnValues.add(new ResultsetColumnValueData(id, villageName));
        }

        return columnValues;
    }

    @Override
    public List<ScopeCriteriaData> fetchDatatableScopesByIdAndScopingCriteria(Integer id, Long scopingCriteriaEnum) {
        
        List<ScopeCriteriaData> scopeCriteriaData = new ArrayList<>(); 
        
        final String sql = "select rtf.loan_product_id as loanProductId, rtf.savings_product_id as savingsProductId, rtf.code_value_id as"
                + " codeValueId, rtf.legal_form_enum as legalForm  from f_registered_table_scoping rtf where rtf.registered_table_id = "+id+"";

        final SqlRowSet rsValues = this.jdbcTemplate.queryForRowSet(sql);
        
        if (scopingCriteriaEnum != null && scopingCriteriaEnum > 0) {
            if (DataTableScopes.fromInt(scopingCriteriaEnum.intValue()).isLoanProduct()) {
                List<AllowedValueOptions> allowedValues = new ArrayList<>();
                EnumOptionData data = null;
                while (rsValues.next()) {
                    final Long loanProductId = rsValues.getLong("loanProductId");
                    if (loanProductId != null && loanProductId > 0) {
                        data = DataTableScopes.dataTableScopes(DataTableScopes.LOAN_PRODUCT.getId());
                        LoanProductData loanProductData = this.loanProductReadPlatformService.retrieveLoanProduct(loanProductId);
                        allowedValues.add(AllowedValueOptions.createNew(loanProductData.getId(), loanProductData.getName()));
                    }
                }
                scopeCriteriaData.add(ScopeCriteriaData.createNew(data.getId(), data.getCode(), data.getValue(), allowedValues));
            } else if (DataTableScopes.fromInt(scopingCriteriaEnum.intValue()).isSavingsProduct()) {
                List<AllowedValueOptions> allowedValues = new ArrayList<>();
                EnumOptionData data = null;
                while (rsValues.next()) {
                    final Long savingsProductId = rsValues.getLong("savingsProductId");
                    if (savingsProductId != null && savingsProductId > 0) {
                        data = DataTableScopes.dataTableScopes(DataTableScopes.SAVINGS_PRODUCT.getId());
                        SavingsProductData savingsProductData = this.savingsProductReadPlatformService.retrieveOne(savingsProductId);
                        allowedValues.add(AllowedValueOptions.createNew(savingsProductData.getId(), savingsProductData.getName()));
                    }
                }
                scopeCriteriaData.add(ScopeCriteriaData.createNew(data.getId(), data.getCode(), data.getValue(), allowedValues));
            } else if (DataTableScopes.fromInt(scopingCriteriaEnum.intValue()).isClientType()
                    || DataTableScopes.fromInt(scopingCriteriaEnum.intValue()).isClientClassification()) {
                List<AllowedValueOptions> allowedValues = new ArrayList<>();
                EnumOptionData data = null;
                while (rsValues.next()) {
                    final Long codeValueId = rsValues.getLong("codeValueId");
                    if (codeValueId != null && codeValueId > 0) {
                        CodeValue codeValueData = this.codeValueRepositoryWrapper.findOneWithNotFoundDetection(codeValueId);
                        Code code = codeValueData.getCode();
                        if (code.name().equals("ClientType")) {
                            data = DataTableScopes.dataTableScopes(DataTableScopes.CLIENT_TYPE.getId());
                            allowedValues.add(AllowedValueOptions.createNew(codeValueData.getId(), codeValueData.label()));
                        } else {
                            data = DataTableScopes.dataTableScopes(DataTableScopes.CLIENT_CLASSIFICATION.getId());
                            allowedValues.add(AllowedValueOptions.createNew(codeValueData.getId(), codeValueData.label()));
                        }
                    }
                }
                scopeCriteriaData.add(ScopeCriteriaData.createNew(data.getId(), data.getCode(), data.getValue(), allowedValues));
            } else if (DataTableScopes.fromInt(scopingCriteriaEnum.intValue()).isClientLegalForm()) {
                List<AllowedValueOptions> allowedValues = new ArrayList<>();
                EnumOptionData data = null;
                while (rsValues.next()) {
                    final Long legalForm = rsValues.getLong("legalForm");
                    if (legalForm != null && legalForm > 0) {
                        data = DataTableScopes.dataTableScopes(DataTableScopes.CLIENT_LEGAL_FORM.getId());
                        EnumOptionData legalFormType = LegalForm.legalFormType(legalForm.intValue());
                        allowedValues.add(AllowedValueOptions.createNew(legalFormType.getId(), legalFormType.getValue()));
                    }
                }
                scopeCriteriaData.add(ScopeCriteriaData.createNew(data.getId(), data.getCode(), data.getValue(), allowedValues));
            }
        }
        
/*        if (appTableName != null && appTableName.equals("m_loan")) {
            List<AllowedValueOptions> allowedValues = new ArrayList<>();
            EnumOptionData data = null;
            while (rsValues.next()) {
                final Long loanProductId = rsValues.getLong("loanProductId");
                if (loanProductId != null && loanProductId > 0) {
                    data = DataTableScopes.dataTableScopes(DataTableScopes.LOAN_PRODUCT.getId());
                    LoanProductData loanProductData = this.loanProductReadPlatformService.retrieveLoanProduct(loanProductId);
                    allowedValues.add(AllowedValueOptions.createNew(loanProductData.getId(), loanProductData.getName()));
                }
            }
            scopeCriteriaData.add(ScopeCriteriaData.createNew(data.getId(), data.getCode(), data.getValue(), allowedValues));
        } else if(appTableName != null && appTableName.equals("m_savings_account")) {
            List<AllowedValueOptions> allowedValues = new ArrayList<>();
            EnumOptionData data = null;
            while (rsValues.next()) {
                final Long savingsProductId = rsValues.getLong("savingsProductId");
                if (savingsProductId != null && savingsProductId > 0) {
                    data = DataTableScopes.dataTableScopes(DataTableScopes.SAVINGS_PRODUCT.getId());
                    SavingsProductData savingsProductData = this.savingsProductReadPlatformService.retrieveOne(savingsProductId);
                    allowedValues.add(AllowedValueOptions.createNew(savingsProductData.getId(), savingsProductData.getName()));
                }
            }
            scopeCriteriaData.add(ScopeCriteriaData.createNew(data.getId(), data.getCode(), data.getValue(), allowedValues));
        } else if(appTableName != null && appTableName.equals("m_client")) {
            EnumOptionData data = null;
            while (rsValues.next()) {
                List<AllowedValueOptions> allowedValues = new ArrayList<>();
                final Long codeValueId = rsValues.getLong("codeValueId");
                final Long legalForm = rsValues.getLong("legalForm");
                
                if (codeValueId != null && codeValueId > 0) {
                    CodeValue codeValueData = this.codeValueRepositoryWrapper.findOneWithNotFoundDetection(codeValueId);
                    Code code = codeValueData.getCode();
                    if (code.name().equals("ClientType")) {
                        data = DataTableScopes.dataTableScopes(DataTableScopes.CLIENT_TYPE.getId());
                        allowedValues.add(AllowedValueOptions.createNew(codeValueData.getId(), codeValueData.label()));
                        scopeCriteriaData.add(ScopeCriteriaData.createNew(data.getId(), data.getCode(), data.getValue(), allowedValues));
                    } else {
                        data = DataTableScopes.dataTableScopes(DataTableScopes.CLIENT_CLASSIFICATION.getId());
                        allowedValues.add(AllowedValueOptions.createNew(codeValueData.getId(), codeValueData.label()));
                        scopeCriteriaData.add(ScopeCriteriaData.createNew(data.getId(), data.getCode(), data.getValue(), allowedValues));
                    }
                } else if(legalForm != null && legalForm > 0) {
                    data = DataTableScopes.dataTableScopes(DataTableScopes.CLIENT_LEGAL_FORM.getId());
                    EnumOptionData legalFormType = LegalForm.legalFormType(legalForm.intValue());
                    allowedValues.add(AllowedValueOptions.createNew(legalFormType.getId(), legalFormType.getValue()));
                    scopeCriteriaData.add(ScopeCriteriaData.createNew(data.getId(), data.getCode(), data.getValue(), allowedValues));
                }
            }
        }*/         
        return scopeCriteriaData;
    }
}