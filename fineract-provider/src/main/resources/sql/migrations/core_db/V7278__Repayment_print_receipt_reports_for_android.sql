-- PARAMETER --

INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('collectionsheetId', 'collectionsheetId', 'Collection Sheet ID', 'text', 'string', 'n/a', NULL, NULL, NULL, NULL, NULL);

-- REPORT (Individual Receipt) -- 

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`, `track_usage`) VALUES ('Individual Receipt', 'Table', NULL, 'Custom', 'SELECT \'Chaitanya India Fin Credit Pvt,ltd.\' AS \'Company Name\',\nmo.name \'Branch Name\',\nCONCAT(mo.external_id,\'-\',mpd.receipt_number) \'Receipt No.\',\nmlt.created_date \'Transaction Date\',\nms.display_name \'Staff Name\',\nmc.display_name \'Client Name\',\nmpl.short_name \'Product Name\',\nml.account_no \'Loan No.\',\nROUND(SUM(IFNULL(mlrs.principal_amount,0)) + SUM(IFNULL(mlrs.interest_amount,0)) +\nSUM(IFNULL(mlrs.penalty_charges_amount,0)) + SUM(IFNULL(mlrs.fee_charges_amount,0)),2) \'Instalment Due Rs\',\nROUND(mlt.amount,2) \'Paid Rs\',\nmpt.value \'Payment Type\'\nFROM m_office mo\nJOIN m_client mc ON mc.office_id = mo.id\nJOIN m_loan ml ON ml.client_id = mc.id\nJOIN m_product_loan mpl ON mpl.id = ml.product_id\nJOIN m_loan_transaction mlt ON mlt.loan_id = ml.id\nJOIN m_loan_transaction_repayment_schedule_mapping map ON map.loan_transaction_id = mlt.id\nJOIN m_loan_repayment_schedule mlrs ON mlrs.id = map.loan_repayment_schedule_id\nLEFT JOIN m_staff ms ON ms.id = ml.loan_officer_id\nLEFT JOIN m_payment_detail mpd ON mpd.id = mlt.payment_detail_id\nLEFT JOIN m_payment_type mpt ON mpt.id = mpd.payment_type_id\nWHERE mlt.id = \'${transactionId}\'', NULL, 0, 1, 0);

-- REPORT (Collection Receipt) -- 

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`, `track_usage`) VALUES ('Collection Receipt', 'Table', NULL, 'Custom', 'SELECT \'Chaitanya India Fin Credit Pvt,ltd.\' AS \'Company Name\',\nmo.name \'Branch Name\',\nCONCAT(mo.external_id,\'-\',mpd.receipt_number) \'Receipt No.\',\nmlt.created_date \'Transaction Date\',\nms.display_name \'Staff Name\',\nmlrs.installment \'Repayment No.\',\ncen.display_name \'Center Name\',\nGROUP_CONCAT(DISTINCT mpl.short_name) \'Product Name\',\nCOUNT(DISTINCT ml.id) \'No. Of Loans\',\nROUND(SUM(IFNULL(mlrs.principal_amount,0)) + SUM(IFNULL(mlrs.interest_amount,0)) +\nSUM(IFNULL(mlrs.penalty_charges_amount,0)) + SUM(IFNULL(mlrs.fee_charges_amount,0)),2) \'Instalment Due Rs\',\nROUND(SUM(mlt.amount),2) \'Paid Rs\',\nmpt.value \'Payment Type\'\nFROM m_office mo\nJOIN m_group cen ON cen.office_id = mo.id\nJOIN m_group gp ON gp.parent_id = cen.id\nJOIN m_group_client mgc ON mgc.group_id = gp.id\nJOIN m_client mc ON mc.id = mgc.client_id\nJOIN m_loan ml ON ml.client_id = mc.id\nJOIN m_product_loan mpl ON mpl.id = ml.product_id\nJOIN f_collection_sheet_transaction_details cstd ON cstd.entity_id = ml.id\nJOIN f_collection_sheet cs ON cs.id = cstd.collection_sheet_id\nJOIN m_loan_transaction mlt ON mlt.id = cstd.transaction_id\nJOIN m_loan_transaction_repayment_schedule_mapping map ON map.loan_transaction_id = mlt.id\nJOIN m_loan_repayment_schedule mlrs ON mlrs.id = map.loan_repayment_schedule_id\nLEFT JOIN m_staff ms ON ms.id = cs.staff_id\nLEFT JOIN m_payment_detail mpd ON mpd.id = mlt.payment_detail_id\nLEFT JOIN m_payment_type mpt ON mpt.id = mpd.payment_type_id\nWHERE cstd.collection_sheet_id  = \'${collectionsheetId}\'', NULL, 0, 1, 0);

-- Report Parameter (Individual Receipt) -- 

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) 
VALUES ((SELECT sr.id FROM stretchy_report sr WHERE sr.report_name = 'Individual Receipt'), (SELECT sp.id FROM stretchy_parameter sp WHERE sp.parameter_name = 'transactionId'), 'transactionId');


-- Report Parameter (Collection Receipt) -- 

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) 
VALUES ((SELECT sr.id FROM stretchy_report sr WHERE sr.report_name = 'Collection Receipt'), (SELECT sp.id FROM stretchy_parameter sp WHERE sp.parameter_name = 'collectionsheetId'), 'collectionsheetId');


-- PERMISSION -- 

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'READ_Individual receipt', 'Individual receipt', 'READ', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'READ_Collection Receipt', 'Collection Receipt', 'READ', 0);


