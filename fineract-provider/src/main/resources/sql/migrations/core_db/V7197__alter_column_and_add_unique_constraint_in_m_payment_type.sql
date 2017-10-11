ALTER TABLE `m_payment_type` ALTER `value` DROP DEFAULT;
ALTER TABLE `m_payment_type` CHANGE COLUMN `value` `value` VARCHAR(100) NOT NULL AFTER `id`;
ALTER TABLE `m_payment_type` CHANGE COLUMN `order_position` `order_position` INT(11) NULL DEFAULT '0' AFTER `is_cash_payment`;
ALTER TABLE `m_payment_type` ADD UNIQUE INDEX `payment_name_unique` (`value`);