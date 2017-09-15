ALTER TABLE `m_client_recurring_charge`
	ADD COLUMN `client_charge_applies_on_date` DATE NULL DEFAULT NULL AFTER `lastmodified_date`;