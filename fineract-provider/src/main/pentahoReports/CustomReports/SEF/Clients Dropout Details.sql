/*Report*/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`, `track_usage`) VALUES ('Clients Dropout Details', 'Pentaho', NULL, 'Custom', NULL, NULL, 0, 1, 0);
 
 /*Insert Script*/
 INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Clients Dropout Details'), (select id from `stretchy_parameter` where parameter_name='startDateSelect'), 'startdate');
 
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Clients Dropout Details'), (select id from `stretchy_parameter` where parameter_name='endDateSelect'), 'enddate');

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Clients Dropout Details'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne'), 'branch');
