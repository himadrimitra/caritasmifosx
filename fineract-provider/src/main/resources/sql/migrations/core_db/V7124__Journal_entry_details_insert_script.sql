/*parameter*/
INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('JournalEntriesType', 'entriesType', 'EntriesType', 'select', 'number', '0', NULL, 'Y', NULL, 'SELECT 2  as id,"System Entries" as vlue\r\nUNION\r\nSELECT 1 ,"Manual Entries"\r\nUNION\r\nSELECT -1 ,"All"', NULL);


/*report*/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Journal Entry - Details', 'Pentaho', NULL, 'Accounting', NULL, 'Journal Entry - Details report', 1, 1);


/*report parameters*/

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Journal Entry - Details'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne'), 'office');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Journal Entry - Details'), (select id from `stretchy_parameter` where parameter_name='startDateSelect'), 'startDate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Journal Entry - Details'),(select id from `stretchy_parameter` where parameter_name='endDateSelect'), 'endDate');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Journal Entry - Details'),(select id from `stretchy_parameter` where parameter_name='SelectGLAccountNO'), 'glid');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Journal Entry - Details'),(select id from `stretchy_parameter` where parameter_name='JournalEntriesType'), 'meId');


/*permissions*/

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'READ_Journal Entry - Details', 'Journal Entry - Details', 'READ', 0);
