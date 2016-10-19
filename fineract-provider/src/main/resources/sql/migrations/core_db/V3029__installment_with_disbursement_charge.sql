DROP TABLE IF EXISTS `m_loan_glim_charges`;

CREATE TABLE `m_loan_glim_charges` (
`id` INT(11) NOT NULL AUTO_INCREMENT,
`client_id` BIGINT(20) NULL DEFAULT NULL,
`charge_id` BIGINT(20) NULL DEFAULT NULL,
`glim_id` INT(20) NULL DEFAULT NULL,
`fee_amount` DECIMAL(19,6) NULL DEFAULT NULL,
`revised_fee_amount` DECIMAL(19,6) NULL DEFAULT NULL,
PRIMARY KEY (`id`),
INDEX `FK_m_loan_glim_charges_m_client` (`client_id`),
INDEX `FK_m_loan_glim_charges_m_charge` (`charge_id`),
INDEX `FK_m_loan_glim_charges_m_loan_glim` (`glim_id`),
CONSTRAINT `FK_m_loan_glim_charges_m_client` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
CONSTRAINT `FK_m_loan_glim_charges_m_charge` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
CONSTRAINT `FK_m_loan_glim_charges_m_loan_glim` FOREIGN KEY (`glim_id`) REFERENCES `m_loan_glim` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

ALTER TABLE `m_loan_glim`
ADD COLUMN  `installment_amount` DECIMAL(19,6) NULL DEFAULT NULL,
ADD COLUMN  `interest_amount` DECIMAL(19,6) NULL DEFAULT NULL,
ADD COLUMN  `adjusted_amount` DECIMAL(19,6) NULL DEFAULT NULL,
ADD COLUMN  `total_payble_amount` DECIMAL(19,6) NULL DEFAULT NULL,
ADD COLUMN  `paid_amount` DECIMAL(19,6) NULL DEFAULT NULL,
ADD COLUMN  `charge_amount` DECIMAL(19,6) NULL DEFAULT NULL,
ADD COLUMN  `paid_interest_amount` DECIMAL(19,6) NULL DEFAULT NULL;
