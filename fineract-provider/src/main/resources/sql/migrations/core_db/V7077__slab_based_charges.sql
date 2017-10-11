CREATE TABLE `f_charge_slab` (
	`id` BIGINT(20) NOT NULL,
	`charge_id` BIGINT(20) NOT NULL,
	`from_loan_amount` DECIMAL(19,6) NOT NULL,
	`to_loan_amount` DECIMAL(19,6) NOT NULL,
	`amount` DECIMAL(19,6) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK__m_charge` (`charge_id`),
	CONSTRAINT `FK__m_charge` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT=1
;

ALTER TABLE `m_charge`
	ADD COLUMN `is_capitalized` TINYINT(1) NOT NULL DEFAULT '0' AFTER `glim_charge_calculation_enum`;
	
ALTER TABLE `m_loan_charge`
	ADD COLUMN `is_capitalized` TINYINT(1) NOT NULL DEFAULT '0' AFTER `tax_amount`;
	
ALTER TABLE `m_loan`
	ADD COLUMN `total_charges_capitalized_at_disbursement_derived` DECIMAL(19,6) NULL DEFAULT '0' AFTER `create_standing_instruction_at_disbursement`;
	
ALTER TABLE `m_charge`	ALTER `amount` DROP DEFAULT;

ALTER TABLE `m_charge`
	CHANGE COLUMN `amount` `amount` DECIMAL(19,6) NULL AFTER `charge_payment_mode_enum`;