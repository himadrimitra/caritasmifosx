CREATE TABLE `f_investment_transaction` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`investment_account_id` BIGINT(20) NOT NULL,
	`office_id` BIGINT(20) NOT NULL,
	`transaction_type_enum` SMALLINT(5) NOT NULL,
	`is_reversed` TINYINT(1) NOT NULL,
	`transaction_date` DATE NOT NULL,
	`amount` DECIMAL(19,6) NOT NULL,
	`running_balance` DECIMAL(19,6) NULL DEFAULT NULL,
	`created_date` DATETIME NOT NULL,
	`appuser_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_f_investment_transaction_f_investment_account` (`investment_account_id`),
	INDEX `FK_f_investment_transaction_m_office` (`office_id`),
	CONSTRAINT `FK_f_investment_transaction_f_investment_account` FOREIGN KEY (`investment_account_id`) REFERENCES `f_investment_account` (`id`),
	CONSTRAINT `FK_f_investment_transaction_m_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
