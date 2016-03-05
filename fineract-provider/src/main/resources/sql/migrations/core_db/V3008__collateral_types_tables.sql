-- collateral subsystem
DROP TABLE IF EXISTS `m_collateral_details`;
DROP TABLE IF EXISTS `m_pledge`;
DROP TABLE IF EXISTS `m_collateral_quality_standards`;
DROP TABLE IF EXISTS `m_product_to_collateral_mappings`;
DROP TABLE IF EXISTS `m_collateral_type`;

CREATE TABLE `m_collateral_type` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NOT NULL,
	`description` VARCHAR(250) NULL DEFAULT NULL,
	`base_unit_price` DECIMAL(19,6) NOT NULL,
	`type_classifier` TINYINT(1) NOT NULL DEFAULT '1',
	PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

	
CREATE TABLE `m_product_to_collateral_mappings` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`collateral_id` BIGINT(20) NULL DEFAULT NULL,
	`product_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `unique_product_collateral_mapping` (`collateral_id`, `product_id`),
	INDEX `FK1_m_product_to_collateral_mappings_m_collateral_type` (`collateral_id`),
	INDEX `FK1_m_product_to_collateral_mappings_m_product_loan` (`product_id`),
	CONSTRAINT `FK1_m_product_to_collateral_mappings_m_collateral_type` FOREIGN KEY (`collateral_id`) REFERENCES `m_collateral_type` (`id`),
	CONSTRAINT `FK1_m_product_to_collateral_mappings_m_product_loan` FOREIGN KEY (`product_id`) REFERENCES `m_product_loan` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

	
CREATE TABLE `m_collateral_quality_standards` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`collateral_id` BIGINT(20) NOT NULL,
	`name` VARCHAR(50) NOT NULL,
	`description` VARCHAR(250) NULL DEFAULT NULL,
	`percentage_price` DECIMAL(19,6) NULL DEFAULT NULL,
	`absolute_price` DECIMAL(19,6) NULL DEFAULT NULL,
	`created_by` BIGINT(20) NULL DEFAULT NULL,
	`created_date` DATE NULL DEFAULT NULL,
	`updated_by` BIGINT(20) NULL DEFAULT NULL,
	`updated_date` DATE NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK1_m_collateral_quality_standards_m_collateral_type` (`collateral_id`),
	CONSTRAINT `FK1_m_collateral_quality_standards_m_collateral_type` FOREIGN KEY (`collateral_id`) REFERENCES `m_collateral_type` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `m_pledge` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NULL DEFAULT NULL,
	`loan_id` BIGINT(20) NULL DEFAULT NULL,
	`seal_number` VARCHAR(50) NULL DEFAULT NULL,
	`pledge_number` VARCHAR(50) NULL DEFAULT NULL,
	`status` TINYINT(1) NULL DEFAULT '1',
	`system_value` DECIMAL(19,6) NOT NULL,
	`user_value` DECIMAL(19,6) NOT NULL,
	`closedon_userid` BIGINT(20) NULL DEFAULT NULL,
	`closedon_date` DATE NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK1_m_pledge_m_client` (`client_id`),
	INDEX `FK_m_pledge_m_product_loan` (`loan_id`),
	INDEX `FK_m_pledge_m_appuser` (`closedon_userid`),
	CONSTRAINT `FK1_m_pledge_m_client` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
	CONSTRAINT `FK_m_pledge_m_appuser` FOREIGN KEY (`closedon_userid`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_m_pledge_m_loan` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `m_collateral_details` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`pledge_id` BIGINT(20) NULL DEFAULT NULL,
	`collateral_id` BIGINT(20) NOT NULL,
	`quality_standard_id` BIGINT(20) NOT NULL,
	`description` VARCHAR(250) NULL DEFAULT NULL,
	`gross_weight` DECIMAL(19,6) NULL DEFAULT NULL,
	`net_weight` DECIMAL(19,6) NULL DEFAULT NULL,
	`system_price` DECIMAL(19,6) NOT NULL,
	`user_price` DECIMAL(19,6) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK1_m_collateral_details_m_collateral_type` (`collateral_id`),
	INDEX `FK1_m_collateral_details_m_collateral_quality_standards` (`quality_standard_id`),
	INDEX `FK1_m_collateral_details_m_pledge` (`pledge_id`),
	CONSTRAINT `FK1_m_collateral_details_m_collateral_type` FOREIGN KEY (`collateral_id`) REFERENCES `m_collateral_type` (`id`),
	CONSTRAINT `FK1_m_collateral_details_m_pledge` FOREIGN KEY (`pledge_id`) REFERENCES `m_pledge` (`id`) ON DELETE CASCADE,
	CONSTRAINT `FK1_m_collateral_details_m_collateral_quality_standards` FOREIGN KEY (`quality_standard_id`) REFERENCES `m_collateral_quality_standards` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'CREATE_COLLATERALS', 'COLLATERALS', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'READ_COLLATERALS', 'COLLATERALS', 'READ', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'UPDATE_COLLATERALS', 'COLLATERALS', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'DELETE_COLLATERALS', 'COLLATERALS', 'DELETE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'CREATE_COLLATERALS_CHECKER', 'COLLATERALS', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'READ_COLLATERALS_CHECKER', 'COLLATERALS', 'READ', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'UPDATE_COLLATERALS_CHECKER', 'COLLATERALS', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'DELETE_COLLATERALS_CHECKER', 'COLLATERALS', 'DELETE', 0);


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'CREATE_PRODUCTCOLLATERALSMAPPING', 'PRODUCTCOLLATERALSMAPPING', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'READ_PRODUCTCOLLATERALSMAPPING', 'PRODUCTCOLLATERALSMAPPING', 'READ', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'UPDATE_PRODUCTCOLLATERALSMAPPING', 'PRODUCTCOLLATERALSMAPPING', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'DELETE_PRODUCTCOLLATERALSMAPPING', 'PRODUCTCOLLATERALSMAPPING', 'DELETE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'CREATE_PRODUCTCOLLATERALSMAPPING_CHECKER', 'PRODUCTCOLLATERALSMAPPING', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'READ_PRODUCTCOLLATERALSMAPPING_CHECKER', 'PRODUCTCOLLATERALSMAPPING', 'READ', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'UPDATE_PRODUCTCOLLATERALSMAPPING_CHECKER', 'PRODUCTCOLLATERALSMAPPING', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'DELETE_PRODUCTCOLLATERALSMAPPING_CHECKER', 'PRODUCTCOLLATERALSMAPPING', 'DELETE', 0);


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'CREATE_COLLATERALQUALITYSTANDARDS', 'COLLATERALQUALITYSTANDARDS', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'READ_COLLATERALQUALITYSTANDARDS', 'COLLATERALQUALITYSTANDARDS', 'READ', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'UPDATE_COLLATERALQUALITYSTANDARDS', 'COLLATERALQUALITYSTANDARDS', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'DELETE_COLLATERALQUALITYSTANDARDS', 'COLLATERALQUALITYSTANDARDS', 'DELETE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'CREATE_COLLATERALQUALITYSTANDARDS_CHECKER', 'COLLATERALQUALITYSTANDARDS', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'READ_COLLATERALQUALITYSTANDARDS_CHECKER', 'COLLATERALQUALITYSTANDARDS', 'READ', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'UPDATE_COLLATERALQUALITYSTANDARDS_CHECKER', 'COLLATERALQUALITYSTANDARDS', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'DELETE_COLLATERALQUALITYSTANDARDS_CHECKER', 'COLLATERALQUALITYSTANDARDS', 'DELETE', 0);


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'CREATE_COLLATERALPLEDGE', 'COLLATERALPLEDGE', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'READ_COLLATERALPLEDGE', 'COLLATERALPLEDGE', 'READ', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'UPDATE_COLLATERALPLEDGE', 'COLLATERALPLEDGE', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'DELETE_COLLATERALPLEDGE', 'COLLATERALPLEDGE', 'DELETE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'CLOSE_COLLATERALPLEDGE', 'COLLATERALPLEDGE', 'CLOSE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'CREATE_COLLATERALPLEDGE_CHECKER', 'COLLATERALPLEDGE', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'READ_COLLATERALPLEDGE_CHECKER', 'COLLATERALPLEDGE', 'READ', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'UPDATE_COLLATERALPLEDGE_CHECKER', 'COLLATERALPLEDGE', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'DELETE_COLLATERALPLEDGE_CHECKER', 'COLLATERALPLEDGE', 'DELETE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collateralmanagement', 'CLOSE_COLLATERALPLEDGE_CHECKER', 'COLLATERALPLEDGE', 'CLOSE', 0);
