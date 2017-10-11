/*report*/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Disbursal Report Digamber', 'Pentaho', NULL, 'Loans', NULL, NULL, 0, 1);

/*report parameter*/


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Disbursal Report Digamber'), (select id from `stretchy_parameter` where parameter_name='startDateSelect') 'fromDate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Disbursal Report Digamber'), (select id from `stretchy_parameter` where parameter_name='endDateSelect') 'toDate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Disbursal Report Digamber'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne') 'branch');
