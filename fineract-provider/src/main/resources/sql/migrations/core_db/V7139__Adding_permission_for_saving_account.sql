INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('portfolio', 'ASSIGNFIELDOFFICER_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'ASSIGNSTAFF', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'ASSIGNFIELDOFFICER_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'ASSIGNSTAFF_CHECKER', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('transaction_savings', 'POSTINTERESTASON_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'POSTINTERESTASON', 1);


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('transaction_savings', 'POSTINTERESTASON_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'POSTINTERESTASON_CHECKER', 1);