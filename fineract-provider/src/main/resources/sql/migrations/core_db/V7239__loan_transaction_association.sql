ALTER TABLE `m_loan_transaction`
	ADD COLUMN `transaction_association_id` BIGINT(20) NULL DEFAULT NULL AFTER `orig_transaction_id`;
