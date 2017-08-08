ALTER TABLE `m_loan_recalculation_details`
	ADD COLUMN `rest_frequency_start_date` DATE NULL DEFAULT NULL,
	ADD COLUMN `compounding_frequency_start_date` DATE NULL DEFAULT NULL;
