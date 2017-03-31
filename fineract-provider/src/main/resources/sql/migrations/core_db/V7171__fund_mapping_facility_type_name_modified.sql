ALTER TABLE `m_fund`
	ADD COLUMN `is_manual_status_update` TINYINT(1) NOT NULL DEFAULT '0';
	
UPDATE  m_code_value m set m.code_value = 'Term Loan' where m.code_value = 'Term';

insert into job (name, display_name, cron_expression, create_time)
values ('Fund Status update', 'Fund Status update', '0 1 1 * * ?', NOW());
