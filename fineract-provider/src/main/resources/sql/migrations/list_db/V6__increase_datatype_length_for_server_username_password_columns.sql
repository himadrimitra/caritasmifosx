ALTER TABLE `tenant_server_connections`
	CHANGE COLUMN `schema_server` `schema_server` VARCHAR(256) NOT NULL DEFAULT 'localhost' AFTER `id`,
	CHANGE COLUMN `schema_username` `schema_username` VARCHAR(256) NOT NULL DEFAULT 'root' AFTER `schema_server_port`,
	CHANGE COLUMN `schema_password` `schema_password` VARCHAR(256) NOT NULL DEFAULT 'mysql' AFTER `schema_username`;