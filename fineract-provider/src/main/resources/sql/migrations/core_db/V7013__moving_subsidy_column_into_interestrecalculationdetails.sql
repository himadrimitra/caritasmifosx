ALTER TABLE `m_product_loan_recalculation_details`
	ADD COLUMN `is_subsidy_applicable` TINYINT(1) NOT NULL DEFAULT '0';
	
ALTER TABLE `m_loan_recalculation_details`
	ADD COLUMN `is_subsidy_applicable` TINYINT(1) NOT NULL DEFAULT '0';
	
UPDATE m_product_loan_recalculation_details AS lrd
INNER JOIN m_product_loan AS pl ON lrd.product_id = pl.id SET lrd.is_subsidy_applicable = pl.is_subsidy_applicable
WHERE pl.is_subsidy_applicable IS NOT NULL;

UPDATE m_loan_recalculation_details AS lrd
INNER JOIN m_loan AS l ON lrd.loan_id = l.id SET lrd.is_subsidy_applicable = l.is_subsidy_applicable
WHERE l.is_subsidy_applicable IS NOT NULL;

ALTER TABLE `m_product_loan`
	DROP COLUMN `is_subsidy_applicable`;
		
ALTER TABLE `m_loan`
	DROP COLUMN `is_subsidy_applicable`;