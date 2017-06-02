INSERT INTO stretchy_report (report_name, report_type, report_subtype, report_category, report_sql, description, core_report, use_report, track_usage) VALUES ('NACH Transaction Details', 'Pentaho', NULL, 'Loans', NULL, NULL, 0, 1, 0);

INSERT INTO stretchy_parameter (parameter_name, parameter_variable, parameter_label, parameter_displayType, parameter_FormatType, parameter_default, special, selectOne, selectAll, parameter_sql, parent_id) VALUES ('statusId', 'statusId', 'Select Status', 'select', 'number', '0', NULL, 'Y', NULL,
 "SELECT 3 AS id,
 'Success' AS value
 UNION
 SELECT 4,'Failed'
 ", NULL);

INSERT INTO stretchy_report_parameter (report_id, parameter_id, report_parameter_name) 
VALUES ((SELECT id FROM stretchy_report WHERE report_name = 'NACH Transaction Details'), 
(SELECT id FROM stretchy_parameter WHERE parameter_name = 'startDateSelect'), 'startDate');

INSERT INTO stretchy_report_parameter (report_id, parameter_id, report_parameter_name) 
VALUES ((SELECT id FROM stretchy_report WHERE report_name = 'NACH Transaction Details'), 
(SELECT id FROM stretchy_parameter WHERE parameter_name = 'endDateSelect'), 'endDate');

INSERT INTO stretchy_report_parameter (report_id, parameter_id, report_parameter_name) 
VALUES ((SELECT id FROM stretchy_report WHERE report_name = 'NACH Transaction Details'), 
(SELECT id FROM stretchy_parameter WHERE parameter_name = 'OfficeIdSelectOne'), 'branch');

INSERT INTO stretchy_report_parameter (report_id, parameter_id, report_parameter_name) 
VALUES ((SELECT id FROM stretchy_report WHERE report_name = 'NACH Transaction Details'), 
(SELECT id FROM stretchy_parameter WHERE parameter_name = 'statusId'), 'statu');

INSERT INTO m_permission (grouping, code, entity_name, action_name, can_maker_checker) VALUES ('report', 'READ_NACH Transaction Details', 'NACH Transaction Details', 'READ', 0);

