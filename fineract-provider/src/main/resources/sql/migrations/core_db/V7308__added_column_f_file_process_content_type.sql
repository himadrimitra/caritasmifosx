ALTER TABLE `f_file_process`
	ADD COLUMN `content_type` VARCHAR(500) NOT NULL AFTER `file_type`;