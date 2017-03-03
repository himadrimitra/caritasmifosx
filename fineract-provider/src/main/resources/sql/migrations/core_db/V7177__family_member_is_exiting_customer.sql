ALTER TABLE `f_family_details`
	ADD COLUMN `client_reference` BIGINT(20) NULL AFTER `lastmodified_date`;
	
	ALTER TABLE `f_family_details`
	ADD CONSTRAINT `FK_f_family_details_m_client` FOREIGN KEY (`client_reference`) REFERENCES `m_client` (`id`);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'DELETE_FAMILYMEMBERASSOCIATION', 'FAMILYMEMBERASSOCIATION', 'DELETE', 0);


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'DELETE_FAMILYMEMBERASSOCIATION_CHECKER', 'FAMILYMEMBERASSOCIATION', 'DELETE_CHECKER', 0);
