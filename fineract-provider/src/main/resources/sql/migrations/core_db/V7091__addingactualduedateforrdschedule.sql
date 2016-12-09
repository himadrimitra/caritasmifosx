ALTER TABLE `m_mandatory_savings_schedule`
	ADD COLUMN `actualduedate` DATE NULL AFTER `duedate`;

update m_mandatory_savings_schedule mss set mss.actualduedate = mss.duedate;