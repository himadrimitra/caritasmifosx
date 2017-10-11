ALTER TABLE `m_portfolio_command_source`
	ADD COLUMN `option_type` VARCHAR(50) NULL AFTER `transaction_id`;
	
DELETE FROM `m_permission` WHERE  entity_name like '%GLIM%';
