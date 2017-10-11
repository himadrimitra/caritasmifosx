
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
	('externalservices', 'UPDATE_OTHER_EXTERNAL_SERVICES_PROPERTIES', 'OTHER_EXTERNAL_SERVICES_PROPERTIES', 'UPDATE', 0);

ALTER TABLE f_bank_account_transaction add column internal_reference_id varchar(32) default null;

update f_bank_account_transaction set internal_reference_id = id;

INSERT IGNORE INTO `c_configuration` (`name`,`value`)
	VALUES ('mask-regex', '\\w(?=.{4})'),
			 ('mask-replacechar', 'x');