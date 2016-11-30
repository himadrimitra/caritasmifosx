ALTER TABLE `c_external_service_properties`
	ADD UNIQUE INDEX `UQ_c_external_service_properties` (`name`, `value`, `external_service_id`);
