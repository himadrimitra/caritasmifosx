
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'EXECUTE_OVERDUECHARGE', 'OVERDUECHARGE', 'EXECUTE', 0), ('portfolio', 'EXECUTE_OVERDUECHARGE_CHECKER', 'OVERDUECHARGE', 'EXECUTE_CHECKER', 0),('portfolio', 'READ_OVERDUECHARGE', 'OVERDUECHARGE', 'READ', 0);

INSERT INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `initializing_errorlog`, `is_active`, `depands_on_job_name`) VALUES ('Apply Penalty For Broken Periods', 'Apply Penalty For Broken Periods', '0 0 0 1/1 * ? *', now(), NULL, 0, 'Apply penalty to overdue loans:Recalculate Interest For Loans');

DROP TABLE `m_loan_overdue_installment_charge`;

DELETE FROM `c_configuration` WHERE `name`  in ('penalty-wait-period','grace-on-penalty-posting','backdate-penalties-enabled');