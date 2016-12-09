INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`,`can_maker_checker`) VALUES 
('transaction_loan', 'PREPAYMENT_LOAN', 'LOAN', 'PREPAYMENT', 0), 
('transaction_loan', 'PREPAYMENT_LOAN_CHECKER', 'LOAN', 'PREPAYMENT_CHECKER', 0);

ALTER TABLE `m_loan` ADD COLUMN `pmt_calculated_in_period_enum` SMALLINT(5) NULL DEFAULT NULL;
ALTER TABLE `m_product_loan` ADD COLUMN `pmt_calculated_in_period_enum` SMALLINT(5) NULL DEFAULT NULL ;








