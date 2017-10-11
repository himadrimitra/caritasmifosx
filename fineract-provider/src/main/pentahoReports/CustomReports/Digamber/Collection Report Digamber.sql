/*report*/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Collection Report Digamber', 'Pentaho', NULL, 'Loans', NULL, NULL, 0, 1);


/*report parameter*/

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Collection Report Digamber'), (select id from `stretchy_parameter` where parameter_name='startDateSelect') 'startDate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Collection Report Digamber'), (select id from `stretchy_parameter` where parameter_name='endDateSelect') 'endDate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Collection Report Digamber'), (select id from `stretchy_parameter` where parameter_name='selectAccount') 'selectLoan');
