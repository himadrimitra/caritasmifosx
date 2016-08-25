ALTER TABLE `m_product_loan`
	ADD COLUMN `min_loan_term` INT NULL DEFAULT NULL ,
	ADD COLUMN `max_loan_term` INT NULL DEFAULT NULL ,
	ADD COLUMN `loan_tenure_frequency_type` INT NULL DEFAULT NULL;
	