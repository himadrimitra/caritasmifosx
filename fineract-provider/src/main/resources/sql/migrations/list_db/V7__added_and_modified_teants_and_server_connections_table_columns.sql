ALTER TABLE `tenant_server_connections`
	ADD COLUMN `server_connection_details_for_encryption` VARCHAR(500) NULL DEFAULT NULL AFTER `deadlock_max_retry_interval`,
	ADD COLUMN `is_server_connection_details_encrypted` TINYINT(1) NOT NULL DEFAULT '0' AFTER `server_connection_details_for_encryption`;
	
UPDATE tenant_server_connections ts
JOIN tenants t ON t.oltp_id = ts.id SET ts.server_connection_details_for_encryption = CONCAT(ts.schema_server, '|', ts.schema_username, '|', ts.schema_password)
WHERE t.tenant_key IS NULL;

ALTER TABLE `tenants`
	CHANGE COLUMN `tenant_key` `tenant_key` BLOB NULL DEFAULT NULL AFTER `report_id`;
	
ALTER TABLE `tenant_server_connections`
	ALTER `schema_server` DROP DEFAULT;
ALTER TABLE `tenant_server_connections`
	CHANGE COLUMN `schema_server` `schema_server` BLOB NOT NULL AFTER `id`;
	
ALTER TABLE `tenant_server_connections`
	ALTER `schema_username` DROP DEFAULT;
ALTER TABLE `tenant_server_connections`
	CHANGE COLUMN `schema_username` `schema_username` BLOB NOT NULL AFTER `schema_server_port`;
	
ALTER TABLE `tenant_server_connections`
	ALTER `schema_password` DROP DEFAULT;
ALTER TABLE `tenant_server_connections`
	CHANGE COLUMN `schema_password` `schema_password` BLOB NOT NULL AFTER `schema_username`;