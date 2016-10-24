ALTER TABLE `m_loan_glim`
	CHANGE COLUMN `installment_amount` `installment_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `is_client_selected`,
	CHANGE COLUMN `interest_amount` `interest_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `installment_amount`,
	CHANGE COLUMN `adjusted_amount` `adjusted_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `interest_amount`,
	CHANGE COLUMN `total_payble_amount` `total_payble_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `adjusted_amount`,
	CHANGE COLUMN `paid_amount` `paid_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `total_payble_amount`,
	CHANGE COLUMN `charge_amount` `charge_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `paid_amount`,
	CHANGE COLUMN `paid_interest_amount` `paid_interest_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `charge_amount`,
	CHANGE COLUMN `paid_principal_amount` `paid_principal_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `paid_interest_amount`,
	CHANGE COLUMN `paid_charge_amount` `paid_charge_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `paid_principal_amount`,
	CHANGE COLUMN `waived_interest_amount` `waived_interest_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `paid_charge_amount`,
	CHANGE COLUMN `waived_charge_amount` `waived_charge_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `waived_interest_amount`,
	ADD COLUMN `principal_writtenoff_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `waived_charge_amount`,
	ADD COLUMN `interest_writtenoff_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `principal_writtenoff_amount`,
	ADD COLUMN `fee_charges_writtenoff_amount` DECIMAL(19,6) NULL DEFAULT '0' AFTER `interest_writtenoff_amount`,
	ADD COLUMN `is_active` TINYINT(1) NOT NULL DEFAULT '1';

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('transaction_loan', 'WRITEOFF_GLIMTRANSACTION', 'GLIMTRANSACTION', 'WRITEOFF', 0);
