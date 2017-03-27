DROP TABLE IF EXISTS `f_existing_loan_cb_payment_details`;

CREATE TABLE `f_existing_loan_cb_payment_details` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`existing_loan_id` BIGINT(20) NOT NULL,
	`date` DATE NOT NULL,
	`dpd` SMALLINT(5) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_f_existing_cb_payment_loan_id` FOREIGN KEY (`existing_loan_id` ) REFERENCES `f_existing_loan` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
