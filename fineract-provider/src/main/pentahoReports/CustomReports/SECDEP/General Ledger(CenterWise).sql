/*Report*/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('General Ledger(CenterWise)', 'Pentaho', NULL, 'Accounting', NULL, NULL, 0, 1);

/*report_parameter*/

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='General Ledger(CenterWise)'),(select id from `stretchy_parameter` where parameter_name='startDateSelect'), 'fromDate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='General Ledger(CenterWise)'), (select id from `stretchy_parameter` where parameter_name='endDateSelect'), 'toDate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='General Ledger(CenterWise)'),(select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne'), 'office');


/*permissions*/

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'READ_General Ledger(CenterWise)', 'General Ledger(CenterWise)', 'READ', 0);
