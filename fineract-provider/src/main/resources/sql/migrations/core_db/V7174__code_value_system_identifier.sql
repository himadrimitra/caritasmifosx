SET @preparedStatement = (SELECT IF(
(SELECT COUNT(*)
FROM INFORMATION_SCHEMA.COLUMNS
WHERE  table_name = 'm_code_value'
AND table_schema = DATABASE()
AND column_name = 'system_identifier'
) > 0,
"SELECT 1",
"ALTER TABLE `m_code_value` ADD `system_identifier` VARCHAR(3) NULL;"
));

PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

UPDATE m_code_value SET system_identifier = 'PAS' WHERE code_value = 'Passport';
UPDATE m_code_value SET system_identifier = 'VDL' WHERE code_value = 'Drivers License';
UPDATE m_code_value SET system_identifier = 'VID' WHERE code_value = 'Voter Id';

INSERT INTO m_code_value (code_id, code_value, order_position, is_active, is_mandatory, system_identifier)
select * from (select 1 as code_id, 'Aadhaar' as code_value, 0 as order_position, 0 as is_active, 0 as is_mandatory, 'UID' as system_identifier) as tmp
WHERE NOT EXISTS (
SELECT system_identifier FROM m_code_value WHERE system_identifier = 'UID'
) LIMIT 1;

INSERT INTO m_code_value (code_id, code_value, order_position, is_active, is_mandatory, system_identifier)
select * from (select 1 as code_id, 'PAN' as code_value, 0 as order_position, 0 as is_active, 0 as is_mandatory, 'PAN' as system_identifier) as tmp
WHERE NOT EXISTS (
SELECT system_identifier FROM m_code_value WHERE system_identifier = 'PAN'
) LIMIT 1;

INSERT INTO m_code_value (code_id, code_value, order_position, is_active, is_mandatory, system_identifier)
select * from (select 1 as code_id, 'Passport' as code_value, 0 as order_position, 0 as is_active, 0 as is_mandatory, 'PAS' as system_identifier) as tmp
WHERE NOT EXISTS (
SELECT system_identifier FROM m_code_value WHERE system_identifier = 'PAS'
) LIMIT 1;

INSERT INTO m_code_value (code_id, code_value, order_position, is_active, is_mandatory, system_identifier)
select * from (select 1 as code_id, 'Drivers License' as code_value, 0 as order_position, 0 as is_active, 0 as is_mandatory, 'VDL' as system_identifier) as tmp
WHERE NOT EXISTS (
SELECT system_identifier FROM m_code_value WHERE system_identifier = 'VDL'
) LIMIT 1;

INSERT INTO m_code_value (code_id, code_value, order_position, is_active, is_mandatory, system_identifier)
select * from (select 1 as code_id, 'Voter Id' as code_value, 0 as order_position, 0 as is_active, 0 as is_mandatory, 'VID' as system_identifier) as tmp
WHERE NOT EXISTS (
SELECT system_identifier FROM m_code_value WHERE system_identifier = 'VID'
) LIMIT 1;

