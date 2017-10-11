CREATE TABLE `f_creditbureau_loanproduct_office_mapping` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`credit_bureau_loan_product_mapping_id` BIGINT(20) NOT NULL,
	`loan_product_id` BIGINT(20) NULL DEFAULT NULL,
	`office_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `uk_loanproduct_office_mapping` (`loan_product_id`, `office_id`),
	INDEX `fk_credit_bureau_loan_product_mapping_id` (`credit_bureau_loan_product_mapping_id`),
	INDEX `fk_office_id` (`office_id`),
	CONSTRAINT `fk_credit_bureau_loan_product_mapping_id` FOREIGN KEY (`credit_bureau_loan_product_mapping_id`) REFERENCES `f_creditbureau_loanproduct_mapping` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT `fk_loan_productid` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT `fk_office_id` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
)
ENGINE=InnoDB
;


INSERT INTO f_creditbureau_loanproduct_office_mapping (credit_bureau_loan_product_mapping_id, loan_product_id)
SELECT id, loan_product_id
FROM f_creditbureau_loanproduct_mapping
ORDER BY f_creditbureau_loanproduct_mapping.id;



ALTER TABLE `f_creditbureau_loanproduct_mapping`
	DROP FOREIGN KEY `fk_f_creditbureau_loanproduct_mapping_loan_product_id`;
	
ALTER TABLE `f_creditbureau_loanproduct_mapping`
	DROP INDEX `fk_f_creditbureau_loanproduct_mapping_loan_product_id`;

ALTER TABLE `f_creditbureau_loanproduct_mapping`
	DROP FOREIGN KEY `fk_f_creditbureau_loanproduct_mapping_product_id`;
	
ALTER TABLE `f_creditbureau_loanproduct_mapping`
	DROP INDEX `uk_f_creditbureau_loanproduct_mapping`;

ALTER TABLE `f_creditbureau_loanproduct_mapping`
	DROP COLUMN `loan_product_id`;
	
ALTER TABLE `f_creditbureau_loanproduct_mapping`
	ADD CONSTRAINT `fk_f_creditbureau_loanproduct_mapping_product_id` FOREIGN KEY (`creditbureau_product_id`) REFERENCES `f_creditbureau_product` (`id`);