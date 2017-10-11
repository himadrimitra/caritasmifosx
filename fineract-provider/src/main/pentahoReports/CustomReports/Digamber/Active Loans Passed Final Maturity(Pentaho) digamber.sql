/*report*/


INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`, `track_usage`) VALUES ('Active Loans Passed Final Maturity(Pentaho) digamber', 'Pentaho', NULL, 'Loan', '(NULL)', '(NULL)', 1, 1, 0);

/*parameter*/


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans Passed Final Maturity(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne'), 'Branch');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans Passed Final Maturity(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='loanOfficerIdSelectAll'), 'Loan Officer');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans Passed Final Maturity(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='currencyIdSelectAll'), 'CurrencyId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans Passed Final Maturity(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='fundIdSelectAll'), 'fundId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans Passed Final Maturity(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='loanProductIdSelectAll'), 'loanProductId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Active Loans Passed Final Maturity(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='loanPurposeIdSelectAll'), 'loanPurposeId');

/*permission*/

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'READ_Active Loans Passed Final Maturity(Pentaho) digamber', 'Active Loans Passed Final Maturity(Pentaho) digamber', 'READ', 0);
