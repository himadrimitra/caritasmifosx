ALTER TABLE `f_charge_slab`
MODIFY COLUMN `charge_id` BIGINT(20) NULL DEFAULT NULL,
ADD COLUMN  `parent_id` BIGINT(20) NULL DEFAULT NULL,
ADD COLUMN  `type` SMALLINT(5) NOT NULL DEFAULT '1';