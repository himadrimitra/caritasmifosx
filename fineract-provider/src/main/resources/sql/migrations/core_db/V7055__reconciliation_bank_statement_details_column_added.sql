ALTER TABLE  f_bank_statement_details 
	ADD COLUMN `updated_date` DATETIME NULL DEFAULT NULL;
	
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('BANKSTATEMENTDETAILS', 'UNDORECONCILE_BANKSTATEMENTDETAILS', 'BANKSTATEMENTDETAILS', 'UNDORECONCILE', 0)