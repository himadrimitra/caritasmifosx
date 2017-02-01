/*report*/


INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Active Loans as On Date(SEF) - Details', 'Pentaho', NULL, 'Custom', '(NULL)', '(NULL)', 1, 1);


/*report parameters*/

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans as On Date(SEF) - Details'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne'), 'branch');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans as On Date(SEF) - Details'), (select id from `stretchy_parameter` where parameter_name='loanOfficerIdSelectAll'),'loanOfficer');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans as On Date(SEF) - Details'), (select id from `stretchy_parameter` where parameter_name='currencyIdSelectAll'), 'currencyId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans as On Date(SEF) - Details'), (select id from `stretchy_parameter` where parameter_name='fundIdSelectAll'), 'fundId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans as On Date(SEF) - Details'), (select id from `stretchy_parameter` where parameter_name='loanProductIdSelectAll'), 'loanProductId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans as On Date(SEF) - Details'), (select id from `stretchy_parameter` where parameter_name='loanPurposeIdSelectAll'), 'loanPurposeId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans as On Date(SEF) - Details'), (select id from `stretchy_parameter` where parameter_name='asOnDate'), 'ondate');
