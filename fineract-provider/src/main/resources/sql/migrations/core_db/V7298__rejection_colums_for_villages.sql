ALTER TABLE `chai_villages`
	ADD COLUMN `rejectedon_date` DATE NULL DEFAULT NULL AFTER `submitedon_date`,
	ADD COLUMN `rejectedon_userid` BIGINT(20) NULL DEFAULT NULL AFTER `rejectedon_date`;
	
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
	('portfolio_village', 'REJECT_VILLAGE', 'VILLAGE', 'REJECT', 0),
	('portfolio_village', 'REJECT_VILLAGE_CHECKER', 'VILLAGE', 'REJECT_CHECKER', 0),
	('gis', 'INITIATEWORKFLOW_DISTRICT', 'DISTRICT', 'INITIATEWORKFLOW', 0),
	('organisation', 'INITIATEWORKFLOW_OFFICE', 'OFFICE', 'INITIATEWORKFLOW', 0);
