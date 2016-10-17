-----------------Loan purpose group INSERT Query-----------------------------
 
 -------------------Consuption------------------------------------

INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Income generation-Working capital- intangibles','IGWCI', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Consumption'), 1);
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Income generation-Movable assets','IGMA', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Consumption'), 1);
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Income generation-Immovable assets','IGIA', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Consumption'), 1);
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Non-income generating-Consumption','NIGC', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Consumption'), 1);
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Non-income generating-Asset creation','NIGAC', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Consumption'), 1);
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Non-income generating-Emergency','NIGEM', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Consumption'), 1);
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Non-income generating-Education','NIGED', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Consumption'), 1);
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Income generation-Working capital- tangibles','IGWCT', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Consumption'), 1);



--------------------- Grouping ------------------------

INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Agriculture','AG', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Grouping'), 1);		
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Services','SRV', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Grouping'), 1);		
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Trading','TRD', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Grouping'), 1);		
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Educational Loans','EDL', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Grouping'), 1);		
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Industry - Manufacturing ','IMF', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Grouping'), 1);		
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Agri & Allied ','AGAL', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Grouping'), 1);		
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Commercial Agriculture','CAG', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Grouping'), 1);		
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Personal ','PL', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Grouping'), 1);		
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Family','FAM', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Grouping'), 1);		
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Food ','FD', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Grouping'), 1);		
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Shop','SP', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Grouping'), 1);		
INSERT  INTO f_loan_purpose_group (name, short_name, type_cv_id, is_active) values
('Skilled Occupation','SOP', (SELECT mcv.id FROM m_code_value mcv 
JOIN m_code mc 
ON mcv.code_id = mc.id 
WHERE mc.code_name = 'LoanPurposeGroupType' AND mcv.code_value = 'Grouping'), 1);	


---------------------------Loan Purpose ----------------------------------------------

------Inserting Loan Purpose ---------------

INSERT  INTO f_loan_purpose (name, short_name, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('STEEL BUSINESS','STBS', 1, 1, now(), 1, now());
INSERT  INTO f_loan_purpose (name, short_name, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('MACHINARY PURCHASE','MCPS', 1, 1, now(), 1, now());
INSERT  INTO f_loan_purpose (name, short_name, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('TEA BUSINESS','TBS', 1, 1, now(), 1, now());
INSERT  INTO f_loan_purpose (name, short_name, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('ROPE BUSINESS','RBS', 1, 1, now(), 1, now());
INSERT  INTO f_loan_purpose (name, short_name, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('TOBACCO BUSINESS','TBCBS', 1, 1, now(), 1, now());
INSERT  INTO f_loan_purpose (name, short_name, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('REPAIRS - SERVICES','RPS', 1, 1, now(), 1, now());
INSERT  INTO f_loan_purpose (name, short_name, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('KIRANA-GENERAL STORE','KGS', 1, 1, now(), 1, now());
INSERT  INTO f_loan_purpose (name, short_name, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('HOUSE REPAIRS','HR', 1, 1, now(), 1, now());
INSERT  INTO f_loan_purpose (name, short_name, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('VEGETABLES CULTIVATION','VTC', 1, 1, now(), 1, now());
INSERT  INTO f_loan_purpose (name, short_name, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('TAILORING','TR', 1, 1, now(), 1, now());
INSERT  INTO f_loan_purpose (name, short_name, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('BAMBOO WORK','BW', 1, 1, now(), 1, now());

__________________________Loan Purpose Group Mapping -----------------------------------------------

INSERT  INTO f_loan_purpose_group_mapping (loan_purpose_group_id, loan_purpose_id, is_active)
select (SELECT lpg.id from f_loan_purpose_group lpg WHERE name='Services'),fp.id,1 
from f_loan_purpose  fp
where fp.id>2	

----------------------------------------------------------------------------------------------------------------------------------------------

---------------------Cash Flow (Occupation Group) ------------------------------------------------

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Agriculture','AG', 1, 1, 1, 1, now(), 1, now());

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Labour','LBR', 1, 1, 1, 1, now(), 1, now());

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Self Employed','SE', 1 , 1, 1, 1, now(), 1, now());

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Small business','SBS', 1, 1, 1, 1, now(), 1, now());

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Large business','LRGBS', 1, 1, 1,1, now(), 1, now());

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Transpotation servies','TS', 1, 1, 1, 1, now(), 1, now());

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Live Stock Rearing','LSR', 1, 1, 1,1, now(), 1, now());

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Salaried Employee','SE', 1, 1, 1,1, now(), 1, now());

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Salaried Employee','SEMP', 1, 1, 1,1, now(), 1, now());

--------------------------------Income Expense ------------------------------------------------------

INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Mekke Jhola', 1,'Acre', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Ragi', 1,'Acre', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Ground Nut', 1,'Acre', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Belle', 1,'Acre', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Cotton ', 1,'Acre', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Paddy', 1,'Acre', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Onions', 1,'Acre', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Arecanut', 1,'Acre', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Sunflower', 1,'Acre', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Green Vegetables', 1,'Acre', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Flowers', 1,'Acre', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Fruits', 1,'Acre', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture'), 'Sugar Cane', 1,'Acre', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Labour'), 'Hotel Worker', 1,'No of Family Workers', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Labour'), 'Lorry and bus cleaner', 1,'No of Family Workers', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Labour'), 'Working in mechanic shop', 1,'No of Family Workers', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Labour'), 'house hold work', 1,'No of Family Workers', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Labour'), 'Agri cooli men', 1,'No of Male Workers', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Labour'), 'Agri cooli women', 1,'No of Female Workers', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Labour'), 'Monthly Contarct worker', 1,'No of Family Workers', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Painter', 1,'No of Family workers', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Carpenter', 1,'No of Family workers', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Bar bender', 1,'No of Family workers', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Welding', 1,'No of Family workers', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Maestri', 1,'No of Family workers', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Barber', 1,'No of Family workers', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Electrician', 1,'No of Family workers', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Contractor', 1,'No of Family workers', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'handicrafts', 1,'No of Family workers', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'in-house occupations', 1,'No of Family workers', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Tailor', 1,'No of Family workers', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Driver', 1,'No of Family workers', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Pottery', 1,'No of Family workers', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Basket making', 1,'No of Family workers', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Bamboo work', 1,'No of Family workers', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'Beedi work', 1,'No of Family workers', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'agarbhatis', 1,'No of Family workers', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Self Employed '), 'paper agent', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Small Kirana shop', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Petty shop', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Punture shop', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Tea Shop', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Bangle shop/sales', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Laundry', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Watch repair shop', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Ice cream sellers', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), '“GOODU ANAGADI”', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Hotel', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Bakery', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Fast food', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Catering', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Beauty parlour', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Barber Shop', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Tailoring shop', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Garage', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Mobile Shops', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Chicken/Mutton shop', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Scrap Business', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Cloth shop', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Vegetable selling', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Fruit Selling', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Flower selling', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'plastic selling', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'steel utensils', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Santé Vendors', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Handicraft shop', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'Sari Vendors', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Small business'), 'coconut selling', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Large business'), 'Flour Mill', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Large business'), 'Wood Business', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Large business'), 'Bricks business', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Large business'), 'Steel business', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Large business'), 'Agricultural Trading', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Large business'), 'Agencies', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Large business'), 'Poultry', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Large business'), 'Furniture shop', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Large business'), 'Foot wear shops', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Large business'), 'Live Stock Trading', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Large business'), 'Skin(leather) Merchants', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Large business'), 'Covaa Business', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Transpotation servies'), 'Auto', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Transpotation servies'), 'Car', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Transpotation servies'), 'tempo', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Transpotation servies'), 'Tractor', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Transpotation servies'), 'Auto', 1,'', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Transpotation servies'), 'tempo', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Transpotation servies'), 'Bus', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Transpotation servies'), 'Lorry', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Live Stock Rearing'), 'Sheep', 1,'No of Animals', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Live Stock Rearing'), 'Goat etc', 1,'No of Animals', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Live Stock Rearing'), 'Cow', 1,'No of Milching Animals', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Live Stock Rearing'), 'Buffalo', 1,'No of Milching Animals', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Live Stock Rearing'), 'Jersey and other Hybrid', 1,'No of Milching Animals', 0, 1, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Salaried Employee'), 'Anganavadi', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Salaried Employee'), 'hospital workers', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Salaried Employee'), 'LIC agents', 1,'', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Salaried Employee'), 'State Govt Employers', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Salaried Employee'), 'Central govt employees', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Salaried Employee'), 'Factory worker', 1,'', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Salaried Employee'), 'Garment workers', 1,'', 0, 2, 1, 1, now(), 1, now());



-------------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------ Income Generating Group -------------------------------------------------------------

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Agriculture Land','AGL', 2, 1, 1,1, now(), 1, now());

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('House Rent','HSR', 2, 1, 1, 1, now(), 1, now());

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Vehicle ','VH', 2 , 1, 1,1, now(), 1, now());


--------------------------------------------------------income and expense for Asset ----------------------------------------------------------------------

INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'House Rent'), 'Town Buiding ', 1,'No of House', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'House Rent'), 'Village House', 1,'No of House', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture Land'), 'Tractor ', 1,'No of Vehicles', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture Land'), 'Wet Land ', 1,'Acre', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture Land'), 'Dry Land', 1,'Acre', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture Land'), 'Plantation', 1,'Acre', 0, 3, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Vehicle'), 'Auto ', 1,'No of Vehicles', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Vehicle'), 'Truck', 1,'No of Vehicle', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Vehicle'), 'Tracks ', 1,'No of Vehicle', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Vehicle'), 'Tata - ACE', 1,'No of Vehicle', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture Land'), 'Bullock Cart', 1,'No of Cart', 0, 2, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_quantifier_needed, quantifier_label, is_capture_month_wise_income, stability_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Agriculture Land'), 'Pump Set', 1,'No of Sets', 0, 3, 1, 1, now(), 1, now());

--------------------------------------------------------------------------------------------------------------------------------------------------------------------------


----------------------------------------------------- House Hold Expense Group -----------------------------------------------------------------------

INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('House Hold Expense','HHE',3 ,2, 1,1, now(), 1, now());
INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Utility','UTY',3 ,2, 1,1, now(), 1, now());
INSERT  INTO f_cashflow_category (name, short_name, category_enum_id, type_enum_id, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values
('Default Base Family Expense ','DBFE',3 ,2, 1,1, now(), 1, now());


-------------------------------------------------------------House Hold Expense -----------------------------------------------------------------------------------

INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'House Hold Expense'), 'Food - Ration',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'House Hold Expense'), 'Health',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'House Hold Expense'), 'Education',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'House Hold Expense'), 'Festival',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'House Hold Expense'), 'Family Occasion ',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'House Hold Expense'), 'House Rent',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'House Hold Expense'), 'House Repair',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Utility'), 'Electricity',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Utility'), 'Cable ',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Utility'), 'Mobile ',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Utility'), 'Water ',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'House Hold Expense'), 'Groceries',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Default Base Family Expense '), 'Adult Expense',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Default Base Family Expense '), 'Children Expense',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Default Base Family Expense '), 'Health - desability Expense',0, 1, 1, now(), 1, now());
INSERT INTO f_income_expense 
(cashflow_category_id, name, is_capture_month_wise_income, is_active, createdby_id, created_date, lastmodifiedby_id, lastmodified_date) values ((SELECT id FROM f_cashflow_category WHERE name = 'Default Base Family Expense '), 'Aged Member Expense',0, 1, 1, now(), 1, now());

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


