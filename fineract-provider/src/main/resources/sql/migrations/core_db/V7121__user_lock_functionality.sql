INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`,`can_maker_checker`) VALUES 
('authorisation', 'UNLOCK_USER', 'USER', 'UNLOCK', 0), 
('authorisation', 'UNLOCK_USER_CHECKER', 'USER', 'UNLOCK_CHECKER', 0);

ALTER TABLE `m_appuser`
	ADD COLUMN `failed_login_attempt` INT NOT NULL DEFAULT '0';

INSERT INTO `c_configuration` (`name`, `value`,`description`) VALUES ('max-login-attempts', '3', 'Max successive failed login attempt');








