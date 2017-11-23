ALTER TABLE `f_investment_account_savings_linkages` ADD COLUMN `account_holder` VARCHAR(100) NOT NULL;

ALTER TABLE `f_investment_account_charge` ADD COLUMN `amount` DECIMAL(19,6) NOT NULL;

CREATE TABLE `f_investment_saving_account_transaction` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`investment_id` BIGINT(20) NOT NULL,
	`savings_id` BIGINT(20) NOT NULL,
	`transaction_id` BIGINT(20) NOT NULL,
	`description` VARCHAR(250) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
);