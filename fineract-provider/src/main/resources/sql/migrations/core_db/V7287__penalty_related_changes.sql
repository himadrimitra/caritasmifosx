ALTER TABLE `m_charge`
	ADD COLUMN `charge_percentage_type` SMALLINT(1) NOT NULL DEFAULT '1' AFTER `is_capitalized`,
	ADD COLUMN `charge_percentage_period_type` SMALLINT(1) NOT NULL DEFAULT '1' AFTER `charge_percentage_type`;

CREATE TABLE `f_charge_overdue_detail` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`charge_id` BIGINT(20) NOT NULL,
	`grace_period` SMALLINT(4) NOT NULL DEFAULT '0',
	`penalty_free_period` SMALLINT(4) NOT NULL DEFAULT '0',
	`grace_type_enum` SMALLINT(1) NOT NULL DEFAULT '2',
	`is_based_on_original_schedule` TINYINT(1) NOT NULL DEFAULT '0',
	`consider_only_posted_interest` TINYINT(1) NOT NULL DEFAULT '0',
	`calculate_charge_on_current_overdue` TINYINT(1) NOT NULL DEFAULT '0',
	`apply_charge_for_broken_period` TINYINT(1) NOT NULL DEFAULT '0',
	`min_overdue_amount_required` DECIMAL(19,6) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_overdue_detail_charge_id` (`charge_id`),
	CONSTRAINT `FK_overdue_detail_charge_id` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`)
);

CREATE TABLE `f_loan_recurring_charge` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_id` BIGINT(20) NOT NULL,
	`charge_id` BIGINT(20) NOT NULL,
	`charge_time_enum` SMALLINT(3) NOT NULL,
	`charge_calculation_enum` SMALLINT(3) NOT NULL,
	`charge_payment_mode_enum` TINYINT(1) NOT NULL,
	`amount` DECIMAL(19,6) NOT NULL,
	`fee_interval` SMALLINT(2) NULL DEFAULT NULL,
	`is_penalty` TINYINT(1) NOT NULL DEFAULT '0',
	`fee_frequency` SMALLINT(1) NULL DEFAULT NULL,
	`tax_group_id` BIGINT(20) NULL DEFAULT NULL,
	`charge_percentage_type` SMALLINT(1) NOT NULL DEFAULT '1',
	`charge_percentage_period_type` SMALLINT(1) NOT NULL DEFAULT '1',
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_loan_recurr_detail_loan` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
	CONSTRAINT `FK_loan_recurr_detail_charge` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`)
);

CREATE TABLE `f_loan_overdue_charge_detail` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`recurrence_charge_id` BIGINT(20) NOT NULL,
	`grace_period` SMALLINT(4) NOT NULL,
	`penalty_free_period` SMALLINT(4) NOT NULL,
	`grace_type_enum` SMALLINT(1) NOT NULL,
	`apply_charge_for_broken_period` TINYINT(1) NOT NULL,
	`is_based_on_original_schedule` TINYINT(1) NOT NULL,
	`consider_only_posted_interest` TINYINT(1) NOT NULL,
	`calculate_charge_on_current_overdue` TINYINT(1) NOT NULL,
	`min_overdue_amount_required` DECIMAL(19,6) NULL DEFAULT NULL,
	`last_applied_on_date` DATE NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_loan_overdue_detail_recurrence_charge` FOREIGN KEY (`recurrence_charge_id`) REFERENCES `f_loan_recurring_charge` (`id`)
);

--INSERT INTO `f_charge_overdue_detail` (`charge_id`,`grace_period`,`penalty_free_period`,`calculate_charge_on_current_overdue`) select c.id,cf.value,cf.value,1  from m_charge c ,  c_configuration cf  where c.charge_time_enum = 9 and cf.name = 'penalty-wait-period';

--INSERT INTO `f_loan_recurring_charge` (`loan_id`, `charge_id`, `charge_time_enum`, `charge_calculation_enum`, `charge_payment_mode_enum`, `amount`, `fee_interval`, `is_penalty`, `fee_frequency`, `charge_percentage_type`, `charge_percentage_period_type`,`tax_group_id`) select ml.id,mc.id, mc.charge_time_enum,mc.charge_calculation_enum,mc.charge_payment_mode_enum,mc.amount,mc.fee_interval,mc.is_penalty,mc.fee_frequency,mc.charge_percentage_type,mc.charge_percentage_period_type,mc.tax_group_id from m_loan ml join m_product_loan mpl on mpl.id = ml.product_id join m_product_loan_charge pc on pc.product_loan_id = mpl.id join m_charge mc on mc.id = pc.charge_id and mc.is_deleted=0 and mc.is_active=1 and mc.charge_time_enum = 9;

--INSERT INTO `f_loan_overdue_charge_detail` (`recurrence_charge_id`, `grace_period`, `penalty_free_period`, `grace_type_enum`, `apply_charge_for_broken_period`, `is_based_on_original_schedule`, `consider_only_posted_interest`, `calculate_charge_on_current_overdue`) select rc.id, cod.grace_period, cod.penalty_free_period, cod.grace_type_enum, cod.apply_charge_for_broken_period, cod.is_based_on_original_schedule, cod.consider_only_posted_interest, cod.calculate_charge_on_current_overdue from f_loan_recurring_charge rc  join f_charge_overdue_detail cod on cod.charge_id = rc.charge_id;

--UPDATE f_loan_overdue_charge_detail lod join (select rc.id as id,max(lc.due_for_collection_as_of_date) as lastAppliedOnDate from f_loan_recurring_charge rc join m_loan_charge lc on lc.charge_id = rc.charge_id group by rc.loan_id,rc.charge_id) as recurrCharge on  recurrCharge.id = lod.recurrence_charge_id SET lod.last_applied_on_date =  recurrCharge.lastAppliedOnDate


