CREATE TABLE  `f_financial_activity_account_payment_type_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `payment_type_id` int(11) NOT NULL DEFAULT '0',
  `financial_activity_account_id` bigint(20) NOT NULL DEFAULT '0',
  `gl_account_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `FK__m_payment_type` (`payment_type_id`),
  KEY `FK__acc_gl_financial_activity_account` (`financial_activity_account_id`),
  CONSTRAINT `FK__acc_gl_account` FOREIGN KEY (`gl_account_id`) REFERENCES `acc_gl_account` (`id`),
  CONSTRAINT `FK__acc_gl_financial_activity_account` FOREIGN KEY (`financial_activity_account_id`) REFERENCES `acc_gl_financial_activity_account` (`id`),
  CONSTRAINT `FK__m_payment_type` FOREIGN KEY (`payment_type_id`) REFERENCES `m_payment_type` (`id`)
);

ALTER TABLE `m_client_transaction`
	DROP FOREIGN KEY `FK_m_client_transaction_m_appuser`;
	
	
ALTER TABLE `m_client_transaction`
	CHANGE COLUMN `created_date` `created_date` DATETIME NULL AFTER `amount`,
	ADD COLUMN `lastmodified_date` DATETIME NULL AFTER `created_date`,
	CHANGE COLUMN `appuser_id` `createdby_id` BIGINT(20) NULL AFTER `lastmodified_date`,
	ADD COLUMN `lastmodifiedby_id` BIGINT(20) NULL AFTER `createdby_id`,
	ADD CONSTRAINT `FK_1_m_client_transaction_m_appuser` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
 	ADD CONSTRAINT `FK_2_m_client_transaction_m_appuser` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`);
	