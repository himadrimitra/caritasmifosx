DROP TABLE IF EXISTS f_loan_product_eligibility_criteria;
DROP TABLE IF EXISTS f_loan_product_eligibility;

DROP TABLE IF EXISTS f_risk_field;

CREATE TABLE `f_risk_field` (
  `id`         BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `name`       VARCHAR(100) NOT NULL,
  `uname`      VARCHAR(100) NOT NULL,
  `value_type` TINYINT(4) NOT NULL DEFAULT '1',
  `options`    VARCHAR(1024)         DEFAULT NULL,
  `code_name`  VARCHAR(100)         DEFAULT NULL,
  `is_active`  TINYINT(4)   NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_f_risk_field_uname` (`uname`)
)
  COLLATE = 'utf8_general_ci' ENGINE = InnoDB;

INSERT INTO f_risk_field (name, uname, value_type, options,code_name, is_active) VALUES
  ("Age", "age", 0, NULL, NULL,1),
  ("Gender", "gender", 1, NULL,"gender", 1);


DROP TABLE IF EXISTS f_risk_rule;

CREATE TABLE `f_risk_rule` (
  `id`         BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `entity_type` TINYINT(4) NOT NULL,
  `name`       VARCHAR(100) NOT NULL,
  `uname`      VARCHAR(100) NOT NULL,
  `description`      VARCHAR(512) NOT NULL,
  `default_value`      VARCHAR(100) DEFAULT NULL,
  `value_type` TINYINT(4) NOT NULL DEFAULT '1',
  `possible_outputs`    VARCHAR(512)     NOT NULL,
  `expression`    text         DEFAULT NULL,
  `is_active`  TINYINT(4)   NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_f_risk_rule_uname` (`uname`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;

CREATE TABLE `f_loan_product_eligibility` (
  `id`         BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `loan_product_id`         BIGINT(20)   NOT NULL,
  `is_active`  TINYINT(4)   NOT NULL DEFAULT 1,
  `created_on_date`  datetime NOT NULL,
  `created_by` bigint(20) NOT NULL,
  `updated_on_date` datetime NOT NULL,
  `updated_by` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_f_loan_product_eligibility_mapping` (`loan_product_id`),
  CONSTRAINT `fk_f_loan_product_eligibility_mapping_loan_product_id` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`),
  CONSTRAINT `fk_f_loan_product_eligibility_created_by` FOREIGN KEY (`created_by`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `fk_f_loan_product_eligibility_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `m_appuser` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;

CREATE TABLE `f_loan_product_eligibility_criteria` (
  `id`         BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `loan_product_eligibility_id`         BIGINT(20),
  `min_amount`         DOUBLE   NOT NULL,
  `max_amount`         DOUBLE   NOT NULL,
  `risk_criteria_id`         BIGINT(20)   NOT NULL,
  `approval_logic`         VARCHAR(256)   NOT NULL,
  `rejection_logic`         VARCHAR(256)   NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_f_loan_product_eligibility_criteria_eligibility_id` FOREIGN KEY (`loan_product_eligibility_id`) REFERENCES `f_loan_product_eligibility` (`id`),
  CONSTRAINT `fk_f_loan_product_eligibility_criteria_risk_criteria_iditeria_id` FOREIGN KEY (risk_criteria_id) REFERENCES `f_risk_rule` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
  ('portfolio', 'CREATE_RISKFACTOR', 'RISKFACTOR', 'CREATE', 0),
  ('portfolio', 'UPDATE_RISKFACTOR', 'RISKFACTOR', 'UPDATE', 0),
  ('portfolio', 'CREATE_RISKDIMENSION', 'RISKDIMENSION', 'CREATE', 0),
  ('portfolio', 'UPDATE_RISKDIMENSION', 'RISKDIMENSION', 'UPDATE', 0),
  ('portfolio', 'CREATE_RISKCRITERIA', 'RISKCRITERIA', 'CREATE', 0),
  ('portfolio', 'UPDATE_RISKCRITERIA', 'RISKCRITERIA', 'UPDATE', 0);

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
  ('portfolio', 'CREATE_LOANPRODUCTELIGIBILITY', 'LOANPRODUCTELIGIBILITY', 'CREATE', 0),
  ('portfolio', 'UPDATE_LOANPRODUCTELIGIBILITY', 'LOANPRODUCTELIGIBILITY', 'UPDATE', 0);


DROP TABLE IF EXISTS `f_loan_application_eligibility_check`;

CREATE TABLE `f_loan_application_eligibility_check` (
  `id`         BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `loan_application_id`         BIGINT(20)   NOT NULL,
  `eligibility_status`  TINYINT(4)   NOT NULL,
  `eligibility_result`  TEXT   NOT NULL,
  `created_on_date`  datetime NOT NULL,
  `created_by` bigint(20) NOT NULL,
  `updated_on_date` datetime NOT NULL,
  `updated_by` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_f_loan_application_eligibility_check` (`loan_application_id`),
  CONSTRAINT `fk_f_loan_application_eligibility_check_loan_application_id` FOREIGN KEY (`loan_application_id`) REFERENCES `f_loan_application_reference` (`id`),
  CONSTRAINT `fk_f_loan_application_eligibility_check_created_by` FOREIGN KEY (`created_by`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `fk_f_loan_application_eligibility_check_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `m_appuser` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;

 -- alter table f_risk_rule CHANGE entity entity_type TINYINT(4) NOT NULL;