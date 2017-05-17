CREATE TABLE `f_savings_product_drawing_power_details` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`product_id` BIGINT(20) NOT NULL,
	`frequency_type_enum` SMALLINT(5) NOT NULL,
	`frequency_interval` SMALLINT(3) NOT NULL,
	`frequency_nth_day_enum` SMALLINT(5) NULL DEFAULT NULL,
	`frequency_day_of_week_type_enum` SMALLINT(5) NULL DEFAULT NULL,
	`frequency_on_day` SMALLINT(3) NULL DEFAULT NULL, PRIMARY KEY (`id`), INDEX `FK_savings_product_id_drawing_power_details` (`product_id`), CONSTRAINT `FK_savings_product_id_drawing_power_details` FOREIGN KEY (`product_id`) REFERENCES `m_savings_product` (`id`)
); 

ALTER TABLE `f_savings_account_dp_details` ADD COLUMN `start_date` DATE NULL AFTER `amount_or_percentage`;

UPDATE f_savings_account_dp_details sadp
JOIN m_savings_account sa ON sadp.savings_id = sa.id SET sadp.start_date = sa.activatedon_date; 

ALTER TABLE `f_savings_account_dp_details`
	ALTER `start_date` DROP DEFAULT;
ALTER TABLE `f_savings_account_dp_details`
	CHANGE COLUMN `start_date` `start_date` DATE NOT NULL AFTER `amount_or_percentage`;

ALTER TABLE `m_calendar` ADD COLUMN `temp_entity_id` BIGINT(20) NULL DEFAULT NULL AFTER `meeting_time`;

INSERT INTO m_calendar (title,start_date,calendar_type_enum,repeating,recurrence,temp_entity_id)
SELECT CONCAT ('savings_account_', dp.id,'_dp_details'), 
sa.activatedon_date, 
1,
dp.dp_reduction_every, CONCAT ('FREQ=', CASE WHEN dp.frequency = 0 THEN 'DAILY' WHEN dp.frequency = 1 THEN 'WEEKLY' WHEN dp.frequency = 2 THEN 'MONTHLY' WHEN dp.frequency = 3 THEN 'YEARLY' END, CASE WHEN dp.dp_reduction_every > 1 THEN CONCAT(';INTERVAL=',dp.dp_reduction_every) END
),
dp.savings_id
FROM f_savings_account_dp_details dp
JOIN m_savings_account sa ON dp.savings_id = sa.id
WHERE sa.activatedon_date IS NOT NULL;

INSERT INTO m_calendar_instance (calendar_id,entity_id,entity_type_enum)
SELECT
c.id,
c.temp_entity_id,
9
FROM m_calendar c
WHERE c.temp_entity_id IS NOT NULL; 

ALTER TABLE `f_savings_account_dp_details`
	DROP COLUMN `frequency`,
	DROP COLUMN `dp_reduction_every`;
	
ALTER TABLE `m_calendar`
	DROP COLUMN `temp_entity_id`;