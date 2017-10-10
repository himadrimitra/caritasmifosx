ALTER TABLE `f_task`
	ADD COLUMN `image_id` BIGINT(20) NULL DEFAULT NULL AFTER `due_time`,
	ADD CONSTRAINT `FK_f_image_image_id` FOREIGN KEY (`image_id`) REFERENCES `m_image` (`id`);