ALTER TABLE `m_product_loan` 
	ADD COLUMN `adjust_first_emi_amount` TINYINT(1) NOT NULL DEFAULT '0',
	ADD COLUMN `adjusted_instalment_in_multiples_of`  INT(4) NULL DEFAULT NULL;
	
ALTER TABLE `m_loan`
	ADD COLUMN `first_emi_amount` DECIMAL(19,6) NULL DEFAULT NULL;	
	
INSERT INTO `c_configuration` (`name`, `value`, `enabled`, `is_trap_door`, `description`) VALUES ('adjusted-amount-rounding-mode', 6, 1, 1, '0 - UP, 1 - DOWN, 2- CEILING, 3- FLOOR, 4- HALF_UP, 5- HALF_DOWN, 6 - HALF_EVEN');
