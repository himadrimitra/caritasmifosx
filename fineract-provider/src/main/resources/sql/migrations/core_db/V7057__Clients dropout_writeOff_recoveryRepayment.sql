/*Stretchy Parameter*/


INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ( 'OfficeIdSelectAll', 'officeId', 'Office', 'select', 'number', '0', NULL, 'Y', NULL, 'select id, \r\nconcat(substring("........................................", 1, \r\n   \r\n\r\n((LENGTH(`hierarchy`) - LENGTH(REPLACE(`hierarchy`, \'.\', \'\')) - 1) * 4)), \r\n   `name`) as tc\r\nfrom m_office\r\nwhere hierarchy like concat\r\n\r\n(\'${currentUserHierarchy}\', \'%\')\r\n\r\n\r\nUNION \r\nSELECT -1, "All"\r\norder by 1\r\n\r\n                                                                                                      ', NULL);

INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ( 'WriteOffReasonSelectAll', 'writeOffId', 'writeOffId', 'select', 'number', '0', NULL, 'Y', '', 'select mcv.id,mcv.code_value\r\nFROM m_code_value mcv \r\nWHERE mcv.code_id = 26\r\n\r\nUNION\r\nSELECT -1, "All"\r\norder by 1                                                                                          ', NULL);


/* Clients Dropout Details */

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Clients Dropout Details', 'Pentaho', NULL, 'Client', NULL, NULL, 0, 1);


INSERT INTO `stretchy_report_parameter` ( `report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Clients DropOut Details'),(select id from `stretchy_parameter` where parameter_name='OfficeIdSelectAll'),'branch');


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Clients DropOut Details'),(select id from `stretchy_parameter` where parameter_name='startDateSelect'), 'startdate');


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Clients DropOut Details'),(select id from `stretchy_parameter` where parameter_name='endDateSelect'), 'enddate');


/* Custom written-off report */

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Custom written-off report', 'Pentaho', NULL, 'Client', NULL, NULL, 0, 1);


INSERT INTO `stretchy_report_parameter` ( `report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Custom written-off report'),(select id from `stretchy_parameter` where parameter_name='OfficeIdSelectAll'),'branch');

INSERT INTO `stretchy_report_parameter` ( `report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Custom written-off report'),(select id from `stretchy_parameter` where parameter_name='WriteOffReasonSelectAll'),'writeOffId');


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Custom written-off report'),(select id from `stretchy_parameter` where parameter_name='startDateSelect'), 'startdate');


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Custom written-off report'),(select id from `stretchy_parameter` where parameter_name='endDateSelect'), 'enddate');


/* Custom written-off repayment */


INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Custom written-off repayment', 'Pentaho', NULL, 'Client', NULL, NULL, 0, 1);


INSERT INTO `stretchy_report_parameter` ( `report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Custom written-off repayment'),(select id from `stretchy_parameter` where parameter_name='OfficeIdSelectAll'),'branch');

INSERT INTO `stretchy_report_parameter` ( `report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Custom written-off repayment'),(select id from `stretchy_parameter` where parameter_name='WriteOffReasonSelectAll'),'writeOffId');


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Custom written-off repayment'),(select id from `stretchy_parameter` where parameter_name='startDateSelect'), 'startdate');


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Custom written-off repayment'),(select id from `stretchy_parameter` where parameter_name='endDateSelect'), 'enddate');