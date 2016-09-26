-- add permissions for Client Fees

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'READ_CLIENTRECURRINGCHARGE', 'CLIENTRECURRINGCHARGE', 'READ', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_CLIENTRECURRINGCHARGE', 'CLIENTRECURRINGCHARGE', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'INACTIVATE_CLIENTRECURRINGCHARGE', 'CLIENTRECURRINGCHARGE', 'INACTIVATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_CLIENTRECURRINGCHARGE_CHECKER', 'CLIENTRECURRINGCHARGE', 'CREATE_CHECKER', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'INACTIVATE_CLIENTRECURRINGCHARGE_CHECKER', 'CLIENTRECURRINGCHARGE', 'INACTIVATE_CHECKER', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'UPDATE_CLIENTRECURRINGCHARGE_CHECKER', 'CLIENTCHARGE', 'UPDATE_CHECKER', 0);

-- new tables
CREATE TABLE `m_client_recurring_charge` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `client_id` BIGINT(20) NOT NULL,
    `charge_id` BIGINT(20) NOT NULL,
	`name` VARCHAR(100) NULL DEFAULT NULL,

	`charge_due_date` DATE,

	`currency_code` VARCHAR(3) NOT NULL,
	`charge_applies_to_enum` SMALLINT(5) NOT NULL,
	`charge_time_enum` SMALLINT(5) NOT NULL,
	`charge_calculation_enum` SMALLINT(5) NOT NULL,
	`charge_payment_mode_enum` SMALLINT(5) NULL DEFAULT NULL,
	`amount` DECIMAL(19,6) NOT NULL,
	`fee_on_day` SMALLINT(5) NULL DEFAULT NULL,
	`fee_interval` SMALLINT(5) NULL DEFAULT NULL,
	`fee_on_month` SMALLINT(5) NULL DEFAULT NULL,
	`is_penalty` TINYINT(1) NOT NULL DEFAULT '0',
	`is_active` TINYINT(1) NOT NULL,
	`is_deleted` TINYINT(1) NOT NULL DEFAULT '0',
	`is_synch_meeting` TINYINT(1) NOT NULL, 
	`min_cap` DECIMAL(19,6) NULL DEFAULT NULL,
	`max_cap` DECIMAL(19,6) NULL DEFAULT NULL,
	`fee_frequency` SMALLINT(5) NULL DEFAULT NULL,
	`inactivated_on_date` DATE NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=168
;

ALTER TABLE m_client_charge ADD COLUMN (client_recurring_charge_id BIGINT (20) );

INSERT INTO `job` 
	(`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`, `is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`)
VALUES 
	('Apply Recurring Charge On Client', 'Apply Recurring Charge On Client', '0 0 0 1/1 * ? *', '2015-04-08 09:24:15', 5, NULL, '2015-10-09 07:57:28', '2015-10-10 00:05:00', 'Apply Recurring Charge On ClientJobDetail1 _ DEFAULT', NULL, 1, 0, 1, 0, 0);


ALTER TABLE `m_client_charge`
ADD CONSTRAINT `FK_m_client_charge_m_client_recurring_charge` FOREIGN KEY (`client_recurring_charge_id`) REFERENCES `m_client_recurring_charge` (`id`);