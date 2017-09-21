ALTER TABLE `m_portfolio_command_source`
CHANGE COLUMN `command_as_json` `command_as_json` LONGTEXT NOT NULL AFTER `subresource_id`;