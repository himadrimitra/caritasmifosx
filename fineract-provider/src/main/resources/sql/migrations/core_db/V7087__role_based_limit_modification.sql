DROP TABLE IF EXISTS `m_role_based_limit`;

CREATE TABLE `m_role_based_limit` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_approval` decimal(19,6) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

ALTER TABLE `m_role` ADD `m_role_based_limit_id` BIGINT(20) NULL DEFAULT NULL,
      ADD CONSTRAINT `m_role_m_role_based_limit` FOREIGN KEY (`m_role_based_limit_id`) REFERENCES `m_role_based_limit`(id);