ALTER TABLE `m_loan_glim` 
ADD COLUMN `paid_principal_amount` DECIMAL(19,6) NULL DEFAULT NULL,
ADD COLUMN `paid_charge_amount` DECIMAL(19,6) NULL DEFAULT NULL,
ADD COLUMN `waived_interest_amount` DECIMAL(19,6) NULL DEFAULT NULL,
ADD COLUMN `waived_charge_amount` DECIMAL(19,6) NULL DEFAULT NULL;

ALTER TABLE `m_charge`
	ADD COLUMN `is_glim_charge` TINYINT(1) NOT NULL DEFAULT '0' AFTER `emi_rounding_goalseek`;