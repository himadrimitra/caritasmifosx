/*report*/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`, `track_usage`) VALUES ('Balance Outstanding(SEF)', 'Pentaho', NULL, 'Loan', '(NULL)', '(NULL)', 1, 1, 0);

/*parameter*/

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Balance Outstanding(SEF)'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne'), 'branch');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Balance Outstanding(SEF)'), (select id from `stretchy_parameter` where parameter_name='asOnDate'), 'ondate');

/*permission*/

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'READ_Balance Outstanding(SEF)', 'Balance Outstanding(SEF)', 'READ', 0);

