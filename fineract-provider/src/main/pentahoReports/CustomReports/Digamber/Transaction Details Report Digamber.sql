/*reports*/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Transaction Details Report Digamber', 'Pentaho', NULL, 'loan', NULL, NULL, 0, 1);


/*parameter*/

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Transaction Details Report Digamber'), (select id from `stretchy_parameter` where parameter_name='startDateSelect') 'ondate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Transaction Details Report Digamber'), (select id from `stretchy_parameter` where parameter_name='endDateSelect') 'todate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Transaction Details Report Digamber'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne') 'Branch');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Transaction Details Report Digamber'), (select id from `stretchy_parameter` where parameter_name='userIdSelectAll') 'user');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Transaction Details Report Digamber'), (select id from `stretchy_parameter` where parameter_name='transactionTypeSelect') 'transaction');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Transaction Details Report Digamber'), (select id from `stretchy_parameter` where parameter_name='paymentTypeSelect') 'payment');
