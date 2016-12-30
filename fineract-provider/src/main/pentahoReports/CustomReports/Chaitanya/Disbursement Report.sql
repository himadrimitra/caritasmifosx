/*Parameter*/

INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('loanProduct', 'loanProduct', 'LoanProduct', 'select', 'number', '0', NULL, NULL, 'Y', 'select p.id, p.name\r\nfrom m_product_loan p\r\n\r\norder by 2', NULL);


/* Report */

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Disbursement Report', 'Pentaho', NULL, 'loan', NULL, NULL, 0, 1);

/*parameter*/

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Disbursement Report'), (select id from `stretchy_parameter` where parameter_name='startDateSelect'), 'fromDate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Disbursement Report'), (select id from `stretchy_parameter` where parameter_name='endDateSelect'), 'toDate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Disbursement Report'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne'), 'selectOffice');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Disbursement Report'), (select id from `stretchy_parameter` where parameter_name='loanOfficerIdSelectAll'), 'loanOfficer');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Disbursement Report'), (select id from `stretchy_parameter` where parameter_name='loanProduct'), 'loanProductId');


