ALTER TABLE `m_loan_glim` 
ADD COLUMN `percentage`  DECIMAL(19,6) NULL DEFAULT NULL AFTER `disbursed_amount`;