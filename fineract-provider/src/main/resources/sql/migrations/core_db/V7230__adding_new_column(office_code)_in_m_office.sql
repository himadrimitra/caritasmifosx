ALTER TABLE `m_office`
ADD COLUMN `office_code` VARCHAR(5) NULL AFTER `opening_date`;
UPDATE m_office mo
SET mo.office_code = mo.id;
