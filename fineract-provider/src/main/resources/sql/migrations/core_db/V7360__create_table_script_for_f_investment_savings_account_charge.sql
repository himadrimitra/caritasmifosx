INSERT IGNORE INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `job_key`, `initializing_errorlog`) 
VALUES ('Mature Investment Accounts', 'Mature Investment Accounts', '0 10 0 1/1 * ? *', CURRENT_TIMESTAMP(), 'Mature Investment AccountsJobDetail1 _ DEFAULT', NULL);

CREATE TABLE `f_investment_savings_account_charge` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `investment_charge_id` BIGINT(20) NOT NULL,
    `savings_linkage_account` BIGINT(20) NOT NULL,
    `amount` DECIMAL(19,6) NOT NULL,
    `paid_amount` DECIMAL(19,6) NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx1_investment_account_charge` (`investment_charge_id`),
    INDEX `idx_f_investment_savings_linkage_account_charge` (`savings_linkage_account`),
    CONSTRAINT `idx1_investment_account_charge` FOREIGN KEY (`investment_charge_id`) REFERENCES `f_investment_account_charge` (`id`),
    CONSTRAINT `idx_f_investment_savings_linkage_account_charge` FOREIGN KEY (`savings_linkage_account`) REFERENCES `f_investment_account_savings_linkages` (`id`)
);

INSERT INTO `m_charge` (`name`, `currency_code`, `charge_applies_to_enum`, `charge_time_enum`, `charge_calculation_enum`, `charge_payment_mode_enum`, `amount`, `fee_on_day`, `fee_interval`, `fee_on_month`, `is_penalty`, `is_active`, `is_deleted`, `min_cap`, `max_cap`, `fee_frequency`, `income_or_liability_account_id`, `tax_group_id`, `emi_rounding_goalseek`, `is_glim_charge`, `glim_charge_calculation_enum`, `is_capitalized`, `charge_percentage_type`, `charge_percentage_period_type`) VALUES ('external_invstment', 'USD', 2, 53, 1, 0, 0.000000, NULL, NULL, NULL, 0, 1, 0, NULL, NULL, NULL, NULL, NULL, 0, 0, 1, 0, 1, 1);
