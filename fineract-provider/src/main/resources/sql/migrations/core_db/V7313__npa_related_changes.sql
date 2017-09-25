ALTER TABLE `m_loan`
	ADD COLUMN `loan_recalcualated_on` DATE NULL DEFAULT NULL AFTER `interest_recalcualated_on`;