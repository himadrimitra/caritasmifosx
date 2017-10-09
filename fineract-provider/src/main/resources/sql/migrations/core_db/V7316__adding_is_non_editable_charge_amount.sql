ALTER TABLE `m_product_loan_charge`
	ADD COLUMN `is_amount_non_editable` TINYINT(1) NOT NULL DEFAULT '0' AFTER `is_mandatory`;