INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Daily Loan Officer Wise Loan Summary', 'Pentaho', NULL, 'Loan', NULL, 'Daily Loan Officer Wise Loan Summary', 0, 1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) VALUES ( (select sr.id from stretchy_report sr where sr.report_name='Daily Loan Officer Wise Loan Summary'), (select sp.id from stretchy_parameter sp where sp.parameter_name='asOnDate'), 'fromDate');

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'READ_Daily Loan Officer Wise Loan Summary', 'Daily Loan Officer Wise Loan Summary', 'READ', 0);
