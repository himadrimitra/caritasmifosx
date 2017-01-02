ALTER TABLE `f_existing_loan` 
ADD COLUMN `tranche_disbursal_id` BIGINT(20) NULL DEFAULT NULL AFTER `loan_id`, 
CHANGE COLUMN `repayment_frequency_multiple_of` `repayment_frequency_multiple_of` SMALLINT(5) NULL DEFAULT NULL AFTER `loan_tenure_period_type`;

UPDATE f_existing_loan el SET el.loan_tenure_period_type = 4
WHERE el.loan_creditbureau_enquiry_id IS NOT NULL AND el.loan_tenure_period_type = 0;

UPDATE f_existing_loan el SET el.loan_tenure_period_type = 0
WHERE el.loan_creditbureau_enquiry_id IS NOT NULL AND el.loan_tenure_period_type = 1;

UPDATE f_existing_loan el SET el.loan_tenure_period_type = 1
WHERE el.loan_creditbureau_enquiry_id IS NOT NULL AND el.loan_tenure_period_type = 2;

UPDATE f_existing_loan el SET el.loan_tenure_period_type = 2
WHERE el.loan_creditbureau_enquiry_id IS NOT NULL AND el.loan_tenure_period_type = 3;

UPDATE f_existing_loan el SET el.loan_tenure_period_type = 3
WHERE el.loan_creditbureau_enquiry_id IS NOT NULL AND el.loan_tenure_period_type = 4;