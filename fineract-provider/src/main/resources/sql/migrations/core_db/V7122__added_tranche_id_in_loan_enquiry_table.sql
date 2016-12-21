ALTER TABLE `f_loan_creditbureau_enquiry`
	ADD COLUMN `tranche_disbursal_id` BIGINT(20) NULL DEFAULT NULL AFTER `loan_application_id`,
	ADD CONSTRAINT `FK_f_loan_creditbureau_enquiry_tranche_disbursal_id` FOREIGN KEY (`tranche_disbursal_id`) REFERENCES `m_loan_disbursement_detail` (`id`);

ALTER TABLE `f_loan_creditbureau_enquiry`
	ADD COLUMN `request` MEDIUMTEXT NULL DEFAULT NULL AFTER `status`;
	
INSERT IGNORE INTO `c_configuration` (`name`, `value`, `date_value`, `enabled`, `is_trap_door`, `description`) VALUES ('tranche-disbursal-high-mark', '0', NULL, 0, 0, 'Enable Tranche disbursal high mark features');
