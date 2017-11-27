ALTER TABLE `f_investment_account_savings_linkages` ADD COLUMN 
`maturity_amount` DECIMAL(19,6) NULL DEFAULT NULL,
`expected_maturity_amount` DECIMAL(19,6) NULL DEFAULT NULL,
`expected_interest_amount` DECIMAL(19,6) NULL DEFAULT NULL,
`interest_amount` DECIMAL(19,6) NULL DEFAULT NULL,
`expected_charge_amount` DECIMAL(19,6) NULL DEFAULT NULL,
`charge_amount` DECIMAL(19,6) NULL DEFAULT NULL;


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'RELEASE_INVESTMENT_ACCOUNT', 'INVESTMENT_ACCOUNT', 'RELEASE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'TRANSFER_INVESTMENT_ACCOUNT', 'INVESTMENT_ACCOUNT', 'TRANSFER', 0);
