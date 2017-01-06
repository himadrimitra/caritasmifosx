ALTER TABLE `m_entity_to_entity_mapping`
	ADD COLUMN `allowed_for_child_offices` TINYINT(1) NOT NULL DEFAULT 0 AFTER `end_date`;