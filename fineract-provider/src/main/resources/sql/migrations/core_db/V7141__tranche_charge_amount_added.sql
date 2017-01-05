ALTER TABLE `m_loan_tranche_charges`
	ADD COLUMN `amount` DECIMAL(19,6) NOT NULL AFTER `charge_id`;
	
update m_loan_tranche_charges lc  join m_charge c on c.id = lc.charge_id set lc.amount = c.amount