INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('enitityAuditLogs', 'Table', NULL, NULL, 'SELECT\nlog.id As id,\nlog.action_name As action,\nlog.command_as_json As changes,\nlog.made_on_date As updateDate,\nCONCAT(maker.firstname, maker.lastname) As updateBy,\nCONCAT(checker.firstname, checker.lastname) As approvedBy\nFROM\nm_portfolio_command_source log\nLEFT JOIN m_appuser maker ON maker.id = log.maker_id\nLEFT JOIN  m_appuser checker ON checker.id = log.checker_id\nWHERE log.resource_id = ${resourceId}\nAND log.entity_name = \'${enitityName}\'\nORDER BY log.made_on_date DESC', NULL, 0, 1);

INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('enitityName', 'enitityName', 'Enity Name', 'text', 'string', 'n/a', NULL, NULL, NULL, '', NULL);
INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('resourceId', 'resourceId', 'Resource Id', 'text', 'number', 'n/a', NULL, NULL, NULL, NULL, NULL);

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) 
VALUES ((SELECT sh.id FROM stretchy_report sh WHERE sh.report_name = 'enitityAuditLogs'), 
         (SELECT sh.id FROM stretchy_parameter sh WHERE sh.parameter_name = 'enitityName'), 
		  'enitityName');
		  
		  
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) 
VALUES ((SELECT sh.id FROM stretchy_report sh WHERE sh.report_name = 'enitityAuditLogs'), 
         (SELECT sh.id FROM stretchy_parameter sh WHERE sh.parameter_name = 'resourceId'), 
		  'resourceId');		  


