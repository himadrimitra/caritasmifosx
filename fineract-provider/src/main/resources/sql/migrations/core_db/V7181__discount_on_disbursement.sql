ALTER TABLE `m_loan`
	ADD COLUMN `discount_on_disbursal_amount` DECIMAL(19,6) NULL DEFAULT NULL AFTER `calculated_installment_amount`;
