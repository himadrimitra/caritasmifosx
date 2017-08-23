ALTER TABLE `f_creditbureau_enquiry`
	ADD COLUMN `request_location` VARCHAR(500) NULL DEFAULT NULL AFTER `response`,
	ADD COLUMN `response_location` VARCHAR(500) NULL DEFAULT NULL AFTER `request_location`;
ALTER TABLE `f_loan_creditbureau_enquiry`
	ADD COLUMN `request_location` VARCHAR(500) NULL DEFAULT NULL AFTER `response`,
	ADD COLUMN `response_location` VARCHAR(500) NULL DEFAULT NULL AFTER `request_location`,
	ADD COLUMN `report_location` VARCHAR(500) NULL DEFAULT NULL AFTER `response_location`;