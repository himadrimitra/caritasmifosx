/*report*/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Balance Outstanding Digamber', 'Pentaho', NULL, 'Loans', NULL, NULL, 0, 1);

/*report parameter*/

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Balance Outstanding Digamber'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne') 'branch');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Balance Outstanding Digamber'), (select id from `stretchy_parameter` where parameter_name='asOnDate') 'ondate');
