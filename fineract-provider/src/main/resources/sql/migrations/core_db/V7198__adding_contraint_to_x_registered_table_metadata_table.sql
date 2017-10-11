ALTER TABLE `x_registered_table_metadata`
	DROP FOREIGN KEY `FK_x_registered_table_metadata_x_registered_table`;
ALTER TABLE `x_registered_table_metadata`
	ADD CONSTRAINT `FK_x_registered_table_metadata_x_registered_table` FOREIGN KEY (`registered_table_id`) REFERENCES `x_registered_table` (`id`) ON DELETE CASCADE;