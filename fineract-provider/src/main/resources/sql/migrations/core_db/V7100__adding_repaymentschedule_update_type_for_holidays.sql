ALTER TABLE `m_holiday`
	ADD COLUMN `resheduling_type` INT(5) NOT NULL DEFAULT '0' AFTER `extend_repayment_schedule`;
	
	update m_holiday mh set mh.resheduling_type = 2 where mh.repayments_rescheduled_to is not null;
		 
	update m_holiday mh set mh.resheduling_type = 1 where mh.repayments_rescheduled_to is null;
	
	ALTER TABLE `m_holiday`
	CHANGE COLUMN `resheduling_type` `resheduling_type` INT(5) NOT NULL AFTER `extend_repayment_schedule`;