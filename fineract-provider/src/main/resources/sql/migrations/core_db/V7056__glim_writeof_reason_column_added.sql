ALTER TABLE m_loan_glim 
ADD COLUMN `writeoff_reason_cv_id` INT(11) NULL DEFAULT NULL;

ALTER TABLE acc_product_mapping 
ADD COLUMN `code_value_cv_id` INT(11) NULL DEFAULT NULL;

