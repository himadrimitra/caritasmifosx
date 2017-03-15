ALTER TABLE `m_loan` ADD COLUMN `glim_payment_as_group` TINYINT(1) NOT NULL DEFAULT '0';
INSERT INTO `c_configuration` (`name`, `value`, `date_value`, `enabled`, `is_trap_door`, `description`) VALUES ('glim-payment-as-group', NULL, NULL, 0, 0, 'glim payment as only group level after disbursal');
