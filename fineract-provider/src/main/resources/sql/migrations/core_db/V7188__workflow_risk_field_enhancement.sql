ALTER TABLE `f_risk_field`
	ADD COLUMN `sql_query` VARCHAR(3072) NULL AFTER `is_active`,
	ADD COLUMN `input_params` VARCHAR(3072) NULL AFTER `sql_query`;