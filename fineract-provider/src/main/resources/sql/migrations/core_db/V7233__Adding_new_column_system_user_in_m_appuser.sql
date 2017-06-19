ALTER TABLE `m_appuser`
	ADD COLUMN `system_user` TINYINT(1) NOT NULL DEFAULT 0 AFTER `failed_login_attempt`;
	
UPDATE `m_appuser` SET `system_user`= 1 WHERE username = 'system';
