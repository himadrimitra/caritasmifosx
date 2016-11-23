/*report*/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Progress Report digamber', 'Pentaho', NULL, 'Accounting', NULL, 'Progress Reports', 0, 1);


/*parameters*/

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Progress Report digamber'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne') 'Branch');
