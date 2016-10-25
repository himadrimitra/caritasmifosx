INSERT IGNORE INTO `stretchy_report` (`report_name`, `report_type`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`)
	VALUES ('Journal Voucher', 'Pentaho', 'Accounting', NULL, 'Journal Voucher', 1, 1);
	
INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) 
	VALUES ((select sr.id from stretchy_report sr where sr.report_name = 'Journal Voucher'), 
	(select sp.id from stretchy_parameter sp where sp.parameter_name = 'transactionId'), 'transactionId');

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
	VALUES ('report', 'READ_JournalVoucher(Pentaho)', 'JournalVoucher(Pentaho)', 'READ', 0);
