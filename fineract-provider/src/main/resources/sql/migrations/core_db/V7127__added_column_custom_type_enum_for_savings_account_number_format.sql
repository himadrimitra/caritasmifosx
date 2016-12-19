ALTER TABLE `c_account_number_format`
	ADD COLUMN `custom_type_enum` SMALLINT(1) NULL DEFAULT NULL ;
	
	
	ALTER TABLE `m_savings_account`
	ALTER `account_no` DROP DEFAULT;
ALTER TABLE `m_savings_account`
	CHANGE COLUMN `account_no` `account_no` VARCHAR(30) NOT NULL AFTER `id`;
	

	ALTER TABLE `m_loan`
	ALTER `account_no` DROP DEFAULT;
ALTER TABLE `m_loan`
	CHANGE COLUMN `account_no` `account_no` VARCHAR(30) NOT NULL AFTER `id`;


	ALTER TABLE `m_product_loan`
	DROP INDEX `external_id_UNIQUE`;

	ALTER TABLE `m_savings_product`
	DROP INDEX `external_id`;
