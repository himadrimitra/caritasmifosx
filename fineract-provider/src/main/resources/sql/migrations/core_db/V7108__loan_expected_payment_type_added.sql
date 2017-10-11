ALTER TABLE `m_loan` ADD COLUMN `expected_disbursal_payment_type_id` INT(11) NULL DEFAULT NULL,
ADD CONSTRAINT fk2_m_loan_m_payment_type FOREIGN KEY (`expected_disbursal_payment_type_id`) REFERENCES `m_payment_type`(`id`);

ALTER TABLE `m_loan` ADD COLUMN `expected_repayment_payment_type_id` INT(11) NULL DEFAULT NULL,
ADD CONSTRAINT fk3_m_loan_m_payment_type FOREIGN KEY (`expected_repayment_payment_type_id`) REFERENCES `m_payment_type`(`id`);
