ALTER TABLE f_bank_account_details
add column document_id int(20),
add column checker_info text;

ALTER TABLE f_bank_account_details
add CONSTRAINT `FK_f_bank_account_document_id` FOREIGN KEY (`document_id`) REFERENCES `m_document` (`id`);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`,`can_maker_checker`) VALUES
('bank', 'ACTIVATE_BANKACCOUNTDETAIL', 'BANKACCOUNTDETAIL', 'ACTIVATE', 0),
('bank', 'CHECKERINFO_BANKACCOUNTDETAIL', 'BANKACCOUNTDETAIL', 'CHECKERINFO', 0),
('bank', 'ACTIVATE_BANKACCOUNTDETAIL_CHECKER', 'BANKACCOUNTDETAIL', 'ACTIVATE_CHECKER', 0),
('bank', 'CHECKERINFO_BANKACCOUNTDETAIL_CHECKER', 'BANKACCOUNTDETAIL', 'CHECKERINFO_CHECKER', 0);

INSERT IGNORE INTO `f_task_activity` (`name`, `identifier`, `config_values`, `supported_actions`, `type`) VALUES
('Bank Account Checker ', 'checkerbankaccount', NULL, NULL, 3),
('Bank Account Review', 'reviewbankaccount', NULL, NULL, 3);