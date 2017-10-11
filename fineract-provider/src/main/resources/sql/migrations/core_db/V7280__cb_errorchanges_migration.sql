ALTER TABLE `f_creditbureau_enquiry`
	ADD COLUMN `errors_json` VARCHAR(500) NULL DEFAULT NULL AFTER `response`;