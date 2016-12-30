ALTER TABLE `m_savings_product`
	ADD COLUMN `external_id` VARCHAR(50) NULL AFTER `days_to_escheat`,
	ADD UNIQUE INDEX `external_id` (`external_id`);

	
	ALTER TABLE `m_currency`
	ADD COLUMN `external_id` VARCHAR(50) NULL AFTER `internationalized_name_code`,
	ADD UNIQUE INDEX `external_id` (`external_id`);
