/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FundApiConstants {

    public static final String FUND_RESOURCE_NAME = "FUND";
    public static final String CREATE_ACTION = "CREATE";
    public static final String UPDATE_ACTION = "UPDATE";
    public static final String READ_ACTION = "READ";
    public static final String DELETE_ACTION = "DELETE";
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    public static final String fundParamName = "fund";
    public static final String idParamName = "id";
    public static final String nameParamName = "name";
    public static final String fundSourceParamName = "fundSource";
    public static final String fundCategoryParamName = "fundCategory";
    public static final String facilityTypeParamName = "facilityType";
    public static final String assignmentStartDateParamName = "assignmentStartDate";
    public static final String assignmentEndDateParamName = "assignmentEndDate";
    public static final String sanctionedDateParamName = "sanctionedDate";
    public static final String sanctionedAmountParamName = "sanctionedAmount";
    public static final String disbursedDateParamName = "disbursedDate";
    public static final String disbursedAmountParamName = "disbursedAmount";
    public static final String maturityDateParamName = "maturityDate";
    public static final String interestRateParamName = "interestRate";
    public static final String fundRepaymentFrequencyParamName = "fundRepaymentFrequency";
    public static final String tenureParamName = "tenure";
    public static final String tenureFrequencyParamName = "tenureFrequency";
    public static final String morotoriumParamName = "morotorium";
    public static final String morotoriumFrequencyParamName = "morotoriumFrequency";
    public static final String loanPortfolioFeeParamName = "loanPortfolioFee";
    public static final String bookDebtHypothecationParamName = "bookDebtHypothecation";
    public static final String cashCollateralParamName = "cashCollateral";
    public static final String personalGuranteeParamName = "personalGurantee";
    public static final String isActiveParamName = "isActive";
    public static final String FIRST_FILE = "file0";
    public static final String entityName = "fundmapping";
    public static final Long fundMappingFolder = 1l;
    public static final String csvFileSize = "csvFileSize";
    public static final String activeParamName = "active";
    public static final String activateOrDeactivateParamName = "activateOrDeactivate";
    public static final String externalIdParamName = "externalId";

    // code values name

    public static final String FUND_SOURCE_CODE_VALUE = "FundSource";
    public static final String FACILITY_TYPE_CODE_VALUE = "FacilityType";
    public static final String CATEGORY_CODE_VALUE = "Category";
    public static final String FUND_REPAYMENT_FREQUENCY_CODE_VALUE = "FundRepaymentFrequency";

    // search code value options
    public static final String GENDER_CODE_VALUE = "Gender";
    public static final String CLIENT_TYPE_CODE_VALUE = "ClientType";
    public static final String CLIENT_CLASSIFICATION_CODE_VALUE = "ClientClassification";
    public static final String LOAN_PURPOSE_CATEGORY_CODE_VALUE = "LoanPurposeGroupType";
    public static final String LOAN_PURPOSE_CODE_VALUE = "LoanPurpose";
    public static final String fundLoanPurposeParamName = "fundLoanPurpose";

    public static final Set<String> FUND_CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName, dateFormatParamName,
            nameParamName, externalIdParamName, fundLoanPurposeParamName, fundSourceParamName, fundCategoryParamName,
            facilityTypeParamName, assignmentStartDateParamName, assignmentEndDateParamName, sanctionedDateParamName,
            sanctionedAmountParamName, disbursedDateParamName, disbursedAmountParamName, maturityDateParamName, interestRateParamName,
            fundRepaymentFrequencyParamName, tenureParamName, tenureFrequencyParamName, morotoriumParamName, morotoriumFrequencyParamName,
            loanPortfolioFeeParamName, bookDebtHypothecationParamName, cashCollateralParamName, personalGuranteeParamName));

    public static final Set<String> FUND_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName, dateFormatParamName,
            nameParamName, externalIdParamName, fundSourceParamName, fundCategoryParamName, facilityTypeParamName,
            assignmentStartDateParamName, assignmentEndDateParamName, sanctionedDateParamName, sanctionedAmountParamName,
            disbursedDateParamName, fundLoanPurposeParamName, disbursedAmountParamName, maturityDateParamName, interestRateParamName,
            fundRepaymentFrequencyParamName, tenureParamName, tenureFrequencyParamName, morotoriumParamName, morotoriumFrequencyParamName,
            loanPortfolioFeeParamName, bookDebtHypothecationParamName, cashCollateralParamName, personalGuranteeParamName,
            isActiveParamName));
    public static final Set<String> FUND_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, nameParamName,
            externalIdParamName, fundSourceParamName, fundCategoryParamName, facilityTypeParamName, assignmentStartDateParamName,
            assignmentEndDateParamName, sanctionedDateParamName, sanctionedAmountParamName, disbursedDateParamName,
            disbursedAmountParamName, maturityDateParamName, interestRateParamName, fundRepaymentFrequencyParamName, tenureParamName,
            tenureFrequencyParamName, morotoriumParamName, morotoriumFrequencyParamName, loanPortfolioFeeParamName,
            bookDebtHypothecationParamName, cashCollateralParamName, personalGuranteeParamName, fundLoanPurposeParamName, "isLoanAssigned"));

    public static Set<String> groupByColumns = new HashSet<>(Arrays.asList("offices", "loanProducts", "loanPurposes",
            "loanPurposeCategories", "funds", "clientClassifications", "genders", "clientTypes", "districts", "states"));

    public static Set<String> clientJoinColumns = new HashSet<>(Arrays.asList("offices", "clientClassifications", "genders", "clientTypes",
            "districts", "states"));

    public static Set<String> addressJoinColumns = new HashSet<>(Arrays.asList("districts", "states"));

    public static final String detailParamName = "detail";
    public static final String summaryParamName = "summary";
    public static final String selectedCriteriaListParamName = "selectedCriteriaList";
    public static final String selectClauseBuilderParamName = "selectClauseBuilder";
    public static final String joinClauseBuilderParamName = "joinClauseBuilder";
    public static final String whereClauseBuilderParamName = "whereClauseBuilder";
    public static final String groupByClauseBuilderParamName = "groupByClauseBuilder";
    public static final String client = "client";
    public static final String address = "address";
    public static final String betweenParamName = "between";
    public static final String operatorParamName = "operator";
    public static final String minParamName = "min";
    public static final String maxParamName = "max";
    public static final String repaymentSchedule = "repaymentSchedule";
    public static final String ownParam = "Own";
    public static final String buyoutParam = "Buyout";
    public static final String securitizationParam = "Securitization";
    public static final String trancheDisburseParam = "trancheDisburse";
    public static final String loanProductsParam = "loanProducts";

    public static final String paidRepaymentSelectClause = " sum(ifnull((select (count(rs.completed_derived)) from m_loan_repayment_schedule rs where l.id = rs.loan_id and rs.completed_derived = 1 group by l.id),0)) as paidRepayment ";
    public static final String pendingRepaymentSelectClause = " sum(ifnull((select (count(rs.completed_derived)) from m_loan_repayment_schedule rs where l.id = rs.loan_id and rs.completed_derived = 0 group by l.id),0)) as pendingRepayment ";
    public static final String approvedDateSelectClause = " l.approvedon_date as approvedDate ";
    public static final String disbursementDateSelectClause = " l.disbursedon_date as disbursementDate ";
    public static final String overDueFromDaysParamName = "overDueFromDays";
    public static final String overDueFromDaysSelectClause = " ifnull(DATEDIFF(CURDATE(),ageing.overdue_since_date_derived),0) as overDueFromDays ";

    public static Map<String, String> selectClauseMap = new HashMap<String, String>() {

        {
            put("offices", " office.id as officeId, office.name as officeName ");
            put("loanProducts", " product.id as productId, product.name as loanProductName ");
            put("genders", " gender.id as genderId ,gender.code_value as genderName ");

            put("clientTypes", " clientType.id as clientTypeId ,clientType.code_value as clientTypeName ");
            put("clientClassifications",
                    " clientClassification.id as clientClassificationId, clientClassification.code_value as clientClassificationName ");
            put("loanPurposes", " loanPurpose.id as loanPurposeId ,loanPurpose.code_value as loanPurposeName ");

            put("ditricts", " district.id as ditrictId, district.district_name as districtName ");
            put("states", " state.id as stateId, state.state_name as stateName ");
            put("approvedDate", " l.approvedon_date as approvedDate ");
            put("disbursementDate", " l.disbursedon_date as disbursementDate ");
            put("funds", " fund.id as fundId ,fund.name as fundName ");
            put("approvedDate", approvedDateSelectClause);
            put("disbursementDate", disbursementDateSelectClause);
            put("paidRepayment", paidRepaymentSelectClause);
            put("pendingRepayment", pendingRepaymentSelectClause);
            put("principalOutstanding", "");
            put("overDueFromDays", overDueFromDaysSelectClause);
            put("trancheDisburse", " product.allow_multiple_disbursals as trancheDisburse ");
        }
    };

    public static Map<String, String> whereClauseMap = new HashMap<String, String>() {

        {
            put("offices", " office.id in  ");
            put("loanProducts", " product.id in  ");
            put("genders", " gender.id in  ");
            put("clientClassifications", " clientClassification.id in  ");
            put("clientTypes", " clientType.id in  ");
            put("loanPurposes", " loanPurpose.id in  ");
            put("districts", " district.id in  ");
            put("states", " state.id in  ");
            put("funds", " fund.id in  ");
            put("approvedDate", " (l.approvedon_date  ");
            put("disbursementDate", " (l.disbursedon_date  ");
            put("paidRepayment",
                    " (ifnull((select (count(rs.completed_derived)) from m_loan_repayment_schedule rs where l.id = rs.loan_id and rs.completed_derived = 1 group by l.id),0) ");
            put("pendingRepayment",
                    " (ifnull((select (count(rs.completed_derived)) from m_loan_repayment_schedule rs where l.id = rs.loan_id and rs.completed_derived = 0 group by l.id),0) ");
            put("principalOutstanding", " (l.principal_outstanding_derived  ");
            put("overDueFromDays", " (ifnull(DATEDIFF(CURDATE(),ageing.overdue_since_date_derived),0) ");

        }
    };

    public static Map<String, String> groupByClauseMap = new HashMap<String, String>() {

        {
            put("offices", " office.id ");
            put("loanProducts", " product.id ");
            put("genders", " gender.id ");
            put("clientClassifications", " clientClassification.id ");
            put("clientTypes", " clientType.id ");
            put("loanPurposes", " loanPurpose.id ");
            put("states", " state.id ");
            put("districts", " district.id ");
            put("funds", " fund.id ");
        }
    };

    public static Map<String, String> joinClauseMap = new HashMap<String, String>() {

        {
            put("client", " LEFT JOIN m_client c on c.id = l.client_id  ");
            put("offices", " LEFT JOIN m_office office on c.office_id = office.id ");
            put("loanProducts", " LEFT JOIN m_product_loan product on product.id = l.product_id ");
            put("genders", " LEFT JOIN m_code_value gender on gender.id = c.gender_cv_id ");
            put("clientTypes", " LEFT JOIN m_code_value clientType on clientType.id = c.client_type_cv_id ");
            put("clientClassifications",
                    " LEFT JOIN m_code_value clientClassification on clientClassification.id = c.client_classification_cv_id ");
            put("loanPurposes", " LEFT JOIN m_code_value loanPurpose on loanPurpose.id = l.loan_purpose_id ");
            put("address",
                    " LEFT JOIN f_address_entity addentity on (addentity.entity_id = c.id and addentity.entity_type_enum = 1 and addentity.is_active = 1) LEFT JOIN f_address address on address.id = addentity.address_id ");
            put("states", " LEFT JOIN f_state state on state.id = address.state_id ");
            put("districts", " LEFT JOIN f_district district on district.id = address.district_id ");
            put("funds", " LEFT JOIN m_fund fund on fund.id = l.fund_id ");
            put("overDueFromDays", " left join m_loan_arrears_aging ageing on ageing.loan_id = l.id ");

        }
    };

    public static StringBuilder summarySelectQuery = new StringBuilder(
            "select count(*) as loanCount,sum(ifnull(l.total_expected_repayment_derived,0.0)) as totalAmount,"
                    + " sum(ifnull(l.total_outstanding_derived,0.0)) as outstandingAmount, sum(ifnull(l.principal_disbursed_derived,0.0)) as disbursedAmount,  sum(ifnull(l.principal_outstanding_derived,0.0)) as principalOutstandingAmount ");

    public static StringBuilder detailSelectQuery = new StringBuilder(
            "select DISTINCT l.id as loanId, c.display_name as clientName , (ifnull(l.total_expected_repayment_derived,0.0)) as totalAmount,"
                    + " (ifnull(l.total_outstanding_derived,0.0)) as outstandingAmount, (ifnull(l.principal_disbursed_derived,0.0)) as disbursedAmount ,  (ifnull(l.principal_outstanding_derived,0.0)) as principalOutstandingAmount ");

    public static StringBuilder fromQuery = new StringBuilder(" from m_loan l ");

    public static Set<String> datesColumns = new HashSet<>(Arrays.asList("approvedDate", "disbursementDate"));

    public static Set<String> repaymentColumns = new HashSet<>(Arrays.asList("pendingRepayment", "paidRepayment"));

    public static Set<String> operatorBasedColumns = new HashSet<>(Arrays.asList("approvedDate", "disbursementDate", "pendingRepayment",
            "paidRepayment", "principalOutstanding", overDueFromDaysParamName));

    public static final Set<String> FUND_SUMMARY_SEARCH_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            selectedCriteriaListParamName, "offices", "genders", "clientTypes", "clientClassifications", "states", "districts",
            "loanProducts", "loanPurposeCategories", "loanPurposes", "approvedDate", "disbursementDate", "pendingRepayment",
            "paidRepayment", "principalOutstanding", "overDueFromDays", "funds", "trancheDisburse", "dateFormat", "locale"));
}
