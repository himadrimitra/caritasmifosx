ALTER TABLE `m_loan_glim_charges` 
ADD COLUMN `waived_charge_amount` DECIMAL(19,6) NULL DEFAULT NULL;

ALTER TABLE `m_loan_glim_transaction`
 ADD COLUMN `transaction_type_enum` SMALLINT(5) NOT NULL AFTER `loan_transaction_id`;

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('transaction_loan', 'WAIVEINTEREST_GLIMTRANSACTION', 'GLIMTRANSACTION', 'WAIVEINTEREST', 0),
('transaction_loan', 'WAIVECHARGE_GLIMTRANSACTION', 'GLIMTRANSACTION', 'WAIVECHARGE', 0);