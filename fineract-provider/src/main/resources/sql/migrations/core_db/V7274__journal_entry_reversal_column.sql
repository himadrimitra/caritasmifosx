ALTER TABLE `f_journal_entry`
	ADD COLUMN `is_reversal_entry` TINYINT(1) NOT NULL DEFAULT '0' AFTER `reversal_id`;