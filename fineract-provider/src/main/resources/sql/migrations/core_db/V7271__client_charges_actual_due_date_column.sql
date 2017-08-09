ALTER TABLE `m_client_charge`
	ADD COLUMN `charge_actual_due_date` DATE NULL DEFAULT NULL AFTER `charge_time_enum`;
	
UPDATE `m_client_charge` cc SET cc.charge_actual_due_date = cc.charge_due_date;