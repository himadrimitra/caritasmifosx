/*	First remove one to one relationship between role and m_role_based_limit*/
ALTER TABLE `m_role`
  DROP FOREIGN KEY `m_role_m_role_based_limit`;

ALTER TABLE `m_role`
  DROP COLUMN `m_role_based_limit_id`;


/*	Delete all existing entries in role based limits	*/
TRUNCATE TABLE `m_role_based_limit` ;


/* m_role_based_limit now has a Mandatory foreign key to m_currency*/
ALTER TABLE `m_role_based_limit`
	ADD COLUMN `currency_id` BIGINT(20) NOT NULL AFTER `id` ;

ALTER TABLE `m_role_based_limit`
	ADD CONSTRAINT `m_role_based_limit_m_currency` FOREIGN KEY (`currency_id`) REFERENCES `m_currency` (`id`);


/* m_role_based_limit now has a Mandatory foreign key to m_role*/
ALTER TABLE `m_role_based_limit`
	ADD COLUMN `role_id` BIGINT(20) NOT NULL AFTER `id` ;

ALTER TABLE `m_role_based_limit`
	ADD CONSTRAINT `m_role_based_limit_m_role` FOREIGN KEY (`role_id`) REFERENCES `m_role` (`id`);

/* Rename column loan_approval and make it Non Nullable */
ALTER TABLE `m_role_based_limit` CHANGE COLUMN `loan_approval` `max_loan_approval_amount` DECIMAL(19,6) NOT NULL;

/* Create Unique constraint across role_id and currency ID*/
ALTER TABLE `m_role_based_limit` ADD UNIQUE `unique_limit`(`role_id`, `currency_id`);
