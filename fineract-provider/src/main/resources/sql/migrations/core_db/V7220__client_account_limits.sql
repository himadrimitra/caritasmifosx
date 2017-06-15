CREATE TABLE `m_client_account_limits` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`total_disbursement_amount_limit` DECIMAL(19,6) NULL DEFAULT NULL,
	`total_loan_outstanding_amount_limit` DECIMAL(19,6) NULL DEFAULT NULL,
	`daily_withdrawal_amount_limit` DECIMAL(19,6) NULL DEFAULT NULL,
	`daily_transfer_amount_limit` DECIMAL(19,6) NULL DEFAULT NULL,
	`total_overdraft_amount_limit` DECIMAL(19,6) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `client_id` (`client_id`),
	CONSTRAINT `FK__m_client_account_limits_m_client` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'CREATE_CUSTOMERACCOUNTLIMITS', 'CUSTOMERACCOUNTLIMITS', 'CREATE', '0'),
('portfolio', 'CREATE_CUSTOMERACCOUNTLIMITS_CHECKER', 'CUSTOMERACCOUNTLIMITS', 'CREATE_CHECKER', '0'),
('portfolio', 'UPDATE_CUSTOMERACCOUNTLIMITS', 'CUSTOMERACCOUNTLIMITS', 'UPDATE', '0'),
('portfolio', 'UPDATE_CUSTOMERACCOUNTLIMITS_CHECKER', 'CUSTOMERACCOUNTLIMITS', 'UPDATE_CHECKER', '0'),
('portfolio', 'DELETE_CUSTOMERACCOUNTLIMITS', 'CUSTOMERACCOUNTLIMITS', 'DELETE', '0'),
('portfolio', 'DELETE_CUSTOMERACCOUNTLIMITS_CHECKER', 'CUSTOMERACCOUNTLIMITS', 'DELETE_CHECKER', '0'),
('portfolio', 'READ_CUSTOMERACCOUNTLIMITS', 'CUSTOMERACCOUNTLIMITS', 'READ', '0');


INSERT INTO `f_business_event_listners` (`business_event_name`, `pre_listeners`, `post_listners`) 
VALUES ('loan_create', NULL, 'eventListnerForLimitLoanDisbursalAmount'),
('loan_modify', NULL, 'eventListnerForLimitLoanDisbursalAmount'),
('loan_approved', NULL, 'eventListnerForLimitLoanDisbursalAmount'),
('loan_disbursal', NULL, 'eventListnerForLimitLoanDisbursalAmount'),
('saving_withdrawal', NULL, 'eventListnerForSavingAccountLimitDailyWithdrawalAmount'),
('saving_transfer', NULL, 'eventListnerForSavingAccountLimitDailyTransferAmount');