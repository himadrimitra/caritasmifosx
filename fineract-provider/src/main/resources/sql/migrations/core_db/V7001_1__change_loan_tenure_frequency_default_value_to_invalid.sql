ALTER TABLE `m_product_loan`
	CHANGE COLUMN `loan_tenure_frequency_type` `loan_tenure_frequency_type` INT(11) NULL DEFAULT '4' AFTER `max_loan_term`;

UPDATE 	`m_product_loan` SET `loan_tenure_frequency_type`= 4;