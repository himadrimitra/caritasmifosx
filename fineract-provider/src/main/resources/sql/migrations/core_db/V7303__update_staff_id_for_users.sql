INSERT INTO `m_staff` (`is_loan_officer`, `office_id`, `firstname`, `lastname`, `display_name`, `external_id`, `is_active`) SELECT 0 AS is_loan_officer, au.office_id, au.firstname, au.lastname, CONCAT(au.lastname,', ', au.firstname) AS displayName, CONCAT(au.username,au.id) AS externalId, 1 AS is_active FROM m_appuser au WHERE au.staff_id IS NULL AND au.is_deleted = 0;

UPDATE m_appuser au JOIN m_staff s ON s.external_id = CONCAT(au.username,au.id) SET au.staff_id = s.id, s.external_id = NULL WHERE au.staff_id IS NULL;
