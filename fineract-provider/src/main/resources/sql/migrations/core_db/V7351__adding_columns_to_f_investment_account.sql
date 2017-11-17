ALTER TABLE f_investment_account_savings_linkages 
ADD COLUMN `status` SMALLINT(5) NOT NULL AFTER `investment_amount`,
ADD COLUMN `active_from_date` DATE NULL DEFAULT NULL AFTER `status`,
ADD COLUMN `active_to_date` DATE NULL DEFAULT NULL AFTER `active_from_date`;

INSERT IGNORE into m_code (`code_name`,`is_system_defined`) VALUES ('Investmentpartners',1);

ALTER TABLE f_investment_account
ADD COLUMN `staff_id`  BIGINT(20) NULL DEFAULT NULL AFTER `office_id`,
ADD COLUMN `track_source_accounts` TINYINT(1) NOT NULL DEFAULT 0 AFTER `reinvest_after_maturity`, 
ADD COLUMN `rejecton_date` DATE NULL DEFAULT NULL AFTER `reinvest_after_maturity`,
ADD COLUMN `rejecton_userid` BIGINT(20) NULL DEFAULT NULL AFTER `rejecton_date`,
ADD COLUMN `closeon_date` DATE NULL DEFAULT NULL AFTER `rejecton_userid`,
ADD COLUMN `closeon_userid` BIGINT(20) NULL DEFAULT NULL AFTER `closeon_date`;

