ALTER TABLE `m_appuser`
ADD COLUMN `system_user` BIT(1) NOT NULL DEFAULT b'0' AFTER `failed_login_attempt`;
update  m_appuser u 
set u.system_user = 1
where u.username = 'system';