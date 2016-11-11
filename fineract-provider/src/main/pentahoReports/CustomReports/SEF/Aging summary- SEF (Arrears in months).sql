/*report*/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`, `track_usage`) VALUES ('Aging summary- SEF (Arrears in months)', 'Pentaho', NULL, 'Loan', '(NULL)', '(NULL)', 1, 1, 0);


/*parameter*/

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Aging summary- SEF (Arrears in months)'), (select id from `stretchy_parameter` where parameter_name='OfficeIdSelectOne'), 'Branch');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from `stretchy_report` where report_name='Aging summary- SEF (Arrears in months)'), (select id from `stretchy_parameter` where parameter_name='currencyIdSelectAll'), 'CurrencyId');


/*permission*/

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'READ_Aging summary- SEF (Arrears in months)', 'Aging summary- SEF (Arrears in months)', 'READ', 0);
