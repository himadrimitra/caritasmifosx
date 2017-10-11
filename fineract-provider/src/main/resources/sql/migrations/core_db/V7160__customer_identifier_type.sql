ALTER TABLE `f_transaction_authentication`
	ADD COLUMN `identifier_type_id` INT(11) NOT NULL,
	ADD FOREIGN KEY (`identifier_type_id`) REFERENCES `m_code_value` (`id`);