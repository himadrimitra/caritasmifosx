INSERT IGNORE INTO `m_code` (`code_name`, `is_system_defined`) VALUES ('ClientSubStatus', 1);

INSERT IGNORE INTO `m_code_value` (`code_id`, `code_value`, `code_description`, `order_position`) VALUES 
((SELECT id FROM m_code WHERE code_name = 'ClientSubStatus'), 'New Client', 'New Client', 1), 
((SELECT id FROM m_code WHERE code_name = 'ClientSubStatus'), 'Active in Good Standing', 'Active in Good Standing', 2),
((SELECT id FROM m_code WHERE code_name = 'ClientSubStatus'), 'Dormant', 'Dormant', 3), 
((SELECT id FROM m_code WHERE code_name = 'ClientSubStatus'), 'Default', 'Default', 4);

INSERT INTO `c_configuration` (`name`, `value`, `date_value`, `enabled`, `is_trap_door`, `description`) VALUES
 ('Num-Months-WithDeposits-ForGoodStandingSubStatus', '6', NULL, 1, 0, 'Num-Months-WithDeposits-ForGoodStandingSubStatus'),
 ('Num-Months-WithoutDeposits-ForDormancySubStatus', '4', NULL, 1, 0, 'Num-Months-WithoutDeposits-ForDormancySubStatus'),
 ('Num-Months-WithoutRepayment-ForDefaultSubStatus', '4', NULL, 1, 0, 'Num-Months-WithoutRepayment-ForDefaultSubStatus');

CREATE TABLE `client_sub_status_change_history` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_Id` BIGINT(20) NULL DEFAULT NULL,
	`sub_status` BIGINT(20) NULL DEFAULT NULL,
	`update_date` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `client_index` (`client_Id`)
);

CREATE TABLE `client_sub_status_migration_date` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NULL DEFAULT NULL,
	`sub_status` INT(11) NULL DEFAULT NULL,
	`migrated_date` DATE NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `client_id` (`client_id`)
);

DELIMITER //
CREATE TRIGGER `client_sub_status_batch_job_trigger` AFTER UPDATE ON `m_client` FOR EACH ROW BEGIN
	INSERT INTO 
	client_sub_status_change_history(client_id,sub_status,update_date)
	values
		(NEW.id,NEW.sub_status,NOW());END
		//
