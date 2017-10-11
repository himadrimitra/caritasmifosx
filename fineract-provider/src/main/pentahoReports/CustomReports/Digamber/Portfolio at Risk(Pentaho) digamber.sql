/*reports*/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`, `track_usage`) VALUES ('Portfolio at Risk(Pentaho) digamber', 'Pentaho', NULL, 'Loan', '(NULL)', '(NULL)', 1, 1, 0);

/*parameter*/


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Portfolio at Risk(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne'), 'Branch');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Portfolio at Risk(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='loanOfficerIdSelectAll'), 'loanOfficer');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Portfolio at Risk(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='currencyIdSelectAll'), 'CurrencyId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Portfolio at Risk(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='fundIdSelectAll'), 'fundId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Portfolio at Risk(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='loanProductIdSelectAll'), 'loanProductId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Portfolio at Risk(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='loanPurposeIdSelectAll'), 'loanPurposeId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Portfolio at Risk(Pentaho) digamber'), (select id from `stretchy_parameter` where parameter_name='parTypeSelect'), 'parType');

/*permissions*/

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'READ_Portfolio at Risk(Pentaho) digamber', 'Portfolio at Risk(Pentaho) digamber', 'READ', 0);
