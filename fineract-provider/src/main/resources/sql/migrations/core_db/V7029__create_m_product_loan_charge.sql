ALTER TABLE `m_product_loan_charge`
	ADD COLUMN `id` BIGINT(20) NOT NULL AUTO_INCREMENT FIRST,
	ADD COLUMN `is_mandatory` TINYINT NOT NULL DEFAULT '0' AFTER `charge_id`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`id`),
	ADD INDEX `product_loan_id` (`product_loan_id`);
	