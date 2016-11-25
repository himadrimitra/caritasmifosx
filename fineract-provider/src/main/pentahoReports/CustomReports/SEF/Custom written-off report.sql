/*parameter*/

INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('WriteOffReasonSelectAll', 'writeOffId', 'writeOffId', 'select', 'number', '0', NULL, 'Y', '', 'select mcv.id,mcv.code_value\r\nFROM m_code_value mcv \r\nWHERE mcv.code_id = (SELECT m.id from m_code m where m.code_name=\'WriteOffReasons\')\r\n\r\nUNION\r\nSELECT -1, "All"\r\norder by 1                                                                                                      ', NULL);


/*report*/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`, `track_usage`) VALUES ('Custom written-off report', 'Pentaho', NULL, 'Client', NULL, NULL, 0, 1, 0);


/*report parameter*/

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Custom written-off report'), (select id from `stretchy_parameter` where parameter_name='startDateSelect'), 'startdate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Custom written-off report'), (select id from `stretchy_parameter` where parameter_name='startDateSelect'), 'enddate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Custom written-off report'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectAll'), 'branch');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Custom written-off report'), (select id from `stretchy_parameter` where parameter_name='WriteOffReasonSelectAll'), 'writeOffId');


