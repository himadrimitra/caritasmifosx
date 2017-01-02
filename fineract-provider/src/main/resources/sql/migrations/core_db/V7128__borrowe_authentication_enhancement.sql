ALTER TABLE `f_transaction_authentication`
	ADD COLUMN `product_id` BIGINT(20) NULL DEFAULT NULL AFTER `lastmodifiedby_id`;
	
	ALTER TABLE `f_transaction_authentication`
	DROP INDEX `portfolio_transaction_type_enum_payment_type_id_amount`,
	ADD UNIQUE INDEX `portfolio_transaction_type_enum_payment_type_id_amount` (`portfolio_type`, `transaction_type_enum`, `payment_type_id`, `amount`, `product_id`);