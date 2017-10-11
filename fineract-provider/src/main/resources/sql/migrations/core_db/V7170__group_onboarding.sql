INSERT INTO `f_task_activity` (`name`, `identifier`, `config_values`, `supported_actions`, `type`)
VALUES
	('Credit Bureau', 'creditbureau', NULL, NULL, 3),
	('Datatable Task', 'datatable', NULL, NULL, 2),
	('Existing Loans', 'existingloans', NULL, NULL, 3),
	('Upload Document', 'clientdocument', NULL, NULL, 3),
	('Survey Task', 'survey', NULL, NULL, 1),
	('Loan Application Approval', 'loanapplicationapproval', NULL, NULL, 3),
	('Loan Disbursal', 'loanapplicationdisbursal', NULL, NULL, 3),
	('Bank Account', 'bankaccount', NULL, NULL, 3),
	('KYC', 'kyc', NULL, NULL, 3);


INSERT INTO `f_task_activity` (`name`, `identifier`, `config_values`, `supported_actions`, `type`)
VALUES
('Group Members', 'groupmembers', NULL, NULL, 3);

INSERT INTO `f_task_activity` (`name`, `identifier`, `config_values`, `supported_actions`, `type`)
VALUES
('Loan Application CoApplicants', 'loanappcoapplicant', NULL, NULL, 3);