ALTER TABLE `m_loan_installment_charge`
	ADD COLUMN `amount_sans_tax` DECIMAL(19,6) NULL DEFAULT NULL AFTER `amount_through_charge_payment`,
	ADD COLUMN `tax_amount` DECIMAL(19,6) NULL DEFAULT NULL AFTER `amount_sans_tax`;